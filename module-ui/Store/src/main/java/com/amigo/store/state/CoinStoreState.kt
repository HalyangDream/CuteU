package com.amigo.store.state

import com.amigo.basic.UserState
import com.amigo.logic.http.response.product.Product

sealed class CoinStoreState : UserState {



    data class CoinStoreProduct(
        val extraList: MutableList<Product>?,
        val list: MutableList<Product>?
    ) : CoinStoreState()

    data class Package3Product(val package3Product: Product?) : CoinStoreState()


    data class Balance(val balance: String) : CoinStoreState()
}
