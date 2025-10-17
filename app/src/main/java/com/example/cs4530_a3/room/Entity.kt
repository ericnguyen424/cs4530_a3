package com.example.cs4530_a3.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class CourseEntity(val courseNumber: String, val department: String, val location: String, val courseDetails : String,
                      @PrimaryKey(autoGenerate = true) val id:Int=0 )

