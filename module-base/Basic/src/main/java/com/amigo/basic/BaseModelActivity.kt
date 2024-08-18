package com.amigo.basic

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType
import kotlin.reflect.javaType

abstract class BaseModelActivity<V : ViewBinding, M : ViewModel> : BaseActivity<V>() {

    val viewModel: M by lazy { ViewModelProvider(this)[extractViewModelClass()] }

    private fun extractViewModelClass(): Class<M> {
        val superType = javaClass.genericSuperclass
        val type = (superType as ParameterizedType).actualTypeArguments[1]
        return type as Class<M>
    }

}