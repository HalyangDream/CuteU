package com.amigo.mine.state

import com.amigo.basic.UserState
import com.amigo.logic.http.response.profile.TagSubItem

sealed class TagState : UserState {

    data class TagList(val list: MutableList<TagSubItem>) : TagState()

    data class UpdateTagList(val state:Boolean) : TagState()
}