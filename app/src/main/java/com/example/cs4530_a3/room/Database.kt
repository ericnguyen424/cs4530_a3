package com.example.cs4530_a3.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CourseEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun courseDao(): CourseDao
}
