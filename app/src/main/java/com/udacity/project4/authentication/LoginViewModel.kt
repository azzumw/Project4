package com.udacity.project4.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

class LoginViewModel:ViewModel() {
    enum class AuthenticationState{
        AUTHENTICATED,UNAUTHENTICATED
    }

    val authenticationState = FirebaseUserLiveData().map {
        if (it != null) {
            AuthenticationState.AUTHENTICATED
        } else {
            AuthenticationState.UNAUTHENTICATED
        }
    }
}