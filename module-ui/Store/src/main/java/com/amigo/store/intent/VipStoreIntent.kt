package com.amigo.store.intent

import com.amigo.basic.UserIntent

sealed class VipStoreIntent : UserIntent {

    object VipProductData : VipStoreIntent()

    object VipPublicityData : VipStoreIntent()
}
