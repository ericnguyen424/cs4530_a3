package com.example.cs4530_a3

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cs4530_a3.ui.theme.Cs4530_a3Theme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * This class holds the VM and the Model and will be used to store the course list
 */
class VMM : ViewModel()
{
    // model
    private val data = MutableStateFlow(listOf<Course>())

    val dataReadOnly : StateFlow<List<Course>> = data

    fun addCourse (item: Course) {
        data.value = data.value + item
    }

    fun removeCourse(item: Course) {
        data.value = data.value.filter({ it.id != item.id })
    }

    fun getCourse(courseID: Int): Course {
        return data.value.filter({it.id == courseID}).elementAt(0)
    }
}

/**
 * This is the class for a Course which takes in a department, course number, and a location
 */
class Course(private val courseNumber: String, private val department: String, private val location: String, private val courseDetails : String) {
    val id = "$department$courseNumber$location$courseDetails".hashCode()

    fun getCourseName() : String {
        return "$department$courseNumber"
    }

    fun getLocation() : String {
        return location
    }

    fun getDetails() : String {
        return courseDetails
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


            val courseList by vm.dataReadOnly.collectAsState()
            var showPopup by remember { mutableStateOf(false)}
            var currentCourse by remember{ mutableStateOf(0)}

            if (showPopup) {
                var course = vm.getCourse(currentCourse)
                DisplayInfo(course, {showPopup = false})
            }

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
                            Text(text = "${course.getCourseName()} at ${course.getLocation()}", color = Color.White)
                        }

                        Row {
                            RemoveCourse(vm, course)
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
                        val newCourse = Course(courseNumber, department, location, details)
                        vm.addCourse(newCourse)
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
fun RemoveCourse(vm: VMM, courseToRemove: Course) {
    Button(
        onClick = {
            vm.removeCourse(courseToRemove)
        },
    ) {
        Text("Remove")
    }
}

@Composable
fun DisplayInfo(course: Course, onDismiss:() -> Unit) {

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(course.getCourseName())},
        text = {
            Text(course.getDetails())
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )

}