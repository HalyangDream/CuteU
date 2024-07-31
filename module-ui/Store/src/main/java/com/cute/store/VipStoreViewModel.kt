package com.cute.store

import androidx.lifecycle.viewModelScope
import com.cute.basic.BaseMVIModel
import com.cute.logic.http.model.ProductRepository
import com.cute.store.intent.VipStoreIntent
import com.cute.store.state.VipStoreState
import kotlinx.coroutines.launch

class VipStoreViewModel : BaseMVIModel<VipStoreIntent, VipStoreState>() {

    private val productRepository = ProductRepository()

    override fun processIntent(intent: VipStoreIntent) {
        when (intent) {
            is VipStoreIntent.VipProductData -> getVipProduct()
            is VipStoreIntent.VipPublicityData -> getVipPublicityData()
        }
    }


    private fun getVipProduct() {
        viewModelScope.launch {
            val response = productRepository.getVipProduct20200()
            setState(VipStoreState.VipStoreProduct(response.data?.list))
        }
    }

    private fun getVipPublicityData() {
        viewModelScope.launch {
            val response = productRepository.getVipPowerData()
            setState(VipStoreState.VipPublicityData(response.data?.list))
        }
    }

}