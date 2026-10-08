package org.tellurgram.compat.gms.auth;

import android.content.Context;
import android.content.Intent;
import org.tellurgram.compat.gms.tasks.Task;

/** Stub — Google Sign-In removed in FOSS builds. */
public class GoogleSignIn {
    public static Task<GoogleSignInAccount> getSignedInAccountFromIntent(Intent data) {
        return new Task<GoogleSignInAccount>() {};
    }
    public static GoogleSignInClient getClient(Context context, GoogleSignInOptions options) {
        return new GoogleSignInClient();
    }
}
