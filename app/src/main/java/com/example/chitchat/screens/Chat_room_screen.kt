package com.example.chitchat.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.chitchat.Auth_ViewModel
import com.example.chitchat.R
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

data class Message(
    val message: String,
    val senderId: String,
    val timestamp: String
)

@Composable
fun Chatroom_Screen(
    navController: NavController,
    authViewmodel: Auth_ViewModel,
    currentUserId: String,
    otherUserId: String
) {
    val db = FirebaseFirestore.getInstance()
    var otherUsername by remember { mutableStateOf("Loading...") }
    var messageText by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<Message>() }
    val chatroomId = listOf(currentUserId, otherUserId).sorted().joinToString("_")

    // Load messages & username
    LaunchedEffect(otherUserId) {
        db.collection("users").document(otherUserId).get().addOnSuccessListener {
            otherUsername = it.getString("username") ?: "Unknown"
        }
        db.collection("chatrooms")
            .document(chatroomId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    messages.clear()
                    for (doc in snapshot.documents) {
                        val msg = doc.getString("message") ?: ""
                        val senderId = doc.getString("senderId") ?: ""
                        val timestamp = doc.getTimestamp("timestamp")?.toDate()?.toString() ?: ""
                        messages.add(Message(msg, senderId, timestamp))
                    }
                }
            }
    }

    // Screen UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .imePadding()
    ) {

        // 🔝 Top Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp)
        ) {
            Row(
                modifier = Modifier.align(Alignment.CenterStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    navController.navigate("Home_screen") // Make sure route name matches NavHost
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.back_white),
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Image(
                    painter = painterResource(id = R.drawable.profile_pic),
                    contentDescription = null,
                    modifier = Modifier
                        .size(55.dp)
                        .clip(CircleShape)
                )
            }

            Text(
                text = otherUsername,
                fontSize = 22.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // 📨 Messages & Send Box
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                reverseLayout = false
            ) {
                items(messages) { message ->
                    MessageBubble(
                        message = message,
                        isCurrentUser = message.senderId == currentUserId
                    )
                }
            }

            // 🔽 Input Box
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Type a message...", color = Color.Black) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        cursorColor = Color(0xFF7F8CAA),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        sendMessage(
                            db,
                            chatroomId,
                            currentUserId,
                            messageText,
                            onSent = { messageText = "" }
                        )
                    })
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .size(55.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.send),
                        contentDescription = "Send",
                        modifier = Modifier
                            .size(36.dp)
                            .clickable {
                                sendMessage(
                                    db,
                                    chatroomId,
                                    currentUserId,
                                    messageText,
                                    onSent = { messageText = "" }
                                )
                            }
                    )
                }
            }
        }
    }
}

// 🧩 Send Message Helper
private fun sendMessage(
    db: FirebaseFirestore,
    chatroomId: String,
    senderId: String,
    message: String,
    onSent: () -> Unit
) {
    if (message.isNotBlank()) {
        val msgData = hashMapOf(
            "senderId" to senderId,
            "message" to message,
            "timestamp" to FieldValue.serverTimestamp()
        )
        db.collection("chatrooms")
            .document(chatroomId)
            .collection("messages")
            .add(msgData)
        onSent()
    }
}

// 💬 Message Bubble
@Composable
fun MessageBubble(message: Message, isCurrentUser: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .background(
                    if (isCurrentUser) Color(0xFF9FB5E6) else Color.White,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(12.dp)
                .widthIn(max = 250.dp)
        ) {
            Text(
                text = message.message,
                color = Color.Black,
                fontSize = 16.sp
            )
            Text(
                text = message.timestamp.take(19),
                fontSize = 10.sp,
                color = Color.Black,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}
