package s3492492project.labreportlogger.chandu

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleRight
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.firebase.database.FirebaseDatabase
import s3492492project.labreportlogger.chandu.ui.theme.crimsonRed


fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

@Composable
fun LabReportloginScreen(navController: NavController) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current.findActivity()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = crimsonRed)
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Login",
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

            Column(
                modifier = Modifier
                    .width(300.dp)
                    .background(color = crimsonRed)
            )
            {

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
                            imageVector = Icons.Default.Lock,
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


            IconButton(onClick = {
                when {
                    email.isEmpty() -> {
                        Toast.makeText(context, " Please Enter Mail", Toast.LENGTH_SHORT).show()
                    }

                    password.isEmpty() -> {
                        Toast.makeText(context, " Please Enter Password", Toast.LENGTH_SHORT).show()
                    }

                    else -> {
                        signInGuest(email, password, context!!, navController)
                    }

                }
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowCircleRight,
                    contentDescription = "Login",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }



            Spacer(modifier = Modifier.weight(1f))

        }
        Spacer(modifier = Modifier.height(64.dp))

        Button(
            onClick = {

                navController.navigate(AppScreens.Register.route) {
                    popUpTo(AppScreens.Login.route) {
                        inclusive = true
                    }
                }

            },
            modifier = Modifier
                .height(50.dp),
            shape = RoundedCornerShape(
                topEnd = 16.dp,
                bottomEnd = 16.dp
            ),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFB0BEC5),
                contentColor = Color.Black
            )
        ) {
            Text("New to app? Register")
        }



        Spacer(modifier = Modifier.weight(1f))

    }


}


@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LabReportloginScreen(navController = NavHostController(LocalContext.current))
}

private fun signInGuest(
    useremail: String,
    userpassword: String,
    context: Activity,
    navController: NavController
) {
    val db = FirebaseDatabase.getInstance()
    val sanitizedUid = useremail.replace(".", ",")
    val ref = db.getReference("Users").child(sanitizedUid)

    ref.get().addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val userData = task.result?.getValue(UserData::class.java)
            checkAndGO(useremail, userpassword, context, userData, navController)
        } else {
            Toast.makeText(
                context,
                "Failed to retrieve user data: ${task.exception?.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

fun checkAndGO(
    useremail: String,
    userpassword: String,
    context: Activity,
    userData: UserData?,
    navController: NavController
) {
    if (userData != null) {
        if (userData.userpassword == userpassword) {

            UserPrefs.markLoginStatus(context, true)
            UserPrefs.saveEmail(
                context,
                email = useremail
            )
            UserPrefs.saveName(context, userData.username)

            Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()

            navController.navigate(AppScreens.Home.route) {
                popUpTo(AppScreens.Login.route) {
                    inclusive = true
                }
            }


        } else {
            Toast.makeText(context, "Invalid Password", Toast.LENGTH_SHORT).show()
        }
    } else {
        Toast.makeText(context, "No user data found", Toast.LENGTH_SHORT).show()
    }
}