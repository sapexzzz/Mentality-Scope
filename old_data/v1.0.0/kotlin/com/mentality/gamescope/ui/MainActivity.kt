package com.mentality.gamescope.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import android.widget.Toast
import com.mentality.gamescope.data.repository.CrosshairRepository
import com.mentality.gamescope.permission.PermissionManager
import com.mentality.gamescope.ui.screen.ConfigsScreen
import com.mentality.gamescope.ui.screen.HomeScreen
import com.mentality.gamescope.ui.screen.SettingsScreen
import com.mentality.gamescope.ui.theme.MentalityScopeTheme
import com.mentality.gamescope.viewmodel.CrosshairViewModel
import com.mentality.gamescope.viewmodel.CrosshairViewModelFactory

/**
 * Главная Activity приложения Mentality Scope
 * Управляет навигацией между тремя основными экранами: Home, Configs, Settings
 */
class MainActivity : ComponentActivity() {

    private lateinit var viewModel: CrosshairViewModel
    private lateinit var permissionManager: PermissionManager

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            showSnackbar("Разрешение на уведомления отклонено")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Инициализировать репозиторий и ViewModel
        val repository = CrosshairRepository(this)
        val viewModelFactory = CrosshairViewModelFactory(this, repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CrosshairViewModel::class.java)

        // Инициализировать менеджер разрешений
        permissionManager = PermissionManager(this)

        // Проверить и запросить разрешения
        checkAndRequestPermissions()

        // Установить content
        setContent {
            MentalityScopeTheme {
                MainScreen(viewModel)
            }
        }
    }

    /**
     * Проверить и запросить необходимые разрешения
     */
    private fun checkAndRequestPermissions() {
        // Проверить SYSTEM_ALERT_WINDOW
        if (!permissionManager.canDrawOverlays()) {
            permissionManager.requestDrawOverlayPermission()
        }

        // Запросить POST_NOTIFICATIONS (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    /**
     * Показать Snackbar сообщение
     */
    private fun showSnackbar(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Основной экран с Scaffold и NavigationBar
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MainScreen(viewModel: CrosshairViewModel) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Mentality Scope",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Главная") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Configs") },
                    label = { Text("Конфиги") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.MoreVert, contentDescription = "Settings") },
                    label = { Text("Настройки") }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                0 -> HomeScreen(viewModel)
                1 -> ConfigsScreen(viewModel)
                2 -> SettingsScreen(viewModel)
            }
        }
    }
}
