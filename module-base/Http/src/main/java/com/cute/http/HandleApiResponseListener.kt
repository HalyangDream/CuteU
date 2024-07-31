package com.cute.http

import android.os.Bundle

interface HandleApiResponseListener {

    fun onHandle(response: ApiResponse<*>, dialogBundle: Bundle?)
}