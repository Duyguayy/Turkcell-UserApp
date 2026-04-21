package com.example.userapp.data.repository

import com.example.userapp.data.model.User
import com.example.userapp.data.remote.RetrofitInstance
class UserRepository {
    suspend fun getAllUsers(): List<User> {
        return RetrofitInstance.api.getUsers()
    }

    suspend fun getUserById(id: Int): User {
        return RetrofitInstance.api.getUserById(id)
    }
    fun filterUsers(users: List<User>, query: String): List<User> {
        if (query.isEmpty()) return users

        val lowerQuery = query.lowercase()
        return users.filter { user ->
            user.name.lowercase().contains(lowerQuery) ||
                    user.email.lowercase().contains(lowerQuery) ||
                    user.username.lowercase().contains(lowerQuery)
        }
    }
}