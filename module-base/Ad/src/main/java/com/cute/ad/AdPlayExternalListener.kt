package com.cute.ad

interface AdPlayExternalListener {

    fun onAdLoaded()
    fun onAdFailed()
    fun onAdPlayStart()
    fun onAdPlayEnd()
    fun onAdPlayFailed()
    fun onAdClosed()
    fun onAdPlayClicked()
    fun onAdReward()
}