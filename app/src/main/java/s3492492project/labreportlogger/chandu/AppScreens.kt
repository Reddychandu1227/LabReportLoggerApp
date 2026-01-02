package s3492492project.labreportlogger.chandu

sealed class AppScreens(val route: String) {
    object Splash : AppScreens("splash_route")
    object Login : AppScreens("login_route")
    object Register : AppScreens("register_route")

    object Home : AppScreens("home_screen")

}