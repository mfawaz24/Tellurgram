package it.belloworld.tellurgram;

import android.net.Uri;
import android.text.TextUtils;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;

import org.json.JSONObject;
import org.telegram.messenger.SharedConfig;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

/**
 * Upgrades cleartext http:// to https:// when {@link SharedConfig#mg_enforceHttps}
 * is on.
 *
 * Android only reads the cleartext policy from the manifest, and the app has
 * to keep {@code usesCleartextTraffic="true"} so the toggle can be turned off,
 * so the policy is applied here instead, at the places where a URL leaves the
 * app: opening links, web views, direct downloads and the video player.
 * Every entry point returns its input unchanged when the toggle is off.
 *
 * Loopback addresses are left alone: nothing sent to them crosses the network.
 */
public class MgHttps {

    public static boolean enabled() {
        return SharedConfig.mg_enforceHttps;
    }

    private static boolean isCleartext(Uri uri) {
        if (uri == null || !"http".equalsIgnoreCase(uri.getScheme())) {
            return false;
        }
        String host = uri.getHost();
        return !("localhost".equalsIgnoreCase(host) || "127.0.0.1".equals(host) || "[::1]".equals(host) || "::1".equals(host));
    }

    public static Uri upgrade(Uri uri) {
        if (!enabled() || !isCleartext(uri)) {
            return uri;
        }
        String authority = uri.getEncodedAuthority();
        // An explicit :80 is the http port, not the site's https port.
        if (authority != null && uri.getPort() == 80) {
            authority = authority.substring(0, authority.lastIndexOf(':'));
        }
        return uri.buildUpon().scheme("https").encodedAuthority(authority).build();
    }

    public static String upgrade(String url) {
        if (!enabled() || url == null || url.length() < 7 || !url.regionMatches(true, 0, "http:", 0, 5)) {
            return url;
        }
        return upgrade(Uri.parse(url)).toString();
    }

    /**
     * For {@code WebViewClient.shouldInterceptRequest}: returns null to let the
     * request through. A cleartext page load is answered with a page that
     * moves to the https address; any other cleartext request is refused.
     */
    public static WebResourceResponse intercept(WebResourceRequest request) {
        if (!enabled() || request == null || !isCleartext(request.getUrl())) {
            return null;
        }
        if (request.isForMainFrame()) {
            String target = upgrade(request.getUrl()).toString();
            String html = "<!DOCTYPE html><meta name=\"referrer\" content=\"no-referrer\">"
                    + "<meta http-equiv=\"refresh\" content=\"0;url=" + TextUtils.htmlEncode(target) + "\">"
                    + "<script>location.replace(" + JSONObject.quote(target) + ")</script>";
            return new WebResourceResponse("text/html", "utf-8", 200, "OK", Collections.emptyMap(),
                    new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8)));
        }
        return new WebResourceResponse("text/plain", "utf-8", 403, "Forbidden", Collections.emptyMap(),
                new ByteArrayInputStream(new byte[0]));
    }
}
