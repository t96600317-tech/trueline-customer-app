package com.example.truelineapp.network.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfileData?>(null)
    val userProfile: StateFlow<UserProfileData?> = _userProfile.asStateFlow()

    private val _balance = MutableStateFlow(260.0)
    val balance: StateFlow<Double> = _balance.asStateFlow()

    private val _selectedLanguage = MutableStateFlow("en")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    fun fetchUserProfile() {
        viewModelScope.launch {
            repository.getUserProfile().onSuccess {
                _userProfile.value = it
                _balance.value = it.balance
                _selectedLanguage.value = it.user.language_pref
            }.onFailure {
                println("DEV MODE: User profile fetch failed. Using fallback values.")
            }
        }
    }

    fun updateLanguage(languageCode: String) {
        viewModelScope.launch {
            // Optimistic UI Update for Dev Mode/Offline
            _selectedLanguage.value = languageCode
            
            repository.updateLanguagePreference(languageCode).onFailure {
                println("DEV MODE: Language update API failed. Reverting or keeping local state.")
            }
        }
    }
}
