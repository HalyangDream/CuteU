package com.cute.basic

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.TypedArray
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import com.cute.basic.language.MultiLanguages
import com.cute.basic.util.StatusUtils


abstract class BaseActivity<V : ViewBinding> : AppCompatActivity() {


    lateinit var viewBinding: V

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating()) {
            val result = fixOrientation()
        }
        super.onCreate(savedInstanceState)
        StatusUtils.setStatusMode(true, this.window)
        // 防截屏
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        initPermissionLauncher()
        viewBinding = initViewBinding(layoutInflater)
        setContentView(viewBinding.root)
        initView()
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(MultiLanguages.attach(newBase))
    }


    abstract fun initViewBinding(layout: LayoutInflater): V

    abstract fun initView()


    private lateinit var multiplePermissionLauncher: ActivityResultLauncher<Array<String>>
    private fun initPermissionLauncher() {
        multiplePermissionLauncher =
            this.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
                var isAllGranted = true
                for (entry in result) {
                    if (!entry.value && isAllGranted) {
                        isAllGranted = false
                    }
                    if (entry.value) onGranted?.invoke(entry.key) else onDenied?.invoke(entry.key)
                }
                if (isAllGranted) {
                    onAllGranted?.invoke()
                }
            }
    }

    private var onGranted: ((permission: String) -> Unit)? = null
    private var onDenied: ((permission: String) -> Unit)? = null
    private var onAllGranted: (() -> Unit)? = null


    fun requestMultiplePermission(
        vararg permissions: String,
        onGranted: (permission: String) -> Unit,
        onDenied: (permission: String) -> Unit,
        onAllGranted: (() -> Unit)? = null
    ) {
        this.onGranted = onGranted
        this.onDenied = onDenied
        this.onAllGranted = onAllGranted
        for (permission in permissions) {
            val result = ActivityCompat.checkSelfPermission(
                this, permission
            )
            if (result == PackageManager.PERMISSION_GRANTED) {
                onGranted.invoke(permission)
            }
        }

        val list = permissions.toList().toTypedArray()
        val deniedList = list.filter {
            ActivityCompat.checkSelfPermission(
                this, it
            ) != PackageManager.PERMISSION_GRANTED
        }
        if (deniedList.isNotEmpty()) {
            multiplePermissionLauncher.launch(deniedList.toTypedArray())
        } else {
            onAllGranted?.invoke()
        }
    }


    fun requestMediaPermission(
        onDenied: (permission: String) -> Unit, onAllGranted: (() -> Unit)? = null
    ) {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                android.Manifest.permission.READ_MEDIA_VIDEO,
                android.Manifest.permission.READ_MEDIA_IMAGES
            )
        } else {
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
        requestMultiplePermission(*permissions, onGranted = {

        }, onDenied = onDenied, onAllGranted = onAllGranted)
    }


    private fun isTranslucentOrFloating(): Boolean {
        var isTranslucentOrFloating = false
        try {
            val styleableRes = Class.forName("com.android.internal.R\$styleable").getField("Window")
                .get(null) as IntArray
            val ta = obtainStyledAttributes(styleableRes)
            val m = ActivityInfo::class.java.getMethod(
                "isTranslucentOrFloating", TypedArray::class.java
            )
            m.isAccessible = true
            isTranslucentOrFloating = m.invoke(null, ta) as Boolean
            m.isAccessible = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return isTranslucentOrFloating
    }

    private fun fixOrientation(): Boolean {
        try {
            val field = android.app.Activity::class.java.getDeclaredField("mActivityInfo")
            field.isAccessible = true
            val o = field[this] as ActivityInfo
            o.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            field.isAccessible = false
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}