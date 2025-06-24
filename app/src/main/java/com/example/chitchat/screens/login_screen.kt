package com.example.chitchat.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.chitchat.AuthState
import com.example.chitchat.Auth_ViewModel
import com.example.chitchat.R

@Composable
fun LoginScreen(navController: NavController,authViewmodel: Auth_ViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var show by remember { mutableStateOf(true) }
    var auth_state=authViewmodel.auth_state.observeAsState()
    var contex= LocalContext.current
    LaunchedEffect(auth_state.value) {
        when(auth_state.value){
            is AuthState.Authenticated-> navController.navigate(route = "home_screen"){
                popUpTo("signup_screen") { inclusive = true }
            }
            is AuthState.Error -> {
                Toast.makeText(contex, "Email or Password Invalid", Toast.LENGTH_SHORT).show()
            }
            else -> Unit
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(108.dp))
        Box(modifier = Modifier
            .width(220.dp)
            .height(200.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(Color.White)
            , contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(id = R.drawable.chit_chat),
                contentDescription = "App Logo",
                modifier = Modifier.size(158.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Chit Chat", fontSize = 48.sp, color = Color.White)

        Spacer(modifier = Modifier.height(48.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("Email", color = Color.Black) },
            singleLine = true,
            shape = RoundedCornerShape(22.dp),
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = Color(0xFF7F8CAA),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(18.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Password", color = Color.Black) },
            singleLine = true,
            shape = RoundedCornerShape(22.dp),
            visualTransformation = if (show) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = {
                IconButton(onClick = { show = !show }) {
                    val icon = if (show) R.drawable.show_new else R.drawable.hide_new
                    val description = if (show) "Hide password" else "Show password"
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = description
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = Color(0xFF7F8CAA),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White
            )
        )
        Spacer(modifier = Modifier.height(18.dp))

        Button(onClick = {
            if(email.isBlank()||password.isBlank())
            {
                Toast.makeText(contex, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
            else {
                authViewmodel.Login(email, password)
            }
        },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White
            )
        ) {
            Text("Login",color = Color.Black , fontSize = 22.sp, fontWeight = FontWeight.Normal)
        }
        Spacer(modifier = Modifier.height(18.dp))
        TextButton(onClick = { navController.navigate(route = "signup_screen") }) {
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.White)) {
                        append("New user? ")
                    }
                    withStyle(style = SpanStyle(color = Color(0xFF7F8CAA))) {
                        append("Register")
                    }
                },
                fontSize = 18.sp,
                fontWeight = FontWeight.Light
            )
        }
    }
}
