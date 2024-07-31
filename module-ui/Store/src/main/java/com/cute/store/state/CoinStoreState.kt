package com.cute.store.state

import com.cute.basic.UserState
import com.cute.logic.http.response.product.Product

sealed class CoinStoreState : UserState {


    data class CoinStoreProduct(val extraList: MutableList<Product>?,val list: MutableList<Product>?) : CoinStoreState()

    data class Balance(val balance: String) : CoinStoreState()
}
