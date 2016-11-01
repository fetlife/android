# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\_Development\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-dontwarn org.w3c.dom.bootstrap.DOMImplementationRegistry
-dontwarn sun.misc.**
-dontwarn okio.**
-dontwarn retrofit.Platform$Java8
-dontwarn org.joda.convert.**
-dontwarn org.joda.time.**

-keep class org.joda.time.** { *; }
-keep interface org.joda.time.** { *;}

-keep class org.codehaus.** { *; }

-keepattributes Annotation,EnclosingMethod

-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}