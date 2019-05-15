package com.agooday.baseapp.ad

import com.agooday.baseapp.BuildConfig

/**
 * Created by Hungnd on 3/29/2017.
 */

interface AdsId {
    companion object {
        val interAds = arrayOf("ca-app-pub-3940256099942544/1033173712")
        val bannerAds = arrayOf("ca-app-pub-3940256099942544/6300978111")
        val nativeAds = if (!BuildConfig.DEBUG) arrayOf(
                "ca-app-pub-4064594014466732/6961044036")
        else arrayOf("ca-app-pub-3940256099942544/2247696110")
    }
}
