package com.agooday.baseapp.data

import androidx.annotation.Nullable
import com.agooday.baseapp.BuildConfig
import com.agooday.baseapp.util.AppUtil
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

class RemoteConfig private constructor() {
    private val firebaseRemoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    init {
        val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build()
        firebaseRemoteConfig.setConfigSettings(configSettings)
    }

    fun fetch() {
        var cacheExpiration: Long = 3600 // 1 hour in seconds.
        // If your app is using developer mode, cacheExpiration is set to 0, so each fetch will
        // retrieve values from the service.
        if (firebaseRemoteConfig.info.configSettings.isDeveloperModeEnabled) {
            cacheExpiration = 0
        }
        firebaseRemoteConfig.fetch(cacheExpiration).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                firebaseRemoteConfig.activateFetched()
                if(AppUtil.getIapLevel() == -1L){//iapLevel should be set 1 time only
                    AppUtil.setIapLevel(firebaseRemoteConfig.getLong("iap_level"))
                }
            }
        }
    }

    companion object {
        @Nullable
        private var remoteConfig: RemoteConfig? = null
        val instance: RemoteConfig
            get() {
                if (remoteConfig == null) {
                    remoteConfig = RemoteConfig()
                }
                return remoteConfig as RemoteConfig
            }
    }

}
