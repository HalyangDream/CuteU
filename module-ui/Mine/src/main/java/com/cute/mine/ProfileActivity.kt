package com.cute.mine

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter4.BaseQuickAdapter
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.luck.picture.lib.config.PictureMimeType
import com.cute.analysis.Analysis
import com.cute.baselogic.statusDataStore
import com.cute.basic.BaseModelActivity
import com.cute.basic.util.StatusUtils
import com.cute.logic.http.Gender
import com.cute.logic.http.response.profile.ProfileAlbum
import com.cute.logic.http.response.profile.ProfileDetail
import com.cute.logic.http.response.user.UserTag
import com.cute.mine.adapter.ProfileAlbumAdapter
import com.cute.mine.adapter.ProfileTagAdapter
import com.cute.mine.databinding.ActivityEditProfileBinding
import com.cute.mine.dialog.AgeDialog
import com.cute.mine.dialog.HeightDialog
import com.cute.mine.dialog.UgcWarningDialog
import com.cute.mine.intent.ProfileIntent
import com.cute.mine.state.ProfileState
import com.cute.mine.viewmodel.ProfileViewModel
import com.cute.picture.LocalAlbumManager
import com.cute.picture.PictureMimeEnum
import com.cute.tool.Toaster
import com.cute.uibase.DefaultLoadingDialog
import com.cute.uibase.R
import com.cute.uibase.databinding.LayoutTitleBarBinding
import com.cute.uibase.dialog.GenderDialog
import com.cute.uibase.gone
import com.cute.uibase.media.preview.PicturePreviewActivity
import com.cute.uibase.media.preview.VideoPreviewActivity
import com.cute.uibase.visible
import java.io.File

class ProfileActivity : BaseModelActivity<ActivityEditProfileBinding, ProfileViewModel>() {

    private lateinit var titleBinding: LayoutTitleBarBinding
    private val loadingDialog: DefaultLoadingDialog = DefaultLoadingDialog()

    private var albumAdapter: ProfileAlbumAdapter? = null
    private var profileTagAdapter: ProfileTagAdapter? = null
    private var profileDetail: ProfileDetail? = null

    private var uploadFileType = UPLOAD_ALBUM

    private companion object {
        const val REQUEST_CODE_EDIT_NAME = 111
        const val REQUEST_CODE_EDIT_INTRO = 222
        const val REQUEST_CODE_EDIT_TAG = 333

        const val UPLOAD_AVATAR = 444
        const val UPLOAD_ALBUM = 555
    }

    override fun initViewBinding(layout: LayoutInflater): ActivityEditProfileBinding {
        return ActivityEditProfileBinding.inflate(layout)
    }

