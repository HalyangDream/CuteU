package com.cute.mine.intent

import com.cute.basic.UserIntent
import com.cute.logic.http.Gender
import com.cute.logic.http.response.profile.ProfileAlbum
import java.io.File

sealed class ProfileIntent : UserIntent {

    object GetMyProfile : ProfileIntent()

    data class UpdateAlbum(val isVideo: Boolean, val file: File) : ProfileIntent()

    data class DeleteAlbum(val album: ProfileAlbum) : ProfileIntent()

    data class UpdateName(val name: String) : ProfileIntent()

    data class UpdateAvatar(val avatarFile: File) : ProfileIntent()

    data class UpdateGender(val gender: Gender) : ProfileIntent()

    data class UpdateAge(val age: Int) : ProfileIntent()

    data class UpdateHeight(val height: String) : ProfileIntent()

    data class UpdateIntroduction(val introduction: String) : ProfileIntent()
}