package madproject.chandu.labreportlogger

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import madproject.chandu.labreportlogger.screens.LabReport
import madproject.chandu.labreportlogger.screens.MyReportsScreen
import madproject.chandu.labreportlogger.screens.ProfileScreen
import madproject.chandu.labreportlogger.screens.ReportDetailsRouteHandler
import madproject.chandu.labreportlogger.screens.ReportDetailsScreen
import madproject.chandu.labreportlogger.screens.UploadReportScreen


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(homenavController = NavHostController(LocalContext.current))
}

@Composable
fun HomeScreen(homenavController: NavController) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
            NavigationGraph(navController,homenavController)
        }
    }
}

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Upload : BottomNavItem("upload", "Upload Report", Icons.Default.Person)
    object Reports : BottomNavItem("reports", "My Reports", Icons.Default.AccountBox)
    object Profile : BottomNavItem("profile", "Profile", Icons.Default.Person)
}


@Composable
fun NavigationGraph(navController: NavHostController,homenavController: NavController) {

    val context = LocalContext.current


    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Upload.route
    ) {

        // ---------------- Upload Screen ----------------
        composable(BottomNavItem.Upload.route) {
            UploadReportScreen()
        }

        // ---------------- Reports List Screen ----------------
        composable(BottomNavItem.Reports.route) {
            MyReportsScreen(onViewReport = { report ->
                navController.navigate("reportDetails/${report.reportId}")
            })
        }

        // ---------------- Profile ----------------
        composable(BottomNavItem.Profile.route) {

            val context = LocalContext.current

            ProfileScreen(
                onLogout = {
                    // Mark user logged out
                    UserPrefs.markLoginStatus(context, false)

                    // 1. Clear bottom-nav controller backstack completely
                    navController.navigate(BottomNavItem.Upload.route) {
                        popUpTo(0) { inclusive = true }
                    }

                    // 2. Navigate using HOME nav controller to login
                    homenavController.navigate(AppScreens.Login.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }



        // ---------------- Report Details Screen ----------------
        composable(
            route = "reportDetails/{reportId}"
        ) { backStackEntry ->

            val reportId = backStackEntry.arguments?.getString("reportId") ?: ""

            ReportDetailsRouteHandler(
                reportId = reportId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}



@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Upload,
        BottomNavItem.Reports,
        BottomNavItem.Profile
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                    }
                }
            )
        }
    }
}

