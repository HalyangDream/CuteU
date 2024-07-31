package com.cute.home.bean

import com.cute.logic.http.response.list.Filter

data class VideoFilterCondition(
    var feeling: Filter? = null,
    var language: Filter? = null,
    var region: Filter? = null,
    var country: Filter? = null
)
