package com.example.carbonroadapplication.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carbonroadapplication.data.TripDao
import com.example.carbonroadapplication.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

// 勋章数据模型
data class BadgeUI(
    val id: String,
    val name: String,
    val desc: String,
    val color: Long,
    val isUnlocked: Boolean = false // 核心字段：是否解锁
)

// 等级规则
data class LevelDef(
    val level: Int,
    val title: String,
    val reqExp: Int,
    val desc: String
)

data class UserProfileState(
    val userName: String = "环保卫士",
    val avatarUri: Uri? = null,
    val level: Int = 1,
    val levelTitle: String = "新手守护者",
    val currentExp: Int = 0,
    val maxExp: Int = 100,
    val totalTrees: Int = 0,      // 核心字段：树木数量
    val totalCarbonKg: Double = 0.0,
    val badges: List<BadgeUI> = emptyList(), // 核心字段：勋章列表

    // 弹窗状态
    val selectedBadge: BadgeUI? = null,
    val showBadgeDialog: Boolean = false,
    val showLevelDialog: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val tripDao: TripDao,
    private val sessionManager: SessionManager, // 必须注入 SessionManager 以区分用户
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserProfileState())
    val uiState = _uiState.asStateFlow()

    // 1-10级 升级规则表
    val levelRules = listOf(
        LevelDef(1, "新手守护者", 0, "踏上环保之旅的第一步"),
        LevelDef(2, "森林游侠", 100, "初识森林，开始探索"),
        LevelDef(3, "丛林探险家", 300, "深入丛林，发现自然之美"),
        LevelDef(4, "大地德鲁伊", 600, "与大地连接，感受生命律动"),
        LevelDef(5, "生态领航员", 1000, "引领低碳风尚，做出表率"),
        LevelDef(6, "自然之子", 1500, "完全融入自然，守护一方水土"),
        LevelDef(7, "荒野行者", 2200, "足迹遍布荒野，传播绿色信念"),
        LevelDef(8, "地球卫士", 3000, "拥有强大的力量守护地球"),
        LevelDef(9, "盖亚祭司", 4000, "聆听地球母亲的声音"),
        LevelDef(10, "碳中和传奇", 5500, "你的名字将被铭刻在绿色史册")
    )

    init {
        loadUserProfile()
        loadAvatarFromPrefs()
    }

    private fun loadUserProfile() {
        // 1. 获取当前登录账号
        val currentUser = sessionManager.getUsername() ?: "unknown"

        viewModelScope.launch {
            // 2. 实时监听数据库的三项数据 (减排量、里程、次数)
            combine(
                tripDao.getTotalCarbon(currentUser),
                tripDao.getTotalDistance(currentUser),
                tripDao.getTripCount(currentUser)
            ) { carbon, distanceMeters, count ->
                Triple(carbon, distanceMeters, count)
            }.collect { (totalCarbon, totalDistanceMeters, totalCount) ->

                // === A. 计算经验与等级 ===
                val totalExp = (totalCarbon * 10).toInt() // 1kg = 10经验
                val (currentLevel, nextLevelExp) = calculateLevelInfo(totalExp)
                val currentTitle = levelRules.find { it.level == currentLevel }?.title ?: "新手守护者"

                // === B. 计算树木数量 ===
                // 规则：每 10kg 碳排 = 1 棵树
                val trees = (totalCarbon / 10.0).toInt()

                // === C. 计算勋章解锁状态 ===
                val newBadges = listOf(
                    // 1. 首程纪念：只要跑过 1 次
                    BadgeUI(
                        id = "first_trip",
                        name = "首程纪念",
                        desc = "完成第一次环保出行",
                        color = 0xFFFFD700, // 金色
                        isUnlocked = totalCount > 0
                    ),
                    // 2. 低碳达人：累计减排 5kg
                    BadgeUI(
                        id = "low_carbon",
                        name = "低碳达人",
                        desc = "累计减排超过 5kg",
                        color = 0xFFCD7F32, // 铜色
                        isUnlocked = totalCarbon >= 5.0
                    ),
                    // 3. 植树专家：拥有 5 棵树 (即 50kg 减排)
                    BadgeUI(
                        id = "tree_planter",
                        name = "植树专家",
                        desc = "拥有 5 棵以上的虚拟树木 (累计减排50kg)",
                        color = 0xFF2E7D32, // 绿色
                        isUnlocked = trees >= 5
                    ),
                    // 4. 百公里：里程 > 100km (100,000米)
                    BadgeUI(
                        id = "marathon",
                        name = "百公里",
                        desc = "累计里程超过 100km",
                        color = 0xFFC0C0C0, // 银色
                        isUnlocked = (totalDistanceMeters / 1000.0) >= 100.0
                    )
                )

                // === D. 更新 UI 状态 ===
                _uiState.value = _uiState.value.copy(
                    userName = currentUser,
                    totalCarbonKg = totalCarbon,
                    totalTrees = trees,
                    level = currentLevel,
                    levelTitle = currentTitle,
                    currentExp = totalExp,
                    maxExp = nextLevelExp,
                    badges = newBadges
                )
            }
        }
    }

    // 计算等级辅助方法
    private fun calculateLevelInfo(currentExp: Int): Pair<Int, Int> {
        val match = levelRules.findLast { currentExp >= it.reqExp } ?: levelRules.first()
        val nextLevel = levelRules.find { it.level == match.level + 1 }
        val nextExp = nextLevel?.reqExp ?: match.reqExp
        return Pair(match.level, nextExp)
    }

    // 头像相关
    fun updateAvatar(uri: Uri) {
        _uiState.value = _uiState.value.copy(avatarUri = uri)
        saveAvatarToPrefs(uri.toString())
    }
    private fun saveAvatarToPrefs(uriString: String) {
        val currentUser = sessionManager.getUsername() ?: return
        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("avatar_uri_$currentUser", uriString).apply()
    }
    private fun loadAvatarFromPrefs() {
        val currentUser = sessionManager.getUsername() ?: return
        val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val uriString = prefs.getString("avatar_uri_$currentUser", null)
        if (uriString != null) _uiState.value = _uiState.value.copy(avatarUri = Uri.parse(uriString))
        else _uiState.value = _uiState.value.copy(avatarUri = null) // 默认状态
    }

    // 弹窗控制
    fun showBadgeDetails(badge: BadgeUI) {
        _uiState.value = _uiState.value.copy(selectedBadge = badge, showBadgeDialog = true)
    }
    fun dismissBadgeDialog() {
        _uiState.value = _uiState.value.copy(showBadgeDialog = false, selectedBadge = null)
    }
    fun showLevelRules() {
        _uiState.value = _uiState.value.copy(showLevelDialog = true)
    }
    fun dismissLevelDialog() {
        _uiState.value = _uiState.value.copy(showLevelDialog = false)
    }
}