package com.cute.store.intent

import com.cute.basic.UserIntent

sealed class CoinStoreIntent : UserIntent {

    object CoinProductData : CoinStoreIntent()

    object GetBalance : CoinStoreIntent()
}
