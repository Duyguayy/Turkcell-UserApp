package com.example.userapp.domain.model
import com.example.userapp.data.model.User
sealed interface UserUiState {
    object Loading : UserUiState

    data class Success(val users: List<User>) : UserUiState
    data class Error(val message: String) : UserUiState
}