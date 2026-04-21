package com.example.userapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.userapp.data.model.User
import com.example.userapp.data.repository.UserRepository
import com.example.userapp.domain.model.UserUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
class UserViewModel(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _userState = MutableStateFlow<UserUiState>(UserUiState.Loading)
    val userState: StateFlow<UserUiState> = _userState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filteredUsers = MutableStateFlow<List<User>>(emptyList())
    val filteredUsers: StateFlow<List<User>> = _filteredUsers.asStateFlow()

    private val _selectedUser = MutableStateFlow<User?>(null)
    val selectedUser: StateFlow<User?> = _selectedUser.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()


    init {
        loadUsers()
    }

    fun loadUsers() {
        viewModelScope.launch {
            _userState.value = UserUiState.Loading

            try {
                val users = userRepository.getAllUsers()
                _userState.value = UserUiState.Success(users)
                _filteredUsers.value = users

            } catch (e: Exception) {
                val errorMessage = e.message ?: "Bilinmeyen hata oluştu"
                _userState.value = UserUiState.Error(errorMessage)
                e.printStackTrace()
            }
        }
    }
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query

        val currentState = _userState.value

        if (currentState is UserUiState.Success) {
            _filteredUsers.value = userRepository.filterUsers(
                currentState.users,
                query
            )
        }
    }

    fun selectUser(user: User) {
        _selectedUser.value = user
    }

    fun refreshUsers() {
        viewModelScope.launch {
            _isRefreshing.value = true

            try {
                val users = userRepository.getAllUsers()
                _userState.value = UserUiState.Success(users)
                _filteredUsers.value = users
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Bilinmeyen hata oluştu"
                _userState.value = UserUiState.Error(errorMessage)
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun clearSelectedUser() {
        _selectedUser.value = null
    }
}