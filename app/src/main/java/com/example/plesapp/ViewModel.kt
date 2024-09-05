package com.example.plesapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class UserViewModel() : ViewModel() {
    private val _userState = MutableStateFlow<User?>(null)
    val userState: StateFlow<User?> = _userState.asStateFlow()

    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token.asStateFlow()

    fun getIdPartnert(): String? {
        return userState.value?.id
    }

    fun logout() {
        viewModelScope.launch {
            _userState.value = null

        }
    }

    fun setUser(user: User) {
        viewModelScope.launch {
            _userState.emit(user)
        }
    }
}
data class User(val id: String?, val name: String?, val email: String?, val phone: String?, val nameUserApp: String)
