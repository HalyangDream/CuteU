package com.amigo.logic.http.service

import com.amigo.http.ApiResponse
import com.amigo.logic.http.response.product.Product
import com.amigo.logic.http.response.product.ProductResponse
import com.amigo.logic.http.response.product.PackageShow
import com.amigo.logic.http.response.product.PopShow
import com.amigo.logic.http.response.product.VipPowerInfoDataResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ProductService {


    @POST("/v1/product/vip_power")
    suspend fun getVipPower(@Body body: RequestBody): ApiResponse<VipPowerInfoDataResponse>

    @POST("/v1/product/package_show")
    suspend fun packageShow(@Body body: RequestBody): ApiResponse<PackageShow>

    @POST("/v1/product/coin_store")
    suspend fun getCoinProduct20100(@Body body: RequestBody): ApiResponse<ProductResponse>

    @POST("/v1/product/coin_20101")
    suspend fun getCoinProduct20101(@Body body: RequestBody): ApiResponse<ProductResponse>

    @POST("/v1/product/coin_20102")
    suspend fun getCoinProduct20102(@Body body: RequestBody): ApiResponse<ProductResponse>

    @POST("/v1/product/coin_20300")
    suspend fun getCoinPackage20300(@Body body: RequestBody): ApiResponse<Product>

    @POST("/v1/product/vip_store")
    suspend fun getVipProduct20200(@Body body: RequestBody): ApiResponse<ProductResponse>

    @POST("/v1/product/vip_20201")
    suspend fun getVip20201(@Body body: RequestBody): ApiResponse<Product>

    @POST("/v1/product/coin_20301")
    suspend fun getCoinPackage20301(@Body body: RequestBody): ApiResponse<Product>

    @POST("/v1/product/show_pop")
    suspend fun showPop(@Body body: RequestBody): ApiResponse<PopShow>


}