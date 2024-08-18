package com.amigo.uibase.route.provider

import android.content.Context
import com.alibaba.android.arouter.facade.template.IProvider

interface IMineService : IProvider {

    fun showRatingDialog(context: Context)
}