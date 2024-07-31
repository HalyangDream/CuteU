package com.cute.store

import androidx.lifecycle.viewModelScope
import com.cute.basic.BaseMVIModel
import com.cute.logic.http.model.ProductRepository
import com.cute.logic.http.model.ProfileRepository
import com.cute.store.intent.CoinStoreIntent
import com.cute.store.state.CoinStoreState
import kotlinx.coroutines.launch

class CoinStoreViewModel : BaseMVIModel<CoinStoreIntent, CoinStoreState>() {

    private val productRepository = ProductRepository()
    private val profileRepository = ProfileRepository()

    override fun processIntent(intent: CoinStoreIntent) {
        when (intent) {
            is CoinStoreIntent.GetBalance -> getBalance()
            is CoinStoreIntent.CoinProductData -> getCoinProduct()
        }
    }

    private fun getBalance() {
        viewModelScope.launch {
            val response = profileRepository.getBalance()
            setState(CoinStoreState.Balance(response.data?.balance ?: "0"))
        }
    }

    private fun getCoinProduct() {
        viewModelScope.launch {
            val response = productRepository.getCoinProduct20100()
            setState(
                CoinStoreState.CoinStoreProduct(
                    response.data?.extraProduct,
                    response.data?.list
                )
            )
        }
    }
}