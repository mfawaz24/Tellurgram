#!/usr/bin/env bash
# Merges a rewritten Mercurygram history into the current branch.
#
# Mercurygram keeps its changes as a patch stack and rebases it onto every
# Telegram release, so the mirror we merged last is no longer an ancestor of
# the new one. A plain merge then sees the whole stack as new and conflicts
# almost everywhere. Instead, this applies only the difference between the old
# and new mirror and records the new mirror as the second parent, so later
# merges and the mg-upstream pull request into prod see it as merged.
#
# Usage: scripts/mg-rewrite-merge.sh [old-mirror] <new-mirror>
#   old-mirror defaults to the last mirror merged into HEAD (see last_mirror).
#
# Exit codes: 0 merged and committed, 2 conflicts left in the work tree to
# resolve (then `git commit`), 1 anything else.

set -euo pipefail

# Upstream package path -> Tellurgram package path.
map_paths() { sed -E 's#it/belloworld/mercurygram/#org/tellurgram/#g'; }

# Second parent of the newest merge that brought a Mercurygram mirror in:
# manual or automated merges ("Merge|Record Mercurygram <sha> ...") and
# merged mg-upstream pull requests.
last_mirror() {
  # grep -m1 closes the pipe early; don't let git's SIGPIPE fail pipefail.
  { git log --merges --format='%P %s' "$@" || :; } |
    grep -m1 -E '^[0-9a-f]+ [0-9a-f]+ ((Merge|Record) Mercurygram [0-9a-f]{7,}|Merge pull request .* from [^ ]+/mg-upstream$)' |
    cut -d' ' -f2
}

if [ $# -eq 1 ]; then
  new=$1
  old=$(last_mirror HEAD)
  [ -n "$old" ] || { echo "No earlier Mercurygram merge found in HEAD" >&2; exit 1; }
elif [ $# -eq 2 ]; then
  old=$1 new=$2
else
  sed -n '2,/^$/p' "$0" | sed 's/^# \{0,1\}//' >&2
  exit 1
fi

git diff --quiet HEAD && git diff --cached --quiet HEAD ||
  { echo "Work tree is not clean" >&2; exit 1; }

old_short=$(git rev-parse --short "$old")
new_short=$(git rev-parse --short "$new")
work=$(mktemp -d)
trap 'rm -rf "$work"' EXIT

# Leave out changes to files Tellurgram deleted (e.g. the F-Droid recipe);
# added files are still applied.
excludes=()
while IFS=$'\t' read -r status path; do
  case $status in A) continue ;; esac
  path=$(printf '%s\n' "$path" | map_paths)
  if ! git cat-file -e "HEAD:$path" 2>/dev/null; then
    echo "Skipping $path: not in HEAD"
    excludes+=("--exclude=$path")
  fi
done < <(git diff --no-renames --name-status "$old" "$new")

git diff --no-renames --binary "$old" "$new" |
  sed -E '/^(diff --git |--- |\+\+\+ )/ s#it/belloworld/mercurygram/#org/tellurgram/#g' \
  > "$work/mg.diff"

git merge -q --no-ff --no-commit -s ours "$new" >/dev/null 2>&1
git apply --3way --index "${excludes[@]}" "$work/mg.diff" 2>"$work/apply.err" || true

# Every Telegram update bumps the version next to APP_PACKAGE, which always
# conflicts: keep Tellurgram's side of each conflict, then take upstream's
# version.
if git diff --name-only --diff-filter=U | grep -qx gradle.properties; then
  code=$(git show "$new:gradle.properties" | sed -n 's/^APP_VERSION_CODE=//p')
  name=$(git show "$new:gradle.properties" | sed -n 's/^APP_VERSION_NAME=//p')
  awk '/^<<<<<<< /{s=1; next} /^=======$/ && s==1{s=2; next} /^>>>>>>> /{s=0; next} s!=2' \
    gradle.properties | sed -E "s/^APP_VERSION_CODE=.*/APP_VERSION_CODE=${code}/; s/^APP_VERSION_NAME=.*/APP_VERSION_NAME=${name}/" \
    > "$work/gradle.properties"
  cp "$work/gradle.properties" gradle.properties
  git add gradle.properties
fi

# git apply doesn't always record a conflict in the index (it didn't for
# .gitmodules once), so also look for markers in the files the patch touched.
conflicts=$( {
  git diff --name-only --diff-filter=U
  git diff --ignore-submodules --name-only --diff-filter=AMU HEAD |
    while IFS= read -r f; do
      if [ -f "$f" ] && grep -qE '^(<<<<<<< ours|>>>>>>> theirs)$' "$f"; then printf '%s\n' "$f"; fi
    done
} | sort -u)
if [ -n "$conflicts" ]; then
  echo "Conflicts applying Mercurygram ${old_short}..${new_short}:"
  printf '%s\n' "$conflicts" | sed 's/^/- /'
  exit 2
fi
if grep -q '^error:' "$work/apply.err"; then
  cat "$work/apply.err" >&2
  git merge --abort
  exit 1
fi

version=$(git show "$new:gradle.properties" | sed -n 's/^APP_VERSION_NAME=//p')
git commit -q -F - <<EOF
Merge Mercurygram ${new_short}${version:+ (Telegram ${version})} into $(git rev-parse --abbrev-ref HEAD)

Mercurygram rewrote its history, so this applies only the difference
between the old mirror ${old_short} and ${new_short}, with the new mirror
recorded as the second parent.
EOF
echo "Merged Mercurygram ${old_short}..${new_short}"
