package com.amigo.logic.http.model

import com.amigo.http.ApiClient
import com.amigo.http.ApiRepository
import com.amigo.http.ApiResponse
import com.amigo.logic.http.HttpCommonParam
import com.amigo.logic.http.HttpCommonParam.toRequestBody
import com.amigo.logic.http.response.product.PackageShow
import com.amigo.logic.http.response.product.Product
import com.amigo.logic.http.response.product.ProductResponse
import com.amigo.logic.http.response.product.VipPowerInfoDataResponse
import com.amigo.logic.http.service.ProductService

class ProductRepository : ApiRepository() {


    private val service by lazy { ApiClient.getService(ProductService::class.java) }

    suspend fun getVipPowerData(): ApiResponse<VipPowerInfoDataResponse> {
        return launchRequest {
            service.getVipPower(
                HttpCommonParam.getCommonParam().toRequestBody()
            )
        }
    }

    suspend fun getPackageShow(): ApiResponse<PackageShow> {
        return launchRequest {
            service.packageShow(
                HttpCommonParam.getCommonParam().toRequestBody()
            )
        }
    }


    suspend fun getCoinProduct20100(): ApiResponse<ProductResponse> {
        return launchRequest {
            service.getCoinProduct20100(
                HttpCommonParam.getCommonParam().toRequestBody()
            )
        }
    }

    suspend fun getCoinProduct20101(): ApiResponse<ProductResponse> {
        return launchRequest {
            service.getCoinProduct20101(
                HttpCommonParam.getCommonParam().toRequestBody()
            )
        }
    }

    suspend fun getCoinProduct20102(): ApiResponse<ProductResponse> {
        return launchRequest {
            service.getCoinProduct20102(
                HttpCommonParam.getCommonParam().toRequestBody()
            )
        }
    }

    suspend fun getCoinPackage20300(): ApiResponse<Product> {
        return launchRequest {
            service.getCoinPackage20300(
                HttpCommonParam.getCommonParam().toRequestBody()
            )
        }
    }

    suspend fun getVipProduct20200(): ApiResponse<ProductResponse> {
        return launchRequest {
            service.getVipProduct20200(
                HttpCommonParam.getCommonParam().toRequestBody()
            )
        }
    }

    suspend fun getVipProduct20201(): ApiResponse<Product> {
        return launchRequest {
            service.getVip20201(
                HttpCommonParam.getCommonParam().toRequestBody()
            )
        }
    }


}