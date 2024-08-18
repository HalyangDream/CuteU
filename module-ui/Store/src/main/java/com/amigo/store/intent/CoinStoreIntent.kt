package com.amigo.store.intent

import com.amigo.basic.UserIntent

sealed class CoinStoreIntent : UserIntent {

    object CoinProductData : CoinStoreIntent()

    object GetBalance : CoinStoreIntent()
}
