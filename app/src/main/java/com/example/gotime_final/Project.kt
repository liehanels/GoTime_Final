package com.example.gotime_final

import android.net.Uri

data class Project
    (
    val Name :String? = null,
    val Category :String? = null,
    val Description :String? = null,
    val StartDate :String? = null,
    val EndDate :String? = null,
    val MinHours :Int? = null,
    val MaxHours :Int? = null,
    val currentTotalHours :Int? = null,
    val imgPP :Uri? = null,
    val UID :String? = null
)