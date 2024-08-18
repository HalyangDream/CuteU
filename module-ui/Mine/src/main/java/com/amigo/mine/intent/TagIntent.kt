package com.amigo.mine.intent

import com.amigo.basic.UserIntent
import com.amigo.logic.http.response.profile.TagSubItem

sealed class TagIntent : UserIntent {

    object GetTagList : TagIntent()

    data class UpdateTagList(val selectedTags: List<TagSubItem>) : TagIntent()
}
