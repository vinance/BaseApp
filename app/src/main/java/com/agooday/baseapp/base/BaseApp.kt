package com.agooday.baseapp.base

import android.app.Application
import com.agooday.preference.AGDPreferenceManager

class BaseApp : Application(){
    override fun onCreate() {
        super.onCreate()
        AGDPreferenceManager.getInstance().initialize(this)
    }
}