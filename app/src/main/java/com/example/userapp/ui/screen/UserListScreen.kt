package com.example.userapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
    onUserSelected: (String) -> Unit = {}
) {

    val uiState by viewModel.userState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val filteredUsers by viewModel.filteredUsers.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    // Pull-to-Refresh state
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Kullanıcılar",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    // Refresh butonu (manuel)
                    IconButton(onClick = { viewModel.refreshUsers() }) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Yenile",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { viewModel.refreshUsers() },
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Arama çubuğu
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { viewModel.updateSearchQuery(it) }
                )

                // Ana içerik - UI State'e göre
                when (uiState) {
                    // ===== LOADING DURUMU =====
                    is UserUiState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(48.dp),
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Kullanıcılar yükleniyor...",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    is UserUiState.Success -> {
                        if (filteredUsers.isEmpty()) {
                            // Arama sonucu boşsa
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (searchQuery.isEmpty()) {
                                        "Kullanıcı bulunamadı"
                                    } else {
                                        "\"$searchQuery\" ile eşleşen kullanıcı yok"
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            // LazyColumn - Verimli liste (scroll sırasında render)
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(vertical = 8.dp),
                                contentPadding = PaddingValues(bottom = 16.dp)
                            ) {
                                items(
                                    items = filteredUsers,
                                    key = { it.id }  // Unique key - performans
                                ) { user ->
                                    UserItem(
                                        user = user,
                                        onClick = {
                                            // Detay ekranına geç
                                            onUserSelected(user.id.toString())
                                        },
                                    )
                                }
                            }
                        }
                    }

                    // ===== HATA DURUMU =====
                    is UserUiState.Error -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Hata ikonu
                                Icon(
                                    imageVector = Icons.Filled.Warning,
                                    contentDescription = "Error",
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.error
                                )

                                // Hata mesajı
                                Text(
                                    text = (uiState as UserUiState.Error).message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )

                                // Tekrar Dene butonu
                                Button(
                                    onClick = { viewModel.loadUsers() },
                                    modifier = Modifier
                                        .fillMaxWidth(0.6f)
                                        .height(44.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Text(
                                        text = "Tekrar Dene",
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


