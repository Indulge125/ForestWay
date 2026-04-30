package com.example.carbonroadapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carbonroadapplication.data.TripDao
import com.example.carbonroadapplication.data.TripEntity
import com.example.carbonroadapplication.utils.SessionManager // 【新增】导入 SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest // 【新增】导入 flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    tripDao: TripDao,
    sessionManager: SessionManager
) : ViewModel() {
    
    // 1. 获取当前登录的用户名 (如果为空则设为 unknown，防止查出意外数据)
    private val currentUser = sessionManager.getUsername() ?: "unknown_user"

    // 2. 调用 DAO 时，一定要传入 currentUser
    val allTrips: StateFlow<List<TripEntity>> = tripDao.getAllTrips(currentUser)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        
    val totalCarbon: StateFlow<Double?> = tripDao.getTotalCarbon(currentUser)
         .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
}