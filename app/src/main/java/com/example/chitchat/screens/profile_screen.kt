package com.example.chitchat.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.chitchat.AuthState
import com.example.chitchat.Auth_ViewModel
import com.example.chitchat.R

@Composable
fun Profile_Screen(navController: NavController, authViewmodel: Auth_ViewModel) {
    val name by authViewmodel.username.observeAsState()
    val auth_state by authViewmodel.auth_state.observeAsState()
    when(auth_state){
        is AuthState.Unauthenticated->navController.navigate("login_screen")
        else -> Unit
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🔝 Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 48.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Profile Page",
                    fontSize = 24.sp,
                    color = Color.White
                )
                Image(
                    painter = painterResource(id = R.drawable.profile_pic),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .clickable { }
                )
            }

            // ✏️ Option Buttons
            ProfileOption(text = "Edit Name")
            Spacer(Modifier.height(18.dp))
            ProfileOption(text = "Change Password")
            Spacer(Modifier.height(18.dp))
            ProfileOption(text = "Share App")
            Spacer(Modifier.height(18.dp))
            ProfileOption(text = "Privacy Policy")
            Spacer(Modifier.height(18.dp))
            ProfileOption(text = "Delete Account")
            Spacer(Modifier.height(18.dp))
            Button(onClick = {
                authViewmodel.Signout()
            }, colors = ButtonDefaults.buttonColors(
                containerColor = Color.White
            )) {
                Text("Sign out", color = Color.Black)
            }
        }
    }
}

@Composable
fun ProfileOption(text: String) {
    var text_color= Color.Black
    if(text=="Delete Account")
    {
        text_color= Color.Red
    }
        Box(
        modifier = Modifier
            .width(300.dp)
            .height(54.dp).clip(RoundedCornerShape(22.dp))
            .background(Color.White)
            .clickable { },
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = text_color, fontSize = 16.sp)
    }
}
