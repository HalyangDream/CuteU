package com.amigo.mine.route

import android.content.Context
import com.amigo.mine.dialog.RatingDialog
import com.amigo.uibase.route.RoutePage
import com.amigo.uibase.route.provider.IMineService
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