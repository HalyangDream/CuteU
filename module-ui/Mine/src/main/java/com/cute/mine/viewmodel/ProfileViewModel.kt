package com.cute.mine.viewmodel

import androidx.lifecycle.viewModelScope
import com.cute.basic.BaseMVIModel
import com.cute.logic.http.Gender
import com.cute.logic.http.model.ProfileRepository
import com.cute.logic.http.model.UploadRepository
import com.cute.logic.http.response.tool.UploadResult
import com.cute.mine.intent.ProfileIntent
import com.cute.mine.state.ProfileState
import kotlinx.coroutines.launch
import java.io.File

class ProfileViewModel : BaseMVIModel<ProfileIntent, ProfileState>() {

    private val profileRepository = ProfileRepository()

    override fun processIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.GetMyProfile -> getMyProfile()
            is ProfileIntent.UpdateAlbum -> updateAlbum(intent.isVideo, intent.file)
            is ProfileIntent.DeleteAlbum -> deleteAlbum(intent)
            is ProfileIntent.UpdateName -> updateName(intent.name)
            is ProfileIntent.UpdateIntroduction -> updateIntroduction(intent.introduction)
            is ProfileIntent.UpdateGender -> updateGender(intent.gender)
            is ProfileIntent.UpdateAge -> updateAge(intent.age)
            is ProfileIntent.UpdateHeight -> updateHeight(intent.height)
            is ProfileIntent.UpdateAvatar -> updateAvatar(intent.avatarFile)
        }
    }

    private fun updateAvatar(avatarFile: File) {
        viewModelScope.launch {
            setState(ProfileState.Uploading(true))
            val uploadResult = UploadRepository.uploadPicture(avatarFile, avatarFile.name)
            if (uploadResult is UploadResult.Success) {
                val response = profileRepository.updateAvatar(uploadResult.fileUrl)
                setState(ProfileState.UpdateAvatarState(response.isSuccess))
            }
            setState(ProfileState.Uploading(false))
        }
    }

    private fun updateHeight(height: String) {
        viewModelScope.launch {
            val response = profileRepository.updateHeight(height)
            setState(ProfileState.UpdateHeightState(response.isSuccess))
        }
    }

    private fun updateAge(age: Int) {
        viewModelScope.launch {
            val response = profileRepository.updateAge(age)
            setState(ProfileState.UpdateAgeState(response.isSuccess))
        }
    }

    private fun updateGender(gender: Gender) {
        viewModelScope.launch {
            val response = profileRepository.updateGender(gender.value)
            setState(ProfileState.UpdateGenderState(response.isSuccess))
        }
    }

    private fun updateIntroduction(introduction: String) {
        viewModelScope.launch {
            val response = profileRepository.updateIntroduction(introduction)
            setState(ProfileState.UpdateIntroductionState(response.isSuccess))
        }
    }

    private fun updateName(name: String) {
        viewModelScope.launch {
            val response = profileRepository.updateNickName(name)
            setState(ProfileState.UpdateNickNameState(response.isSuccess))
        }
    }

    private fun deleteAlbum(intent: ProfileIntent.DeleteAlbum) {
        viewModelScope.launch {
            val response =
                profileRepository.deleteAlbum(intent.album.resourceId, intent.album.isVideo)
            if (response.isSuccess) {
                setState(ProfileState.DeleteAlbumState(intent.album))
            }
        }
    }

    private fun updateAlbum(isVideo: Boolean, file: File) {
        viewModelScope.launch {
            setState(ProfileState.Uploading(true))
            val result = if (isVideo) UploadRepository.uploadVideo(
                file,
                file.name
            ) else UploadRepository.uploadPicture(file, file.name)

            if (result is UploadResult.Success) {
                val uploadResponse = profileRepository.uploadAlbum(result.fileKey ?: "", isVideo)
                if (uploadResponse.isSuccess) {
                    setState(ProfileState.UpdateAlbumState(uploadResponse.data))
                } else {
                    setState(ProfileState.UpdateAlbumState(null))
                }
            }
            setState(ProfileState.Uploading(false))
        }
    }

    private fun getMyProfile() {
        viewModelScope.launch {
            val response = profileRepository.getProfileDetail()
            if (response.isSuccess) {
                setState(ProfileState.MyProfileState(response.data))
            }
        }
    }


}