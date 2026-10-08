package org.tellurgram.compat.gms.safetynet;

import org.tellurgram.compat.gms.tasks.Task;

/** Stub — SafetyNet removed in FOSS builds. */
public class SafetyNetClient {
    public Task<SafetyNetResponse> attest(byte[] nonce, String apiKey) {
        return new Task<SafetyNetResponse>() {};
    }
}