    override fun initView() {
        StatusUtils.setImmerseLayout(viewBinding.root, this)
        titleBinding = LayoutTitleBarBinding.bind(viewBinding.root)
        titleBinding.tvTitle.text = getString(com.cute.uibase.R.string.str_edit_profile)
        titleBinding.ivNavBack.setOnClickListener {
            finish()
        }
        viewBinding.apply {
            profileTagAdapter = ProfileTagAdapter(this@ProfileActivity)
            rvTags.layoutManager =
                FlexboxLayoutManager(this@ProfileActivity, FlexDirection.ROW, FlexWrap.WRAP)
            rvTags.adapter = profileTagAdapter
            albumAdapter = ProfileAlbumAdapter(this@ProfileActivity)
            rvPhotos.layoutManager = GridLayoutManager(this@ProfileActivity, 3)
            rvPhotos.adapter = albumAdapter
            itemNickName.setOnClickListener {
                val intent = Intent(this@ProfileActivity, EditNameActivity::class.java)
                intent.putExtra("nick_name", profileDetail?.name)
                startActivityForResult(
                    intent,
                    REQUEST_CODE_EDIT_NAME
                )
            }

            profileTagAdapter?.setOnTagClick {
                startTagActivity()
            }

            itemIntroduction.setOnClickListener {
                val intent = Intent(this@ProfileActivity, EditIntroductionActivity::class.java)
                intent.putExtra("intro", profileDetail?.sign)
                startActivityForResult(
                    intent, REQUEST_CODE_EDIT_INTRO
                )
            }

            itemGender.setOnClickListener {
                showGenderDialog()
            }

            itemAge.setOnClickListener {
                showAgeDialog()
            }

            itemHeight.setOnClickListener {
                showHeightDialog()
            }

            itemPortrait.setOnClickListener {
                uploadFileType = UPLOAD_AVATAR
                openAlbum(true, false, PictureMimeEnum.IMAGE)
            }
        }

        viewModel.processIntent(ProfileIntent.GetMyProfile)
        viewModel.observerState {
            when (it) {
                is ProfileState.MyProfileState -> {
                    handleProfileInfo(it.profile)
                }

                is ProfileState.UpdateAlbumState -> {
                    handleUpdateAlbum(it.album)
                }

                is ProfileState.DeleteAlbumState -> {
                    it.album?.let { album ->
                        albumAdapter?.remove(album)
                    }
                }

                is ProfileState.UpdateGenderState -> {
                    toastResult(it.state)
                }

                is ProfileState.UpdateAgeState -> {
                    toastResult(it.state)
                }

                is ProfileState.UpdateHeightState -> {
                    toastResult(it.state)
                }

                is ProfileState.UpdateAvatarState -> {
                    toastResult(it.state)
                    refreshInfo()
                }

                is ProfileState.Uploading -> {
                    if (it.isLoading) {
                        loadingDialog.showDialog(this, null)
                    } else {
                        loadingDialog.dismissDialog()
                    }
                }

                else -> {}
            }
        }

        albumAdapter?.setAlbumAction(openAlbum = {
            if (statusDataStore.hasAgreeUgc()) {
                uploadFileType = UPLOAD_ALBUM
                openAlbum(false, true, PictureMimeEnum.ALL)
            } else {
                showUgcWarningDialog()
            }
        }, deleteAlbum = {
            deleteAlbum(it)
        }, showAlbum = {
            showAlbum(it)
        }, playVideo = {
            playVideo(it)
        })
    }

    private fun startTagActivity() {
        startActivityForResult(Intent(this, TagActivity::class.java), REQUEST_CODE_EDIT_TAG)
    }

    private fun toastResult(result: Boolean) {
        Toaster.showShort(
            context = this,
            if (result) getString(R.string.str_save_success) else getString(R.string.str_save_failed)
        )
    }


    private fun showHeightDialog() {
        val currentHeight = viewBinding.itemHeight.getRightContent()
        val heightDialog = HeightDialog()
        heightDialog.setOnSelectedListener {
            viewBinding.itemHeight.setRightContent(it)
            viewModel.processIntent(ProfileIntent.UpdateHeight(it))
        }
        heightDialog.showDialog(this, Bundle().apply {
            putString("current_height", currentHeight)
        })
    }

    private fun showAgeDialog() {
        val currentAge = viewBinding.itemAge.getRightContent().toIntOrNull() ?: 0
        val ageDialog = AgeDialog()
        ageDialog.setOnSelectedListener {
            viewBinding.itemAge.setRightContent(it.toString())
            viewModel.processIntent(ProfileIntent.UpdateAge(it))
        }
        ageDialog.showDialog(this, Bundle().apply {
            putInt("current_age", currentAge)
        })
    }

    private fun showGenderDialog() {
        val genderDialog = GenderDialog()
        genderDialog.hideBoth()
        genderDialog.setOnSelectGenderListener {
            viewBinding.itemGender.setRightContent(it.name)
            viewModel.processIntent(ProfileIntent.UpdateGender(it))
            refreshInfo()
        }
        val gender = when (profileDetail?.gender) {
            3 -> Gender.MALE
            2 -> Gender.FEMALE
            else -> Gender.UNKNOWN
        }
        genderDialog.setGender(gender)
        genderDialog.showDialog(this, null)
    }

    private fun showUgcWarningDialog() {
        val ugcWarningDialog = UgcWarningDialog()
        ugcWarningDialog.setOnConfirmAction {
            uploadFileType = UPLOAD_ALBUM
            openAlbum(false, true, PictureMimeEnum.ALL)
        }
        ugcWarningDialog.showDialog(this, null)
        statusDataStore.saveAgreeUgc(true)
    }

    private fun handleUpdateAlbum(album: ProfileAlbum?) {
        album?.let {
            albumAdapter?.add(it)
        }
    }


