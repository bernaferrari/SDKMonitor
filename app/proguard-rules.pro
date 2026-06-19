# Modern Android App ProGuard Rules
# Optimized for Jetpack Compose and Hilt

# Print out the full proguard config used for every build
-printconfiguration build/outputs/fullProguardConfig.pro

# Kotlin metadata and reflection
-keep class kotlin.Metadata { *; }

# Keep annotation classes
-dontwarn javax.annotation.**
-dontwarn javax.lang.model.**
-dontwarn com.google.j2objc.annotations.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

# App Functions
-keep class * extends androidx.appfunctions.service.AppFunctionService { *; }
-keep @androidx.appfunctions.service.AppFunction class * { *; }
-keep @androidx.appfunctions.AppFunctionSerializable class * { *; }