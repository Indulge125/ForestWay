package com.example.carbonroadapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carbonroadapplication.data.UserDao
import com.example.carbonroadapplication.data.UserEntity
import com.example.carbonroadapplication.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val sessionManager: SessionManager,
    private val userDao: UserDao
) : ViewModel() {

    // UI 状态
    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _errorMsg = MutableStateFlow<String?>(null)
    val errorMsg = _errorMsg.asStateFlow()

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess = _loginSuccess.asStateFlow()

    private val _isRegisterMode = MutableStateFlow(false)
    val isRegisterMode = _isRegisterMode.asStateFlow()

    fun onUsernameChange(input: String) {
        _username.value = input
        _errorMsg.value = null // 输入时清除错误提示
    }

    fun onPasswordChange(input: String) {
        _password.value = input
        _errorMsg.value = null
    }
    
    fun toggleMode() {
        _isRegisterMode.value = !_isRegisterMode.value
        _errorMsg.value = null
    }

    fun submit() {
        if (_isRegisterMode.value) {
            register()
        } else {
            login()
        }
    }

    private fun login() {
        val user = _username.value.trim()
        val pass = _password.value.trim()

        if (user.isBlank() || pass.isBlank()) {
            _errorMsg.value = "账号或密码不能为空"
            return
        }

        viewModelScope.launch {
            try {
                val userEntity = userDao.getUserByUsername(user)
                if (userEntity == null) {
                    _errorMsg.value = "用户不存在，请先注册"
                    return@launch
                }

                if (userEntity.passwordHash == pass) {
                    // 登录成功
                    sessionManager.saveLoginSession(user)
                    _loginSuccess.value = true
                } else {
                    // 登录失败
                    _errorMsg.value = "密码错误"
                }
            } catch (e: Exception) {
                _errorMsg.value = "登录异常: ${e.message}"
            }
        }
    }

    private fun register() {
        val user = _username.value.trim()
        val pass = _password.value.trim()

        if (user.isBlank() || pass.isBlank()) {
            _errorMsg.value = "账号或密码不能为空"
            return
        }

        viewModelScope.launch {
            try {
                val existingUser = userDao.getUserByUsername(user)
                if (existingUser != null) {
                    _errorMsg.value = "用户已存在，请直接登录"
                    return@launch
                }

                val newUser = UserEntity(username = user, passwordHash = pass)
                userDao.insertUser(newUser)

                // 注册成功自动登录
                sessionManager.saveLoginSession(user)
                _loginSuccess.value = true

            } catch (e: Exception) {
                _errorMsg.value = "注册失败: ${e.message}"
            }
        }
    }
    
    // 退出登录逻辑也放在这，或者 ProfileViewModel
    fun logout() {
        sessionManager.logout()
    }
}