    private fun handleProfileInfo(profile: ProfileDetail?) {
        profile?.let { info ->
            viewBinding.apply {
                updateBaseInfo(info, profile)
                updateAlbumInfo(info)
                updateTags(info)
            }
        }
    }

    private fun updateTags(info: ProfileDetail) {
        if (info.tag.isNullOrEmpty()) {
            viewBinding.llAddYourTag.visible()
            viewBinding.rvTags.gone()
            viewBinding.ivAddTags.gone()
            viewBinding.llAddYourTag.setOnClickListener {
                startTagActivity()
            }
        } else {
            viewBinding.ivAddTags.visible()
            viewBinding.llAddYourTag.gone()
            viewBinding.rvTags.visible()
            profileTagAdapter?.submitList(info.tag)
            viewBinding.ivAddTags.setOnClickListener {
                startTagActivity()
            }
        }
    }


    private fun updateAlbumInfo(info: ProfileDetail) {
        val profileAlbums = info.album ?: mutableListOf()
        profileAlbums.add(0, ProfileAlbum(0, "", "",false))
        albumAdapter?.submitList(profileAlbums)
    }

    private fun ActivityEditProfileBinding.updateBaseInfo(
        info: ProfileDetail,
        profile: ProfileDetail
    ) {
        profileDetail = profile
        itemPortrait.setRightImage(info.avatar)
        itemNickName.setRightContent(info.name)
        val gender = when (profile.gender) {
            Gender.MALE.value -> getString(R.string.str_male)
            Gender.FEMALE.value -> getString(R.string.str_female)
            else -> getString(R.string.str_unknown)
        }
        itemGender.setRightContent(gender)
        itemAge.setRightContent(info.age.toString())
        itemHeight.setRightContent(info.height)
    }

    private fun playVideo(it: ProfileAlbum) {
        VideoPreviewActivity.startPreview(this, it.imageUrl)
    }

    private fun showAlbum(it: ProfileAlbum) {
        PicturePreviewActivity.startPreview(
            this,
            arrayOf(it.imageUrl),
            0
        )
    }

    private fun deleteAlbum(it: ProfileAlbum) {
        viewModel.processIntent(ProfileIntent.DeleteAlbum(it))
    }

    private fun openAlbum(isCrop: Boolean, isCompress: Boolean, type: PictureMimeEnum) {
        requestMediaPermission(onDenied = {
            Toaster.showShort(this, R.string.str_please_grant_permission)
        }, onAllGranted = {
            LocalAlbumManager.Builder()
                .setActivity(this)
                .setMaxSelectNum(1)
                .setEnableCamera(true)
                .setEnableCompress(isCompress)
                .setEnableCrop(isCrop)
                .setMimeType(type)
                .openGallery()
        })
    }

    private fun refreshInfo() {
        viewModel.processIntent(ProfileIntent.GetMyProfile)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_EDIT_NAME -> {
                if (resultCode == RESULT_OK) {
                    refreshInfo()
                }
            }

            REQUEST_CODE_EDIT_INTRO -> {
                if (resultCode == RESULT_OK) {
                    refreshInfo()
                }
            }

            REQUEST_CODE_EDIT_TAG -> {
                if (resultCode == RESULT_OK) {
                    refreshInfo()
                }
            }
        }
        val list = LocalAlbumManager.paresData(requestCode, resultCode, data)
        if (!list.isNullOrEmpty()) {
            val localMedia = list[0]
            when (uploadFileType) {
                UPLOAD_AVATAR -> {
                    val filePath =
                        if (!localMedia.cutPath.isNullOrEmpty()) localMedia.cutPath else localMedia.realPath
                    viewModel.processIntent(ProfileIntent.UpdateAvatar(File(filePath)))
                }

                UPLOAD_ALBUM -> {
                    val isVideo = PictureMimeType.isHasVideo(localMedia.mimeType)
                    if (isVideo) {
                        viewModel.processIntent(
                            ProfileIntent.UpdateAlbum(
                                true,
                                File(localMedia.realPath)
                            )
                        )
                    } else {
                        val filePath =
                            if (!localMedia.compressPath.isNullOrEmpty()) localMedia.compressPath else localMedia.realPath
                        viewModel.processIntent(
                            ProfileIntent.UpdateAlbum(
                                false,
                                File(filePath)
                            )
                        )
                    }
                }
            }
        }
    }
}