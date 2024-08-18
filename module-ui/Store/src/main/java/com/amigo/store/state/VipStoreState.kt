package com.amigo.store.state

import com.amigo.basic.UserState
import com.amigo.logic.http.response.product.Product
import com.amigo.logic.http.response.product.VipPowerInfoData

sealed class VipStoreState : UserState {


    data class VipStoreProduct(val list: MutableList<Product>?) : VipStoreState()

    data class VipPublicityData(val list: MutableList<VipPowerInfoData>?) : VipStoreState()
}
