package com.cute.mine.route

import android.content.Context
import com.cute.mine.dialog.RatingDialog
import com.cute.uibase.route.RoutePage
import com.cute.uibase.route.provider.IMineService
import com.alibaba.android.arouter.facade.annotation.Route

@Route(path = RoutePage.Provider.MINE_PROVIDER)
class IMineServiceImpl : IMineService {

    override fun showRatingDialog(context: Context) {
        val dialog = RatingDialog()
        dialog.showDialog(context, null)
    }

    override fun init(context: Context?) {

    }
}