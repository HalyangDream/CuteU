package com.cute.store.intent

import com.cute.basic.UserIntent

sealed class VipStoreIntent : UserIntent {

    object VipProductData : VipStoreIntent()

    object VipPublicityData : VipStoreIntent()
}
