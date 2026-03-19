package com.mentality.gamescope.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mentality.gamescope.data.repository.CrosshairRepository

/**
 * Factory для создания CrosshairViewModel с зависимостями
 */
class CrosshairViewModelFactory(
    private val context: Context,
    private val repository: CrosshairRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(CrosshairViewModel::class.java) -> {
                CrosshairViewModel(context, repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
