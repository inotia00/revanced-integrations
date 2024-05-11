-dontobfuscate
-dontoptimize
-keepattributes *
-keep class app.revanced.** {
  *;
}
-keep class com.google.** {
  *;
}
-keep class org.schabi.newpipe.extractor.timeago.patterns.** {
  *;
}
-keep class org.mozilla.javascript.** {
  *;
}
-keep class org.mozilla.classfile.ClassFileWriter
-dontwarn java.awt.**
-dontwarn javax.swing.**
-dontwarn org.mozilla.javascript.tools.**
-dontwarn java.beans.BeanDescriptor
-dontwarn java.beans.BeanInfo
-dontwarn java.beans.IntrospectionException
-dontwarn java.beans.Introspector
-dontwarn java.beans.PropertyDescriptor
