package com.amigo.basic

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<V : ViewBinding> : Fragment() {

    lateinit var viewBinding: V
    private var first = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPermissionLauncher()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = initViewBinding(layoutInflater, container)
        initView()
        return viewBinding.root
    }


    override fun onResume() {
        super.onResume()
        if (first) {
            first = false
            firstShowUserVisible()
        }
    }


    abstract fun initViewBinding(layout: LayoutInflater, container: ViewGroup?): V

    abstract fun initView()

    abstract fun firstShowUserVisible()


    private lateinit var multiplePermissionLauncher: ActivityResultLauncher<Array<String>>
    private fun initPermissionLauncher() {
        multiplePermissionLauncher =
            this.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
                for (entry in result) {
                    if (entry.value) onGranted?.invoke(entry.key) else onDenied?.invoke(entry.key)
                }
            }
    }

    private var onGranted: ((permission: String) -> Unit)? = null
    private var onDenied: ((permission: String) -> Unit)? = null


    fun requestMultiplePermission(
        vararg permissions: String,
        onGranted: (permission: String) -> Unit,
        onDenied: (permission: String) -> Unit,
    ) {
        this.onGranted = onGranted
        this.onDenied = onDenied
        for (permission in permissions) {

            val result = ActivityCompat.checkSelfPermission(
                requireContext(),
                permission
            )
            if (result == PackageManager.PERMISSION_GRANTED) {
                onGranted.invoke(permission)
            }
        }
        val list = permissions.toList().toTypedArray()
        val deniedList = list.filter {
            ActivityCompat.checkSelfPermission(
                requireContext(),
                it
            ) != PackageManager.PERMISSION_GRANTED
        }
        if (deniedList.isNotEmpty()) {
            multiplePermissionLauncher.launch(deniedList.toTypedArray())
        }
    }

}