package com.example.cs4530_a3.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlin.reflect.KClass

@Dao
interface CourseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCourse (course: CourseEntity)

    @Query("DELETE FROM courses WHERE id = :courseID")
    suspend fun deleteCourseById(courseID: Int)

    @Query("SELECT * FROM courses WHERE id = :courseID")
    suspend fun getCourse(courseID: Int): CourseEntity

    @Query("select * from courses order by id desc")
    fun getAllCourses(): Flow<List<CourseEntity>>


}


