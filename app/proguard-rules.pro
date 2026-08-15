# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

-keepattributes SourceFile,LineNumberTable,Signature,InnerClasses,EnclosingMethod
-renamesourcefileattribute SourceFile

# Room & SQLite Persistence
-keep class androidx.room.** { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-keep @androidx.room.TypeConverter class * { *; }
-keep @androidx.room.Database class * { *; }
-keep class * extends androidx.room.RoomDatabase { *; }
-keep class com.example.data.** { *; }
-keep class com.example.data.calendar.** { *; }
-keep class com.example.data.prayers.** { *; }

# KSP / Room implementation classes
-keep class *_Impl { *; }

# Support for @Keep annotation
-keep @androidx.annotation.Keep class * { *; }
-keepclassmembers class * {
    @androidx.annotation.Keep <methods>;
    @androidx.annotation.Keep <fields>;
}

# Application Models, Utils & Notifications
-keep class com.example.ui.** { *; }
-keep class com.example.util.** { *; }
-keep class com.example.utils.** { *; }
-keep class com.example.notifications.** { *; }

# Moshi / JSON Serialization
-keepattributes *Annotation*
-keepclassmembers class * {
    @com.squareup.moshi.* <fields>;
    @com.squareup.moshi.* <methods>;
}
-dontwarn com.squareup.moshi.**

# Media3 & ExoPlayer
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# WorkManager
-keep class androidx.work.** { *; }
-keep class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}
-keep class com.example.worker.** { *; }

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# Kotlin Serialization
-dontnote kotlinx.serialization.AnnotationsKt
-keep,allowoptimization class kotlinx.serialization.** { *; }
-dontwarn kotlinx.serialization.**
-keepclassmembers class kotlinx.serialization.** { *; }

# Jetpack Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**
