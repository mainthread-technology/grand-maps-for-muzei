# Retrofit, OkHttp, Gson
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn rx.**
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn  de.psdev.licensesdialog.*

# Moshi
-keep class com.squareup.moshi.** { *; }
-keep interface com.squareup.moshi.** { *; }
-dontwarn com.squareup.moshi.**

# Application classes that will be serialized/deserialized over Gson
-keep class technology.mainthread.apps.grandmaps.data.GrandMapsApi { *; }
-keep class technology.mainthread.apps.grandmaps.data.model.GrandMapsResponse { *; }