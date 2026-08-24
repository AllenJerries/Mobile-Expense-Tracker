# Keep kotlinx.serialization generated serializers for backup payloads.
-keepclassmembers class com.jerries.expense.core.backup.** {
    *** Companion;
}
-keepclasseswithmembers class com.jerries.expense.core.backup.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep Room entities' generated code (usually retained automatically, defensive).
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**
