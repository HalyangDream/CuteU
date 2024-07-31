package com.cute.mine.state

import com.cute.basic.UserState
import com.cute.logic.http.response.profile.ProfileAlbum
import com.cute.logic.http.response.profile.ProfileDetail
import com.cute.mine.intent.ProfileIntent

sealed class ProfileState : UserState {

    data class MyProfileState(val profile: ProfileDetail?) : ProfileState()

    data class UpdateAlbumState(val album: ProfileAlbum?) : ProfileState()

    data class DeleteAlbumState(val album: ProfileAlbum?) : ProfileState()

    data class UpdateIntroductionState(val state: Boolean) : ProfileState()

    data class UpdateNickNameState(val state: Boolean) : ProfileState()

    data class UpdateGenderState(val state: Boolean) : ProfileState()

    data class UpdateAgeState(val state: Boolean) : ProfileState()

    data class UpdateHeightState(val state: Boolean) : ProfileState()

    data class UpdateAvatarState(val state: Boolean) : ProfileState()

    data class Uploading(val isLoading: Boolean) : ProfileState()

}