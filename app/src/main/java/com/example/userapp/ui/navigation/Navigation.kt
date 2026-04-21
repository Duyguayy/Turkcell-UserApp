package com.example.userapp.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.userapp.domain.model.UserUiState
import com.example.userapp.ui.screen.components.SearchBar
import com.example.userapp.ui.screen.components.UserItem
import com.example.userapp.ui.viewmodel.UserViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(
    viewModel: UserViewModel,
    onUserSelected: (Int) -> Unit = {}  // ← BU PARAMETER EKLE
) {
    val userState by viewModel.userState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredUsers by viewModel.filteredUsers.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kullanıcılar") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ARAMA ÇUBUĞU
            SearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            )

            // İÇERİK
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing),
                onRefresh = { viewModel.refreshUsers() },
                modifier = Modifier.fillMaxSize()
            ) {
                when (userState) {
                    is UserUiState.Loading -> {
                        // LOADING STATE
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Kullanıcılar yükleniyor...")
                            }
                        }
                    }

                    is UserUiState.Success -> {
                        // BAŞARILI STATE
                        if (filteredUsers.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Kullanıcı bulunamadı")
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(8.dp)
                            ) {
                                items(
                                    items = filteredUsers,
                                    key = { it.id }
                                ) { user ->
                                    UserItem(
                                        user = user,
                                        onClick = {
                                            viewModel.selectUser(user)  // ← State'i güncelle
                                            onUserSelected(user.id)      // ← Navigation'a geç
                                        },
                                        modifier = Modifier.clickable {
                                            viewModel.selectUser(user)
                                            onUserSelected(user.id)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    is UserUiState.Error -> {
                        // HATA STATE
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "Hata!",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Red
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = (userState as UserUiState.Error).message,
                                    color = Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}