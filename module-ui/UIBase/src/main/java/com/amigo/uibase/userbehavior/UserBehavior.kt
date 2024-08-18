package com.amigo.uibase.userbehavior

object UserBehavior {

    var root = "" //触发的根页面
        private set

    var chargeSource = "" //触发的充值来源
        private set

    fun setRootPage(rootName: String) {
        this.root = rootName
    }

    fun setChargeSource(sourceName: String) {
        this.chargeSource = sourceName
    }

}