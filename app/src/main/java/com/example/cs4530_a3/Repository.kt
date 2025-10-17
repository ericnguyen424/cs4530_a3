package com.example.cs4530_a3

import com.example.cs4530_a3.room.CourseDao
import com.example.cs4530_a3.room.CourseEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class Repository (val scope: CoroutineScope, private val dao: CourseDao) {

    val allCourses: Flow<List<CourseEntity>> = dao.getAllCourses()

    fun addCourse(courseNumber: String, department: String, location: String, courseDetails : String) {
        scope.launch {
            val courseObj = CourseEntity(courseNumber, department, location, courseDetails)
            dao.addCourse(courseObj)
        }
    }

    fun deleteCourse(courseID: Int) {
        scope.launch {
            dao.deleteCourseById(courseID)
        }
    }

    suspend fun getCourse(courseID: Int): CourseEntity {
        return dao.getCourse(courseID)
    }
}