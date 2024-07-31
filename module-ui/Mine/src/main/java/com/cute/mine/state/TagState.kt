package com.cute.mine.state

import com.cute.basic.UserState
import com.cute.logic.http.response.profile.TagSubItem

sealed class TagState : UserState {

    data class TagList(val list: MutableList<TagSubItem>) : TagState()

    data class UpdateTagList(val state:Boolean) : TagState()
}