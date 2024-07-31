package com.cute.store.state

import com.cute.basic.UserState
import com.cute.logic.http.response.product.Product
import com.cute.logic.http.response.product.VipPowerInfoData

sealed class VipStoreState : UserState {


    data class VipStoreProduct(val list: MutableList<Product>?) : VipStoreState()

    data class VipPublicityData(val list: MutableList<VipPowerInfoData>?) : VipStoreState()
}
