package s3492492project.labreportlogger.chandu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.StackedBarChart
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import s3492492project.labreportlogger.chandu.screens.EditReportScreen
import s3492492project.labreportlogger.chandu.screens.FavoriteReportsScreen
import s3492492project.labreportlogger.chandu.screens.MyReportsScreen
import s3492492project.labreportlogger.chandu.screens.ProfileScreen
import s3492492project.labreportlogger.chandu.screens.ReportDetailsRouteHandler
import s3492492project.labreportlogger.chandu.screens.ReportStatisticsScreen
import s3492492project.labreportlogger.chandu.screens.UploadReportScreen
import s3492492project.labreportlogger.chandu.ui.theme.crimsonRed


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
    object Stats : BottomNavItem("statistics", "Lab Report Logger App", Icons.Default.StackedBarChart)
    object Upload : BottomNavItem("upload", "Add Report", Icons.Default.UploadFile)
    object Reports : BottomNavItem("reports", "My Reports", Icons.Default.Description)
    object FavouriteReports : BottomNavItem("favorites", "Favourites", Icons.Default.Favorite)
    object Profile : BottomNavItem("profile", "Profile", Icons.Default.Person)
}


@Composable
fun NavigationGraph(navController: NavHostController,homenavController: NavController) {

    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Stats.route
    ) {

        composable(BottomNavItem.Stats.route) {
            ReportStatisticsScreen(
                onGoToReports = {
                }
            )
        }

        composable(BottomNavItem.Upload.route) {
            UploadReportScreen()
        }

        composable(BottomNavItem.Reports.route) {
            MyReportsScreen(onViewReport = { report ->
                navController.navigate("reportDetails/${report.reportId}")
            })
        }

        composable(BottomNavItem.Profile.route) {

            val context = LocalContext.current

            ProfileScreen(
                onLogout = {
                    UserPrefs.markLoginStatus(context, false)

                    navController.navigate(BottomNavItem.Upload.route) {
                        popUpTo(0) { inclusive = true }
                    }

                    homenavController.navigate(AppScreens.Login.route) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }


        composable(
            route = "reportDetails/{reportId}"
        ) { backStackEntry ->

            val reportId = backStackEntry.arguments?.getString("reportId") ?: ""

            ReportDetailsRouteHandler(
                reportId = reportId,
                onBack = { navController.popBackStack() },
                navController
            )
        }

        composable("editReport/{reportId}") { backStack ->
            val id = backStack.arguments?.getString("reportId") ?: ""
            EditReportScreen(reportId = id, onBack = { navController.popBackStack() })
        }

        composable(BottomNavItem.FavouriteReports.route) {
            FavoriteReportsScreen(
                onViewReport = { report ->
                    navController.navigate("reportDetails/${report.reportId}")
                },
                onBack = { navController.popBackStack() }
            )
        }

    }
}



@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Stats,
        BottomNavItem.Upload,
        BottomNavItem.Reports,
        BottomNavItem.FavouriteReports,
        BottomNavItem.Profile
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },

                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = crimsonRed,
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.Black,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}

