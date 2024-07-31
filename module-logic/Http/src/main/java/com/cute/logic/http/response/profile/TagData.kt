package com.cute.logic.http.response.profile

data class TagData(val list: MutableList<TagItem>)

data class TagItem(val title: String, val subLabel: MutableList<TagSubItem>)

data class TagSubItem(
    val id: Int,
    val name: String,
    var selected: Boolean,
    val icon: String,
)