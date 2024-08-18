package com.amigo.basic

sealed class ListState<out T> : UserState {


    data class EmptyData(val isEmpty: Boolean) : ListState<Nothing>()

    data class NetError(val isNetError: Boolean) : ListState<Nothing>()

    data class Loading(val isLoading: Boolean) : ListState<Nothing>()

    data class DataHasBottom(val isBottom: Boolean) : ListState<Nothing>()

    data class LoadingSuccess<T>(val data: MutableList<T>?) : ListState<T>()

    data class RefreshSuccess<T>(val data: MutableList<T>?) : ListState<T>()

    data class LoadMoreSuccess<T>(val data: MutableList<T>?) : ListState<T>()
}
