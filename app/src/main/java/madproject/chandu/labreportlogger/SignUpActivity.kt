package madproject.chandu.labreportlogger

import android.R.attr.name
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LabReportRegisterScreen()
        }
    }
}



@Composable
fun LabReportRegisterScreen() {

    var fullName by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    var password by remember { mutableStateOf("") }


    val context = LocalContext.current as Activity


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.crimson_red))
    ) {
        Spacer(modifier = Modifier.weight(1f))
        // Login title
        Text(
            text = "Register",
            color = Color.White,
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier
                .padding(bottom = 12.dp)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.weight(0.2f))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(12.dp))
            // Column with Text Fields
            Column(
                modifier = Modifier
                    .width(300.dp)
                    .background(color = colorResource(id = R.color.crimson_red))
            )
            {

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Enter Name") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Account Icon",
                            tint = Color.White
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.Gray,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.Gray,
                        focusedLeadingIconColor = Color.White,
                        unfocusedLeadingIconColor = Color.Gray,
                        focusedIndicatorColor = Color.White,
                        unfocusedIndicatorColor = Color.Gray,
                        cursorColor = Color.White
                    ),
                    modifier = Modifier
                        .width(250.dp)
                        .padding(vertical = 0.dp)
                )

                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Enter Age") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Date Icon",
                            tint = Color.White
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.Gray,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.Gray,
                        focusedLeadingIconColor = Color.White,
                        unfocusedLeadingIconColor = Color.Gray,
                        focusedIndicatorColor = Color.White,
                        unfocusedIndicatorColor = Color.Gray,
                        cursorColor = Color.White
                    ),
                    modifier = Modifier
                        .width(250.dp)
                        .padding(vertical = 0.dp)
                )

                // User Name TextField
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Enter Email") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email Icon",
                            tint = Color.White
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.Gray,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.Gray,
                        focusedLeadingIconColor = Color.White,
                        unfocusedLeadingIconColor = Color.Gray,
                        focusedIndicatorColor = Color.White,
                        unfocusedIndicatorColor = Color.Gray,
                        cursorColor = Color.White
                    ),
                    modifier = Modifier
                        .width(250.dp)
                        .padding(vertical = 0.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Enter Password") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock, // Replace with desired icon
                            contentDescription = "Email Icon",
                            tint = Color.White
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.Gray,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.Gray,
                        focusedLeadingIconColor = Color.White,
                        unfocusedLeadingIconColor = Color.Gray,
                        focusedIndicatorColor = Color.White,
                        unfocusedIndicatorColor = Color.Gray,
                        cursorColor = Color.White
                    ),
                    modifier = Modifier
                        .width(250.dp)
                        .padding(vertical = 0.dp)
                )


            }

            Spacer(modifier = Modifier.weight(1f))

            Image(
                modifier = Modifier.clickable {

                    if (fullName.isEmpty()) {
                        Toast.makeText(context, "Enter name", Toast.LENGTH_SHORT).show()
                        return@clickable

                    }

                    if (age.isEmpty()) {
                        Toast.makeText(context, "Enter Age", Toast.LENGTH_SHORT).show()
                        return@clickable
                    }

                    if (email.isEmpty()) {
                        Toast.makeText(context, "Enter Mail", Toast.LENGTH_SHORT).show()
                        return@clickable
                    }

                    if (password.isEmpty()) {
                        Toast.makeText(context, "Enter Password", Toast.LENGTH_SHORT).show()
                        return@clickable
                    }else {

                        val residentData = UserData(
                            username = fullName,
                            userAge = age,
                            useremail = email,
                            userpassword = password
                        )
                        signUpGuest(residentData, context)
                    }
                },
                painter = painterResource(id = R.drawable.baseline_arrow_circle_right_36),
                contentDescription = "Lab Report Logger",
            )

            Spacer(modifier = Modifier.weight(1f))

        }
        Spacer(modifier = Modifier.height(64.dp))

        Button(
            onClick = {
                context.startActivity(Intent(context, SignUpActivity::class.java))
                context.finish()
            },
            modifier = Modifier
                .height(50.dp),
            shape = RoundedCornerShape(
                topEnd = 16.dp, // Adjust radius as needed
                bottomEnd = 16.dp
            ),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFB0BEC5), // Adjust color as needed
                contentColor = Color.Black
            )
        ) {
            Text("Old User? Login")
        }



        Spacer(modifier = Modifier.weight(1f))

    }
}

private fun signUpGuest(userData: UserData, context: Activity) {
    val db = FirebaseDatabase.getInstance()
    val ref = db.getReference("Users")

    ref.child(userData.useremail.replace(".", ",")).setValue(userData)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                checkandLogin(context)
            } else {
                Toast.makeText(
                    context,
                    "User Registration Failed: ${task.exception?.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        .addOnFailureListener { exception ->
            Toast.makeText(
                context,
                "User Registration Failed: ${exception.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
}

fun checkandLogin(context: Activity) {
    Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()
    context.startActivity(Intent(context, SignInActivity::class.java))
    context.finish()
}


data class UserData(
    val username: String = "",
    val useremail: String = "",
    val userAge: String = "",
    val userpassword: String = ""
)