// ✅ Auth_ViewModel.kt
package com.example.chitchat

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class Auth_ViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _auth_state = MutableLiveData<AuthState>()
    val auth_state: LiveData<AuthState> get() = _auth_state

    private val _currentUser = MutableLiveData<FirebaseUser?>()
    val currentUser: LiveData<FirebaseUser?> get() = _currentUser

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> get() = _username

    val _searchResults = mutableStateOf<List<Pair<String, String>>>(emptyList())

    init {
        Check_Auth_Status()
    }

    fun Check_Auth_Status() {
        _currentUser.value = auth.currentUser
        if (auth.currentUser == null) {
            _auth_state.value = AuthState.Unauthenticated
        } else {
            _auth_state.value = AuthState.Authenticated
        }
    }

    fun Login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _auth_state.value = AuthState.Error("Email or Password can't be Empty")
            return
        }
        _auth_state.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _currentUser.value = auth.currentUser
                    _auth_state.value = AuthState.Authenticated
                } else {
                    _auth_state.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

    fun Signup(email: String, password: String, username: String, name: String) {
        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            _auth_state.value = AuthState.Error("Fields can't be empty")
            return
        }

        _auth_state.value = AuthState.Loading

        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    _auth_state.value = AuthState.Error("Username already taken. Try something else.")
                } else {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val uid = auth.currentUser?.uid
                                _currentUser.value = auth.currentUser
                                if (uid != null) {
                                    val userMap = hashMapOf(
                                        "username" to username,
                                        "email" to email,
                                        "name" to name,
                                        "uid" to uid
                                    )
                                    db.collection("users")
                                        .document(uid)
                                        .set(userMap)
                                        .addOnSuccessListener {
                                            _auth_state.value = AuthState.Authenticated
                                        }
                                        .addOnFailureListener {
                                            _auth_state.value = AuthState.Error("Failed to save user.")
                                        }
                                }
                            } else {
                                _auth_state.value = AuthState.Error(task.exception?.message ?: "Signup failed.")
                            }
                        }
                }
            }
            .addOnFailureListener {
                _auth_state.value = AuthState.Error("Something went wrong. Please try again.")
            }
    }

    fun Signout() {
        auth.signOut()
        _currentUser.value = null
        _auth_state.value = AuthState.Unauthenticated
    }

    fun Loadname() {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            FirebaseFirestore.getInstance().collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { document ->
                    val name = document.getString("name")
                    _username.value = name ?: "No name"
                }
                .addOnFailureListener {
                    _username.value = "Failed to load"
                }
        } else {
            _username.value = "Not Logged In"
        }
    }

    fun searchUsers(query: String, currentUsername: String) {
        val currentUserUid = auth.currentUser?.uid ?: return

        FirebaseFirestore.getInstance().collection("users")
            .orderBy("username")
            .startAt(query)
            .endAt(query + '\uf8ff')
            .get()
            .addOnSuccessListener { result ->
                val users = result.documents
                    .filter { it.id != currentUserUid }
                    .mapNotNull { doc ->
                        val username = doc.getString("username")
                        val uid = doc.id
                        if (username != null) uid to username else null
                    }
                _searchResults.value = users
            }
            .addOnFailureListener {
                _searchResults.value = emptyList()
            }
    }

    fun clearSearchResults() {
        _searchResults.value = emptyList()
    }
}

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(var message: String) : AuthState()
    object Username_not_set : AuthState()
}
