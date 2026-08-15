package com.example.truelineapp.network.partner

import kotlinx.serialization.Serializable

@Serializable
data class PartnerData(
    val id: String,
    val name: String,
    val title: String,
    val photo_url: String,
    val audio_sample_url: String,
    val bio: String,
    val languages: List<String>,
    val rate_per_min: Double,
    val rating_avg: Double,
    val rating_count: Int,
    val availability: String,
    val is_favourite: Boolean
)
