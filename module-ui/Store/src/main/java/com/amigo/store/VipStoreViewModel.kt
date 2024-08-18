package com.amigo.store

import androidx.lifecycle.viewModelScope
import com.amigo.basic.BaseMVIModel
import com.amigo.logic.http.model.ProductRepository
import com.amigo.store.intent.VipStoreIntent
import com.amigo.store.state.VipStoreState
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