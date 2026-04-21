package com.example.userapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.userapp.data.model.User
import com.example.userapp.data.repository.UserRepository
import com.example.userapp.ui.viewmodel.UserViewModel
import kotlinx.coroutines.launch

/**
 * Kullanıcı Detay Ekranı
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    userId: String,
    navController: NavController,
    viewModel: UserViewModel
) {
    val user = remember { mutableStateOf<User?>(null) }
    val isLoading = remember { mutableStateOf(true) }
    val error = remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    // Veri yükle
    LaunchedEffect(userId) {
        scope.launch {
            try {
                isLoading.value = true
                error.value = null

                val userRepository = UserRepository()
                val fetchedUser = userRepository.getUserById(userId.toInt())
                user.value = fetchedUser

            } catch (e: Exception) {
                error.value = e.message ?: "Hata oluştu"
                e.printStackTrace()
            } finally {
                isLoading.value = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kullanıcı Detayları") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading.value -> {
                    CircularProgressIndicator()
                }
                error.value != null -> {
                    Text(
                        text = "Hata: ${error.value}",
                        color = Color.Red,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                user.value != null -> {
                    DetailContent(user = user.value!!)
                }
                else -> {
                    Text("Kullanıcı bulunamadı")
                }
            }
        }
    }
}

@Composable
fun DetailContent(user: User) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar
        Surface(
            shape = CircleShape,
            modifier = Modifier
                .size(120.dp)
                .background(getAvatarColor(user.id))
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(getAvatarColor(user.id))
            ) {
                Text(
                    text = user.name.take(1).uppercase(),
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Ad
        Text(
            text = user.name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Username
        Text(
            text = "@${user.username}",
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Detay Kartları
        DetailCard(label = "Email", value = user.email)
        Spacer(modifier = Modifier.height(12.dp))

        DetailCard(label = "Telefon", value = user.phone)
        Spacer(modifier = Modifier.height(12.dp))

        DetailCard(label = "Website", value = user.website)
        Spacer(modifier = Modifier.height(12.dp))

        DetailCard(label = "Şirket", value = user.company)
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun DetailCard(label: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

fun getAvatarColor(id: Int): Color {
    val colors = listOf(
        Color(0xFF6200EE),
        Color(0xFF03DAC6),
        Color(0xFFFF0000),
        Color(0xFFFF6F00),
        Color(0xFF00BCD4),
        Color(0xFF4CAF50),
        Color(0xFFE91E63),
        Color(0xFF2196F3),
        Color(0xFF9C27B0),
        Color(0xFFFFC107)
    )
    return colors[id % colors.size]}
