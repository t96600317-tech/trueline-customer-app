package com.example.truelineapp.network.partner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class DiscoverViewModel(private val repository: PartnerRepository) : ViewModel() {

    private val _partners = MutableStateFlow<List<PartnerData>>(emptyList())
    val partners: StateFlow<List<PartnerData>> = _partners.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedLanguage = MutableStateFlow("All")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    private val masterList = mutableListOf<PartnerData>()
    private var searchJob: Job? = null

    init {
        fetchPartners()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        applyLocalFilter() // Instant UI feedback
        
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300) // Debounce API call
            fetchPartners()
        }
    }

    fun onLanguageSelected(language: String) {
        _selectedLanguage.value = language
        applyLocalFilter() // Instant UI feedback
        fetchPartners()
    }

    private fun applyLocalFilter() {
        val query = _searchQuery.value
        val lang = _selectedLanguage.value

        _partners.value = masterList.filter { partner ->
            val matchesLanguage = if (lang == "All") true
                                  else partner.languages.any { it.equals(lang, ignoreCase = true) }
            val matchesSearch = if (query.isBlank()) true
                                else partner.name.contains(query, ignoreCase = true)
            matchesLanguage && matchesSearch
        }
    }

    fun fetchPartners() {
        viewModelScope.launch {
            _isLoading.value = true
            val language = if (_selectedLanguage.value == "All") null else _selectedLanguage.value
            val query = if (_searchQuery.value.isBlank()) null else _searchQuery.value
            
            repository.getPartners(language, query).onSuccess {
                masterList.clear()
                masterList.addAll(it)
                applyLocalFilter()
            }.onFailure {
                println("DEV MODE: Partner fetch failed. API error: ${it.message}")
                // Fallback for UI testing
                if (masterList.isEmpty()) {
                    val mockData = listOf(
                        PartnerData(
                            "1", "Afreen", "Joy Helper", "", "", "Always here to listen.", 
                            listOf("Hindi", "Bengali"), 11.0, 4.5, 38, "online", true
                        ),
                        PartnerData(
                            "2", "Ahmedi", "Calm Friend", "", "", "Peaceful listener.", 
                            listOf("Hindi"), 11.0, 4.8, 24, "online", false
                        ),
                        PartnerData(
                            "3", "Saima", "Calm Friend", "", "", "Understanding friend.", 
                            listOf("Hindi"), 11.0, 4.2, 15, "online", false
                        )
                    )
                    masterList.addAll(mockData)
                    applyLocalFilter()
                }
            }
            _isLoading.value = false
        }
    }
}
