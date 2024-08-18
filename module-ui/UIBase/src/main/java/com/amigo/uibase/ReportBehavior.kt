package com.amigo.uibase

import com.amigo.logic.http.model.ReportRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

object ReportBehavior {


    private val repository = ReportRepository()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun reportCloseCoinLessWindow(popCode: String) {
        scope.launch { repository.reportStoreClose(popCode) }
    }

    fun reportEvent(eventName: String, eventMap: Map<String, Any>? = null) {
        scope.launch { repository.reportEvent(eventName, eventMap) }
    }

}