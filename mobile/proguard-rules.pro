# Retrofit, OkHttp, Gson
-keepattributes *Annotation*
-keepattributes Signature
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**
-dontwarn rx.**
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}
-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn  de.psdev.licensesdialog.*

# Application classes that will be serialized/deserialized over Gson
-keep class technology.mainthread.apps.grandmaps.data.GrandMapsApi { *; }
-keep class technology.mainthread.apps.grandmaps.data.GrandMapsResponse { *; }