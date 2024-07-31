package com.cute.mine

import android.content.Intent
import android.view.LayoutInflater
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.cute.baselogic.userDataStore
import com.cute.basic.BaseActivity
import com.cute.basic.util.StatusUtils
import com.cute.im.IMCore
import com.cute.im.service.UserService
import com.cute.mine.databinding.ActivitySettingBinding
import com.cute.mine.dialog.AccountInfoDialog
import com.cute.mine.dialog.LanguageDialog
import com.cute.tool.Toaster
import com.cute.basic.language.LanguagesConfig
import com.cute.basic.language.MultiLanguages
import com.cute.mine.bean.Language
import com.cute.uibase.Constant
import com.cute.uibase.DefaultLoadingDialog
import com.cute.uibase.WebViewActivity
import com.cute.uibase.databinding.LayoutTitleBarBinding
import com.cute.uibase.google.GoogleFeature
import com.cute.uibase.route.RoutePage
import com.cute.uibase.route.RouteSdk
import java.util.Locale

class SettingActivity : BaseActivity<ActivitySettingBinding>() {

    private lateinit var titleBinding: LayoutTitleBarBinding
    private lateinit var launcher: ActivityResultLauncher<IntentSenderRequest>

    private val loadingDialog: DefaultLoadingDialog by lazy { DefaultLoadingDialog() }
    private val dialog = AccountInfoDialog()

    override fun initViewBinding(layout: LayoutInflater): ActivitySettingBinding {
        return ActivitySettingBinding.inflate(layout)
    }

    override fun initView() {
        titleBinding = LayoutTitleBarBinding.bind(viewBinding.root)
        StatusUtils.setImmerseLayout(titleBinding.flTitle, this)
        titleBinding.tvTitle.text = getString(com.cute.uibase.R.string.str_setting)
        titleBinding.ivNavBack.setOnClickListener {
            finish()
        }

        viewBinding.tvLogout.setOnClickListener {
            reLogin()
        }

        viewBinding.tvCheckUpdate.setOnClickListener {
            checkGoogleUpdate()
        }

        viewBinding.tvPrivacyPolicy.setOnClickListener {
            WebViewActivity.startWebView(it.context, Constant.PRIVACY_AGREEMENT)
        }

        viewBinding.tvUserAgreement.setOnClickListener {
            WebViewActivity.startWebView(it.context, Constant.USER_AGREEMENT)
        }
        viewBinding.tvBlackUser.setOnClickListener {
            val intent = Intent(it.context, BlackUserActivity::class.java)
            startActivity(intent)
        }

        viewBinding.tvLanguage.setOnClickListener {
            showLanguageDialog()
        }

        viewBinding.tvAccount.setOnClickListener {
            dialog.showDialog(it.context, null)
            dialog.setDeleteAccountCallback {
                dialog.dismissDialog()
                reLogin()
            }
        }

        viewBinding.tvAbout.setOnClickListener {
            val intent = Intent(it.context, AboutActivity::class.java)
            startActivity(intent)
        }

        launcher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult(),
            ActivityResultCallback {
            })
    }

    private fun showLanguageDialog() {
        val languageDialog = LanguageDialog()
        languageDialog.setOnSelectedListener { position, language ->
            changeLanguage(position, language)
        }
        languageDialog.showDialog(this, null)
    }

    private fun changeLanguage(position: Int, language: Language) {
        // 选择语言
        LanguagesConfig.setSystemLocale(this, position == 0)
        val locale = Locale(language.code, language.country)
        val restart: Boolean = MultiLanguages.setAppLanguage(this, locale)
        if (restart) {
            RouteSdk.navigationActivity(
                RoutePage.Main.MAIN_PAGE,
                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            )
        }
    }

    private fun reLogin() {
        userDataStore.clear()
        IMCore.getService(UserService::class.java).logout()
        finish()
        RouteSdk.navigationLoginActivity()
    }

    private fun checkGoogleUpdate() {
        loadingDialog.showDialog(this, null)
        GoogleFeature.checkAppUpdate(this) { result, manager, info ->
            loadingDialog.dismissDialog()
            if (result) {
                GoogleFeature.startUpdateApp(manager, info, launcher)
            } else {
                Toaster.showShort(this, com.cute.uibase.R.string.str_already_latest_version)
            }
        }
    }
}