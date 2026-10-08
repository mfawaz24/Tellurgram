# AIDL service entry point — keep stub class + methods callable across processes.
-keep class org.tellurgram.plugin.tor.IMgTorService { *; }
-keep class org.tellurgram.plugin.tor.IMgTorService$* { *; }
-keep class org.tellurgram.plugin.tor.IMgTorCallback { *; }
-keep class org.tellurgram.plugin.tor.IMgTorCallback$* { *; }
-keep class org.tellurgram.plugin.tor.MgTorService { *; }

# JNI bridge — native methods + their declaring class must survive R8.
-keepclasseswithmembernames class org.tellurgram.plugin.tor.MgTorNative {
    native <methods>;
}
-keep class org.tellurgram.plugin.tor.MgTorNative { *; }
