package com.amigo.mine

import android.content.Intent
import android.view.LayoutInflater
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.amigo.baselogic.userDataStore
import com.amigo.basic.BaseActivity
import com.amigo.basic.util.StatusUtils
import com.amigo.im.IMCore
import com.amigo.im.service.UserService
import com.amigo.mine.databinding.ActivitySettingBinding
import com.amigo.mine.dialog.AccountInfoDialog
import com.amigo.mine.dialog.LanguageDialog
import com.amigo.tool.Toaster
import com.amigo.basic.language.LanguagesConfig
import com.amigo.basic.language.MultiLanguages
import com.amigo.mine.bean.Language
import com.amigo.uibase.Constant
import com.amigo.uibase.DefaultLoadingDialog
import com.amigo.uibase.WebViewActivity
import com.amigo.uibase.databinding.LayoutTitleBarBinding
import com.amigo.uibase.google.GoogleFeature
import com.amigo.uibase.route.RoutePage
import com.amigo.uibase.route.RouteSdk
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
        titleBinding.tvTitle.text = getString(com.amigo.uibase.R.string.str_setting)
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
                Toaster.showShort(this, com.amigo.uibase.R.string.str_already_latest_version)
            }
        }
    }
}