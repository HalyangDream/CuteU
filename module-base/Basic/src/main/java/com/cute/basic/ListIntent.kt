package com.cute.basic

sealed class ListIntent : UserIntent {

    data class Loading(val param: Any? = null) : ListIntent()

    data class Refresh(val param: Any? = null) : ListIntent()

    data class LoadMore(val param: Any? = null) : ListIntent()

}
