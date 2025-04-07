package com.example.plesapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class UserViewModel : ViewModel() {
    private val _userState = MutableStateFlow<User?>(null)
    val userState: StateFlow<User?> = _userState.asStateFlow()

    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token.asStateFlow()

    private val _productList = MutableStateFlow<List<Product>>(emptyList())
    val productList: StateFlow<List<Product>> = _productList.asStateFlow()

    fun getIdPartnert(): String? = userState.value?.id

    fun logout() {
        viewModelScope.launch {
            _userState.value = null
            _token.value = null
        }
    }

    fun setUser(user: User?) {
        viewModelScope.launch {
            if (user != null) {
                _userState.emit(user)
            } else {
                _userState.emit(null)
            }
        }
    }

    fun setToken(newToken: String?) {
        viewModelScope.launch {
            _token.emit(newToken)
        }
    }

    fun setProducts(products: List<Product>) {
        viewModelScope.launch {
            _productList.emit(products)
        }
    }

    // Métodos auxiliares
    fun getUserName(): String? = userState.value?.name
    fun getUserEmail(): String? = userState.value?.email
    fun isUserLoggedIn(): Boolean = userState.value != null


    data class User(
        val id: String? = null,
        val name: String,
        val email: String,
        val phone: String,
        val domicilio: String?,
        val sexo: String,
        val curp: String,
        val fechaNacimiento: String,
        val tieneTarjetaFisica: Boolean,
        val esUsuarioApp: Boolean,
        val barcode: String
    )



    data class Product(
        val id: Int,
        val name: String,
        val listPrice: Double,
        val qtyAvailable: Int
    )

}




