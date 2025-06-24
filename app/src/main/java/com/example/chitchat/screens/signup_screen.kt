package com.example.chitchat.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.chitchat.AuthState
import com.example.chitchat.Auth_ViewModel
import com.example.chitchat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun SignupScreen(navController: NavController,authViewmodel: Auth_ViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm_password by remember { mutableStateOf("") }
    var password_show by remember { mutableStateOf(true) }
    var confirm_password_show by remember { mutableStateOf(true) }
    var username by remember() { mutableStateOf("") }
    username=username.lowercase()
    var name by remember { mutableStateOf("") }
    val contex= LocalContext.current
    val auth_state=authViewmodel.auth_state.observeAsState()
    LaunchedEffect(auth_state.value) {
       when(auth_state.value){
           is AuthState.Authenticated-> navController.navigate(route = "home_screen"){
               popUpTo("signup_screen") { inclusive = true }
           }
           is AuthState.Error -> {
               Toast.makeText(contex, "Username or Email is Already already taken", Toast.LENGTH_SHORT).show()
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
        Spacer(modifier = Modifier.height(60.dp))
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
        Spacer(modifier = Modifier.height(12.dp))
        Text("Chit Chat", fontSize = 48.sp, color = Color.White)
        Spacer(modifier = Modifier.height(38.dp))
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            placeholder = { Text("Username (Vibe Tag)", color = Color.Black) },
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
            value = name,
            onValueChange = { name = it },
            placeholder = { Text("Name", color = Color.Black) },
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
            visualTransformation = if(password_show) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = {
                IconButton(onClick = {password_show=!password_show}) {
                    val icon=if(password_show) R.drawable.show_new else R.drawable.hide_new
                    val description = if (password_show) "Hide password" else "Show password"
                    Icon(painter = painterResource(id=icon),description)
                }
            },
            colors =  OutlinedTextFieldDefaults.colors(
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
            value = confirm_password,
            onValueChange = { confirm_password = it },
            placeholder = { Text("Confirm Password", color = Color.Black) },
            singleLine = true,
            shape = RoundedCornerShape(22.dp),
            visualTransformation = if(confirm_password_show) PasswordVisualTransformation()else VisualTransformation.None,
            trailingIcon = {
                IconButton(onClick = {confirm_password_show=!confirm_password_show}) {
                    val icon=if(confirm_password_show)R.drawable.show_new else R.drawable.hide_new
                    val description = if (confirm_password_show) "Hide confirm password" else "Show confirm password"
                    Icon(painter = painterResource(id=icon),description)
                }
            },
            colors =  OutlinedTextFieldDefaults.colors(
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
            if(email.isBlank()||password.isBlank()||confirm_password.isBlank()||username.isBlank())
            {
                Toast.makeText(contex,"Email or Passwords or Username can't be Blank",Toast.LENGTH_SHORT).show()
            }
            else if(password!=confirm_password)
            {
                Toast.makeText(contex,"Passwords do not match. Please re-enter",Toast.LENGTH_SHORT).show()
            }
            else
            {
                authViewmodel.Signup(email, password, username,name)
            }
        },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White
            )
        ) {
            Text("Create Account",color = Color.Black , fontSize = 22.sp, fontWeight = FontWeight.Normal)
        }
        Spacer(modifier = Modifier.height(2.dp))
        TextButton(onClick = { navController.navigate(route="login_screen")}) {
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(color = Color.White)) {
                        append("Already have an account? ")
                    }
                    withStyle(style = SpanStyle(color = Color(0xFF7F8CAA))) {
                        append("Login")
                    }
                },
                fontSize = 18.sp,
                fontWeight = FontWeight.Light
            )
        }
    }
}