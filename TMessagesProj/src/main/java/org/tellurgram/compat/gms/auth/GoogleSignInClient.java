package org.tellurgram.compat.gms.auth;

import android.content.Intent;
import org.tellurgram.compat.gms.tasks.Task;

/** Stub — Google Sign-In removed in FOSS builds. */
public class GoogleSignInClient {
    public Task<Void> signOut() { return new Task<Void>() {}; }
    public Intent getSignInIntent() { return new Intent(); }
}
