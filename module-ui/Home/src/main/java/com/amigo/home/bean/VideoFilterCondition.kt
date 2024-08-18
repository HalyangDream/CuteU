package com.amigo.home.bean

import com.amigo.logic.http.response.list.Filter

data class VideoFilterCondition(
    var feeling: Filter? = null,
    var language: Filter? = null,
    var region: Filter? = null,
    var country: Filter? = null
)
