package com.cute.basic

import androidx.lifecycle.ViewModel
import androidx.viewbinding.ViewBinding

abstract class BaseListActivity<V : ViewBinding, M : ViewModel> : BaseModelActivity<V, M>() {

    abstract fun emptyLayout(isEmpty: Boolean)

    abstract fun netErrorLayout(isNetError: Boolean)

    abstract fun dataHasBottomLayout(isBottom: Boolean)

    abstract fun loadingLayout(isLoading: Boolean)

    open fun <T> handleListIntent(state: ListState<T>) {
        when (state) {
            is ListState.EmptyData -> emptyLayout(state.isEmpty)
            is ListState.NetError -> netErrorLayout(state.isNetError)
            is ListState.DataHasBottom -> dataHasBottomLayout(state.isBottom)
            is ListState.Loading -> loadingLayout(state.isLoading)
            else -> {}
        }
    }
}