package it.belloworld.tellurgram.compat.gms.wearable;

/** Stub MessageEvent — GMS Wearable removed in FOSS builds. */
public interface MessageEvent {

    String getPath();

    String getSourceNodeId();

    byte[] getData();
}
