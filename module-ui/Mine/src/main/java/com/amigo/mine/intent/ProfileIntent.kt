package com.amigo.mine.intent

import com.amigo.basic.UserIntent
import com.amigo.logic.http.Gender
import com.amigo.logic.http.response.profile.ProfileAlbum
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