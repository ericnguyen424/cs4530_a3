package com.example.cs4530_a3

import android.app.Application
import android.os.Bundle
import android.view.Display
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cs4530_a3.room.CourseEntity
import com.example.cs4530_a3.ui.theme.Cs4530_a3Theme
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * This class holds the VM and the Repository
 */
class VMM(application: Application) : AndroidViewModel(application) {

    // Model (using Repository)
    val dao=(application as CourseApp).repository

    // Converting the Flow to StateFlow for UI Compose
    val dataReadOnly: StateFlow<List<CourseEntity>> = dao.allCourses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Adds new course
    fun addCourse(courseNumber: String, department: String, location: String, courseDetails : String)
    {
        dao.addCourse(courseNumber, department, location, courseDetails)
    }

    fun removeCourse(courseID: Int) {
        dao.deleteCourse(courseID)
    }

    suspend fun getCourse(courseID: Int): CourseEntity {

        return dao.getCourse(courseID)
    }

}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Cs4530_a3Theme {
                val vm:VMM = viewModel()
                Content(vm)
            }
        }
    }
}

@Composable
fun Content(vm : VMM) {
    // This outermost column holds all the content
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Courses
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            val courseList by vm.dataReadOnly.collectAsState(emptyList())
            var showPopup by remember { mutableStateOf(false)}
            var currentCourse: Int by remember{ mutableStateOf(0)}
            var course: CourseEntity? by remember { mutableStateOf(null) }

            if (showPopup){

                // Display the data async
                LaunchedEffect(Unit) {
                    course = vm.getCourse(currentCourse)
                }
            }

            // If course isn't null call display info
            course?.let { DisplayInfo(it, {course = null;showPopup = false}) }

            // Lazy Column for the course list
            LazyColumn (
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.Top
            ){
                items(courseList) { course ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.LightGray)
                            .padding(8.dp),

                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Button(
                            onClick = {
                                currentCourse = course.id
                                showPopup = true
                            },
                        ) {
                            Text(text = "${course.department + course.courseNumber} at ${course.location}", color = Color.White)
                        }

                        Row {
                            RemoveCourse(vm, course.id)
                        }

                    }

                }
            }

            // Add Course
            var courseNumber by remember { mutableStateOf("") }
            var department by remember { mutableStateOf("") }
            var location by remember { mutableStateOf("") }
            var details by remember { mutableStateOf("") }

            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ){
                // CourseNumber field
                OutlinedTextField(
                    value = courseNumber,
                    onValueChange = {
                        // Only allow int inputs
                        if (it.all { it.isDigit() }) {
                            courseNumber = it
                        }
                    },
                    label = { Text("Course Number") },
                )

                // department field
                OutlinedTextField(
                    value = department,
                    onValueChange = {
                        department = it
                    },
                    label = { Text("Department") }
                )

                // location field
                OutlinedTextField(
                    value = location,
                    onValueChange = {
                        location = it
                    },
                    label = { Text("Location") }
                )

                // Details field
                OutlinedTextField(
                    value = details,
                    onValueChange = {
                        details = it
                    },
                    label = { Text("Course Details") }
                )

                // Submit
                Button(
                    onClick = {
                        if (courseNumber.isEmpty() || location.isEmpty() || details.isEmpty() || department.isEmpty()) {
                            return@Button
                        }
                        // handle submit
                        vm.addCourse(courseNumber, department, location, details)
                        courseNumber = ""
                        location = ""
                        details = ""
                        department = ""
                    },
                ) {
                    Text("Submit")
                }
            }
        }
    }
}

@Composable
fun RemoveCourse(vm: VMM, courseID: Int) {
    Button(
        onClick = {
            vm.removeCourse(courseID)
        },
    ) {
        Text("Remove")
    }
}

@Composable
fun DisplayInfo(course: CourseEntity, onDismiss: () -> Unit) {

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(course.department + course.courseNumber)},
        text = {
            Text(course.courseDetails)
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )

}