package com.amigo.logic.http.response.list

data class ReportReason(val id: String, val reason: String)

data class ReportReasonResponse(val data:MutableList<ReportReason>?)