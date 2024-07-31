package com.cute.mine.intent

import com.cute.basic.UserIntent
import com.cute.logic.http.response.profile.TagSubItem

sealed class TagIntent : UserIntent {

    object GetTagList : TagIntent()

    data class UpdateTagList(val selectedTags: List<TagSubItem>) : TagIntent()
}
