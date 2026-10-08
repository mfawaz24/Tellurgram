package org.tellurgram.compat.gms.wearable;

import org.tellurgram.compat.gms.tasks.Task;

/** Stub MessageClient — GMS Wearable removed in FOSS builds. */
public class MessageClient {

    public Task<Integer> sendMessage(String nodeId, String path, byte[] data) {
        return new Task<Integer>() { };
    }
}
