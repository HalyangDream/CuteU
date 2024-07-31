package com.cute.logic.http.model

import com.cute.http.ApiClient
import com.cute.http.ApiRepository
import com.cute.http.ApiResponse
import com.cute.logic.http.HttpCommonParam
import com.cute.logic.http.HttpCommonParam.toRequestBody
import com.cute.logic.http.service.ReportService
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class ReportRepository : ApiRepository() {

    private val service by lazy { ApiClient.getService(ReportService::class.java) }

    suspend fun reportStoreClose(popCode: String): ApiResponse<Unit> {
        val params = HttpCommonParam.getCommonParam().apply {
            put("pop_code", popCode)
        }
        return launchRequest { service.reportStoreClose(params.toRequestBody()) }
    }

    suspend fun reportEvent(eventName: String, eventMap: Map<String, Any>?): ApiResponse<Unit> {
        val params = HttpCommonParam.getCommonParam().apply {
            put("event_name", eventName)
            if (!eventMap.isNullOrEmpty()) {
                val jsonObj = parseEventMap(eventMap)
                put("event", jsonObj)
            } else {
                put("event", null)
            }
        }
        return launchRequest { service.reportEvent(params.toRequestBody()) }
    }

    private fun parseEventMap(eventMap: Map<String, Any>): JSONObject {
        val jsonObj = JSONObject()
        try {
            for (entry in eventMap.entries) {
                if (entry.value is Collection<*>) {
                    val jsonArray = JSONArray()
                    val collection = entry.value as Collection<*>
                    for (any in collection) {
                        jsonArray.put(any)
                    }
                    jsonObj.put(entry.key, jsonArray)
                } else {
                    jsonObj.put(entry.key, entry.value)
                }
            }

        } catch (ex: JSONException) {
            ex.printStackTrace()
        }
        return jsonObj
    }
}