package com.example.chitchat.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.navigation.NavController
import com.example.chitchat.Auth_ViewModel
import com.example.chitchat.R

@Composable
fun Add_User_Screen(navController: NavController, authViewmodel: Auth_ViewModel) {
    var addUsers by remember { mutableStateOf("") }
    val searchResults by authViewmodel._searchResults
    val currentUsername by authViewmodel.username.observeAsState()

    LaunchedEffect(addUsers) {
        if (addUsers.isNotBlank()) {
            authViewmodel.searchUsers(addUsers.trim(), currentUsername ?: "")
        } else {
            authViewmodel.clearSearchResults()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(top = 60.dp)
    ) {
        val iconId = if (addUsers.isNotEmpty()) R.drawable.close else R.drawable.search

        OutlinedTextField(
            value = addUsers,
            onValueChange = { addUsers = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            singleLine = true,
            placeholder = {
                Text("Enter Vibe Tag of a User", color = Color.Black)
            },
            shape = RoundedCornerShape(20.dp),
            trailingIcon = {
                IconButton(onClick = {
                    addUsers = ""
                    authViewmodel.clearSearchResults()
                }) {
                    Icon(
                        painter = painterResource(id = iconId),
                        contentDescription = if (addUsers.isNotEmpty()) "Clear" else "Search",
                        modifier = Modifier.size(22.dp)
                    )
                }
            },
            leadingIcon = {
                IconButton(onClick = {
                    navController.navigate("Home_screen")
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.back),
                        contentDescription = "Back",
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

        Spacer(modifier = Modifier.height(12.dp))

        if (addUsers.isNotBlank()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(searchResults, key = { it.first }) { pair ->
                    val uid = pair.first
                    val username = pair.second

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .clickable {
                                navController.navigate("Chatroom_Screen/$uid")
                            }
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.user),
                                contentDescription = "User Icon",
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                            )
                            Text(
                                text = username,
                                color = Color.Black
                            )
                        }
                    }
                }
            }


        }
    }
}