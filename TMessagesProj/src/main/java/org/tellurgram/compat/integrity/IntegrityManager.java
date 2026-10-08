package org.tellurgram.compat.integrity;

import org.tellurgram.compat.gms.tasks.Task;

/** Stub — Play Integrity removed in FOSS builds. */
public class IntegrityManager {
    public Task<IntegrityTokenResponse> requestIntegrityToken(IntegrityTokenRequest request) {
        return new Task<IntegrityTokenResponse>() {};
    }
}
