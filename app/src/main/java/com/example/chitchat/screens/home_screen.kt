package com.example.chitchat.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun Home_screen(navController: NavController, authViewmodel: Auth_ViewModel) {
    val authState by authViewmodel.auth_state.observeAsState()
    val name by authViewmodel.username.observeAsState()
    val currentUser by authViewmodel.currentUser.observeAsState()
    val currentUserId = currentUser?.uid

    var search_chat by remember { mutableStateOf("") }
    val db = FirebaseFirestore.getInstance()
    val userList = remember { mutableStateListOf<Map<String, Any>>() }
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                authViewmodel.Loadname()
                db.collection("users")
                    .get()
                    .addOnSuccessListener { result ->
                        userList.clear()
                        for (doc in result.documents) {
                            if (doc.id != currentUserId) {
                                val userData = doc.data ?: emptyMap()
                                userList.add(userData + mapOf("uid" to doc.id)) // include uid
                            }
                        }
                    }
            }

            is AuthState.Unauthenticated -> navController.navigate("login_screen") {
                popUpTo("Home_screen") { inclusive = true }
            }

            else -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 🔝 Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 48.dp)
                .align(Alignment.TopCenter),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Hey, ${name ?: ""}!",
                fontSize = 24.sp,
                color = Color.White
            )
            Image(
                painter = painterResource(id = R.drawable.profile_pic),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .clickable {
                        navController.navigate(route = "Profile_Screen")
                    }
            )
        }

        // 📜 Chat List
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 120.dp, bottom = 80.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = search_chat,
                onValueChange = { search_chat = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                singleLine = true,
                placeholder = { Text("Search", color = Color.Black) },
                shape = RoundedCornerShape(20.dp),
                trailingIcon = {
                    var icon=R.drawable.search
                    if(search_chat.isNotBlank())
                    {
                        icon=R.drawable.close
                    }
                    IconButton(onClick = {
                        if (search_chat.isNotBlank())
                        {
                            search_chat=""
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = icon),
                            contentDescription = "Search",
                            modifier = Modifier.size(22.dp)
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

            Spacer(modifier = Modifier.height(16.dp))

            val filteredUsers = userList.filter {
                val username = (it["username"] as? String)?.lowercase() ?: ""
                username.contains(search_chat.lowercase())
            }

            if (filteredUsers.isEmpty()) {
                Text("Start a new Chat", color = Color.Gray, fontSize = 16.sp)
            } else {
                filteredUsers.forEach { user ->
                    val username = user["username"] as? String ?: "Unknown"
                    val uid = user["uid"] as? String ?: return@forEach

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .clickable {
                                navController.navigate("Chatroom_Screen/$uid")
                            },
                        colors = CardDefaults.cardColors(containerColor = Color.Black)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.background(Color.Black).size(60.dp))
                            {
                                Image(
                                    painter = painterResource(id = R.drawable.profile_pic),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Box(modifier = Modifier.clip(RoundedCornerShape(22.dp))
                                .background(Color.White)
                                .fillMaxWidth().height(54.dp).padding(12.dp),
                                contentAlignment = Alignment.CenterStart
                            )
                            {
                                Text(
                                    text = username,
                                    color = Color.Black,
                                    fontSize = 22.sp
                                )
                            }

                        }
                    }
                }
            }
        }

        // ➕ FAB
        Row(
            modifier = Modifier
                .size(90.dp)
                .align(Alignment.BottomEnd)
                .padding(end = 28.dp, bottom = 28.dp)
                .background(Color.Black, shape = CircleShape)
                .border(1.dp, Color.White, CircleShape)
                .clip(CircleShape)
                .padding(12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.plus),
                contentDescription = "Plus Icon",
                modifier = Modifier
                    .size(62.dp)
                    .clickable {
                        navController.navigate(route = "Add_User_Screen")
                    }
            )
        }
    }
}
