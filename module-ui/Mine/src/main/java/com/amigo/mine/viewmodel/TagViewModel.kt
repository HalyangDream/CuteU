package com.amigo.mine.viewmodel

import androidx.lifecycle.viewModelScope
import com.amigo.basic.BaseMVIModel
import com.amigo.logic.http.model.ProfileRepository
import com.amigo.logic.http.response.profile.TagSubItem
import com.amigo.mine.intent.TagIntent
import com.amigo.mine.state.TagState
import kotlinx.coroutines.launch

class TagViewModel : BaseMVIModel<TagIntent, TagState>() {

    private val profileRepository = ProfileRepository()

    override fun processIntent(intent: TagIntent) {
        when (intent) {
            is TagIntent.GetTagList -> handleTagList()

            is TagIntent.UpdateTagList -> updateTag(intent.selectedTags)
        }
    }

    private fun updateTag(selectedTags: List<TagSubItem>) {
        viewModelScope.launch {
            val tagIdList = mutableListOf<String>()
            selectedTags.forEach {
                tagIdList.add(it.id.toString())
            }
            val response = profileRepository.updateTag(tagIdList)
            if (response.isSuccess) {
                setState(TagState.UpdateTagList(true))
            } else {
                setState(TagState.UpdateTagList(false))
            }
        }
    }

    private fun handleTagList() {
        viewModelScope.launch {
            val response = profileRepository.getTagList()
            if (response.isSuccess) {
                val list = response.data?.list
                val subItemList = mutableListOf<TagSubItem>()
                list?.forEach{tagItem->
                    tagItem.subLabel.add(0, TagSubItem(-999, tagItem.title, false, ""))
                    subItemList.addAll(tagItem.subLabel)
                }
                setState(TagState.TagList(subItemList))
            }
        }
    }


}