# AIDL service entry point — keep stub class + methods callable across processes.
-keep class it.belloworld.tellurgram.plugin.tor.IMgTorService { *; }
-keep class it.belloworld.tellurgram.plugin.tor.IMgTorService$* { *; }
-keep class it.belloworld.tellurgram.plugin.tor.IMgTorCallback { *; }
-keep class it.belloworld.tellurgram.plugin.tor.IMgTorCallback$* { *; }
-keep class it.belloworld.tellurgram.plugin.tor.MgTorService { *; }

# JNI bridge — native methods + their declaring class must survive R8.
-keepclasseswithmembernames class it.belloworld.tellurgram.plugin.tor.MgTorNative {
    native <methods>;
}
-keep class it.belloworld.tellurgram.plugin.tor.MgTorNative { *; }
