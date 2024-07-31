package com.cute.logic.http.model

import com.cute.http.ApiClient
import com.cute.http.ApiRepository
import com.cute.http.ApiResponse
import com.cute.logic.http.HttpCommonParam
import com.cute.logic.http.HttpCommonParam.toRequestBody
import com.cute.logic.http.response.product.PackageShow
import com.cute.logic.http.response.product.Product
import com.cute.logic.http.response.product.ProductResponse
import com.cute.logic.http.response.product.VipPowerInfoDataResponse
import com.cute.logic.http.service.ProductService

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