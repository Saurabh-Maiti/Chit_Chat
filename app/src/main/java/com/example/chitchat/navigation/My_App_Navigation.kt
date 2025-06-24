package com.example.chitchat.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.chitchat.Auth_ViewModel
import com.example.chitchat.screens.*
import com.google.firebase.auth.FirebaseAuth

@Composable
fun My_App_Navigation(authViewmodel: Auth_ViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "signup_screen") {
        composable("login_screen") {
            LoginScreen(navController, authViewmodel)
        }
        composable("signup_screen") {
            SignupScreen(navController, authViewmodel)
        }
        composable("Home_screen") {
            Home_screen(navController, authViewmodel)
        }
        composable("Profile_Screen") {
            Profile_Screen(navController, authViewmodel)
        }
        composable("Add_User_Screen") {
            Add_User_Screen(navController, authViewmodel)
        }
        composable(
            route = "Chatroom_Screen/{otherUserId}",
            arguments = listOf(navArgument("otherUserId") { type = NavType.StringType })
        ) { backStackEntry ->
            val otherUserId = backStackEntry.arguments?.getString("otherUserId") ?: return@composable
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return@composable

            Chatroom_Screen(
                navController = navController,
                authViewmodel = authViewmodel,
                currentUserId = currentUserId,
                otherUserId = otherUserId
            )
        }
    }
}
