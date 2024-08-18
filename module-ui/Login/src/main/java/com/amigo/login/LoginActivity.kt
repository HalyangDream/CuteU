package com.amigo.login

import android.content.Intent
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.amigo.baselogic.userDataStore
import com.amigo.basic.BaseModelActivity
import com.amigo.basic.util.StatusUtils
import com.amigo.logic.http.response.account.AccountProfileInfo
import com.amigo.login.databinding.ActivityAppLoginBinding
import com.amigo.login.intent.LoginIntent
import com.amigo.login.state.LoginState
import com.amigo.tool.AppUtil
import com.amigo.tool.JwtAuthUtil
import com.amigo.tool.Toaster
import com.amigo.uibase.Constant
import com.amigo.uibase.DefaultLoadingDialog
import com.amigo.uibase.WebViewActivity
import com.amigo.uibase.media.VideoPlayer
import com.amigo.uibase.media.VideoPlayerManager
import com.amigo.uibase.route.RoutePage
import com.amigo.uibase.route.RouteSdk
import com.amigo.uibase.setThrottleListener


@Route(path = RoutePage.Login.LOGIN_PAGE)
class LoginActivity : BaseModelActivity<ActivityAppLoginBinding, LoginViewModel>() {


    private val loadingDialog: DefaultLoadingDialog = DefaultLoadingDialog()
    private val videoPlayer: VideoPlayer by lazy { VideoPlayerManager.getVideoPlayer(this) }
    private val googleSignClient by lazy(LazyThreadSafetyMode.NONE) {
        val serverId = resources.getIdentifier("google_server_id", "string", packageName)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestServerAuthCode(getString(serverId))
            .requestIdToken(getString(serverId))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(this, gso)
    }

    private val RC_SIGN_IN = 0x1417


    private val oneClickSpan = object : ClickableSpan() {
        override fun onClick(widget: View) {
            WebViewActivity.startWebView(this@LoginActivity, Constant.USER_AGREEMENT)
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = true
            ds.color = Color.WHITE
        }
    }

    private val twoClickSpan = object : ClickableSpan() {
        override fun onClick(widget: View) {
            WebViewActivity.startWebView(this@LoginActivity, Constant.PRIVACY_AGREEMENT)
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = true
            ds.color = Color.WHITE
        }
    }

    override fun initViewBinding(layout: LayoutInflater): ActivityAppLoginBinding {
        return ActivityAppLoginBinding.inflate(layout)
    }

    override fun initView() {
        StatusUtils.setImmerseLayout(viewBinding.flTitle, this)
        viewBinding.btnAccountLogin.setThrottleListener {
            val dialog = AccountLoginDialog()
            dialog.setAccountLoginListener { userName, password ->
                viewModel.processIntent(LoginIntent.AccountLogin(userName, password))
            }
            dialog.showDialog(it.context, null)

        }
        viewBinding.btnVisitorLogin.setOnClickListener {
            val token = JwtAuthUtil.jwtGenerate(AppUtil.getAndroidID(this))
            viewModel.processIntent(LoginIntent.VisitorLogin(token))
        }

        viewModel.observerState {
            when (it) {
                is LoginState.Logging -> loadingDialog.showDialog(this, null)
                is LoginState.SUCCESS -> bindSuccessUi(it.response)
                is LoginState.ERROR -> {
                    loadingDialog.dismissDialog()
                    Toaster.showShort(this, "${it.error}")
                }
            }
        }
        val originContent = getString(com.amigo.uibase.R.string.sign_notice)
        val firstIndex = originContent.indexOf("%s")
        val lastIndex = originContent.lastIndexOf("%s") - 2

        val oneContent = getString(com.amigo.uibase.R.string.str_terms_service)
        val twoContent = getString(com.amigo.uibase.R.string.str_privacy_policy)
        val displayContent = originContent.format(oneContent.lowercase(), twoContent.lowercase())


        val spannable = SpannableString(displayContent)
        spannable.setSpan(
            oneClickSpan,
            firstIndex,
            firstIndex + oneContent.length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            twoClickSpan,
            lastIndex + oneContent.length,
            displayContent.length,
            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
        )
        viewBinding.tvPolicy.text = spannable
        viewBinding.tvPolicy.movementMethod = LinkMovementMethod.getInstance()
    }


    override fun onDestroy() {
        super.onDestroy()
        videoPlayer.release()
        releaseCredentialManager()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val completedTask = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)
                // Signed in successfully, show authenticated UI.
                handleGoogleSignIn(account)
            } catch (e: ApiException) {
                loadingDialog.dismissDialog()
                // The ApiException status code indicates the detailed failure reason.
                // Please refer to the GoogleSignInStatusCodes class reference for more information.
                Toaster.showShort(this, "${e.statusMessage}")
            }
        }
    }

    /**
     * 谷歌登陆
     */
    private fun getGoogleAccount() {

        try {
            loadingDialog.showDialog(this, null)
            val account = GoogleSignIn.getLastSignedInAccount(this)
            if (account != null) {
                handleGoogleSignIn(account)
            } else {
                val signInIntent: Intent = googleSignClient.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            loadingDialog.dismissDialog()
        }

    }

    private fun handleGoogleSignIn(account: GoogleSignInAccount) {
        loadingDialog.dismissDialog()
//        if (!account.idToken.isNullOrEmpty()) {
//            viewModel.processIntent(LoginIntent.AccountLogin(account.idToken!!))
//        } else {
//            Toaster.showShort(this, "Google token is null")
//        }
    }

    private fun bindSuccessUi(response: AccountProfileInfo) {
        loadingDialog.dismissDialog()
        userDataStore.saveToken(response.token)
        userDataStore.saveUid(response.id)
        RouteSdk.navigationActivity(RoutePage.Main.MAIN_PAGE)
        finish()
    }

    private fun releaseCredentialManager() {
        googleSignClient.signOut()
    }
}