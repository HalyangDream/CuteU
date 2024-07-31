package com.cute.basic

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

abstract class BaseModelFragment<V : ViewBinding, M : ViewModel> : BaseFragment<V>() {


    val viewModel: M by lazy { ViewModelProvider(this)[extractViewModelClass()] }

    private fun extractViewModelClass(): Class<M> {
        val superClass = javaClass.genericSuperclass
        val type = (superClass as ParameterizedType).actualTypeArguments[1]
        return type as Class<M>
    }

}