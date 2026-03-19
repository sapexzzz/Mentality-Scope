package com.mentality.gamescope.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.mentality.gamescope.data.repository.CrosshairRepository
import com.mentality.gamescope.permission.PermissionManager
import com.mentality.gamescope.ui.screen.ConfigsScreen
import com.mentality.gamescope.ui.screen.HomeScreen
import com.mentality.gamescope.ui.screen.SettingsScreen
import com.mentality.gamescope.ui.theme.MentalityScopeTheme
import com.mentality.gamescope.viewmodel.CrosshairViewModel
import com.mentality.gamescope.viewmodel.CrosshairViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: CrosshairViewModel
    private lateinit var permissionManager: PermissionManager

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Toast.makeText(this, "Разрешение на уведомления отклонено", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = CrosshairRepository(this)
        val viewModelFactory = CrosshairViewModelFactory(this, repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CrosshairViewModel::class.java)

        permissionManager = PermissionManager(this)
        checkAndRequestPermissions()

        // Наблюдать за языком и применять при изменении
        lifecycleScope.launch {
            viewModel.appLanguage.collect { lang ->
                val localeList = when (lang) {
                    "ru" -> LocaleListCompat.forLanguageTags("ru")
                    "en" -> LocaleListCompat.forLanguageTags("en")
                    else -> LocaleListCompat.getEmptyLocaleList()
                }
                AppCompatDelegate.setApplicationLocales(localeList)
            }
        }

        setContent {
            val appTheme by viewModel.appTheme.collectAsState()
            MentalityScopeTheme(appTheme = appTheme) {
                MainScreen(viewModel)
            }
        }
    }

    private fun checkAndRequestPermissions() {
        if (!permissionManager.canDrawOverlays()) {
            permissionManager.requestDrawOverlayPermission()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

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
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
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
                    icon = { Icon(Icons.Default.List, contentDescription = "Configs") },
                    label = { Text("Конфиги") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Настройки") }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                0 -> HomeScreen(viewModel)
                1 -> ConfigsScreen(viewModel, onNavigateToHome = { selectedTab = 0 })
                2 -> SettingsScreen(viewModel)
            }
        }
    }
}

