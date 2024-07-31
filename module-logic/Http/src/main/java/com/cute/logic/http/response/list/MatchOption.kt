package com.cute.logic.http.response.list

data class MatchOption(
    val id: Long, val icon: String, val name: String, val price: String
)

data class MatchOptionResponse(val list: MutableList<MatchOption>?)
