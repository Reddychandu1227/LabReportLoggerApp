package madproject.chandu.labreportlogger

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import madproject.chandu.labreportlogger.R
import madproject.chandu.labreportlogger.ui.theme.LabReportLoggerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LabReportLoggerTheme {
                MyAppNavGraph()
            }
        }
    }

}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyAppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppScreens.Splash.route
    ) {
        composable(AppScreens.Splash.route) {
            BrandDisplay(navController = navController)
        }

        composable(AppScreens.Login.route) {
            LabReportloginScreen(navController = navController)
        }

        composable(AppScreens.Register.route) {
            LabReportRegisterScreen(navController = navController)
        }

        composable(AppScreens.Home.route) {
            HomeScreen(homenavController = navController)
        }



    }

}


@Composable
fun BrandDisplay(navController: NavController) {

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        delay(3000)

        if (UserPrefs.checkLoginStatus(context)) {
            navController.navigate(AppScreens.Home.route) {
                popUpTo(AppScreens.Splash.route) {
                    inclusive = true
                }
            }
        } else {
            navController.navigate(AppScreens.Login.route) {
                popUpTo(AppScreens.Splash.route) {
                    inclusive = true
                }
            }
        }

    }

    BrandDisplayScreen()
}



@Composable
fun BrandDisplayScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.crimson_red)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Welcome to",
                color = colorResource(id = R.color.soft_peach),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(bottom = 18.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Spacer(modifier = Modifier.height(6.dp))

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Lab Report Logger",
                color = colorResource(id = R.color.soft_peach),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(bottom = 18.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Image(
                painter = painterResource(id = R.drawable.ic_labreport),
                contentDescription = "Lab Report Logger",
            )

            Spacer(modifier = Modifier.weight(1f))


        }
    }

}


@Preview(showBackground = true)
@Composable
fun BrandDisplayScreenPreview() {
    BrandDisplayScreen()
}