package com.agooday.baseapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.agooday.baseapp.util.AppBundle
import com.agooday.baseapp.util.SingleLiveEvent

class MainViewModel(private val context:Application) : AndroidViewModel(context){
    val showBackButtonEvent = SingleLiveEvent<Boolean>()
    val backEvent = SingleLiveEvent<Void>()
    val showNavigationBottomEvent = SingleLiveEvent<Boolean>()
    val showFragmentEvent = SingleLiveEvent<AppBundle>()
    val showTitleEvent = SingleLiveEvent<String>()
    val askPermisisonEvent= SingleLiveEvent<Void>()
    val showToastEvent= SingleLiveEvent<String>()
    val donateEvent= SingleLiveEvent<Void>()
    val updateMenuEvent= SingleLiveEvent<String>()
    val reviewEvent= SingleLiveEvent<Void>()
    val upgradeEvent= SingleLiveEvent<String>()
    val finishAppEvent= SingleLiveEvent<Void>()
    val showFullScreenEvent= SingleLiveEvent<Boolean>()
    val restartEvent = SingleLiveEvent<Void>()
    val showInAdsEvent = SingleLiveEvent<Void>()
}