package com.agooday.baseapp.ad

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.agooday.baseapp.R
import com.agooday.baseapp.base.BaseActivity
import com.agooday.baseapp.util.AppUtil
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.formats.MediaView
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView

object NativeAdUtils {
    fun inflateNativeAd(activity: BaseActivity, adIds: Array<String>, container: ViewGroup, layoutResourceId: Int) {
        inflateNativeAd(activity, adIds, container, layoutResourceId, 0)
    }

    private fun inflateNativeAd(activity: BaseActivity?, adIds: Array<String>, container: ViewGroup?, layoutResourceId: Int, count: Int) {
        if (activity == null || container == null
                || AppUtil.isPremium) {
            return
        }
        if (count >= adIds.size) {
            return
        }
        val builder = AdLoader.Builder(activity, adIds[count])
        builder.forUnifiedNativeAd { unifiedNativeAd ->
            val adView = activity.layoutInflater
                    .inflate(layoutResourceId, null) as UnifiedNativeAdView
            populateUnifiedNativeAdView(activity, unifiedNativeAd, adView)
            container.visibility = View.VISIBLE
            container.removeAllViews()
            container.addView(adView)
        }

        loadDataForAd(builder, object : AdListener() {
            override fun onAdFailedToLoad(i: Int) {
                AppUtil.log(this, "onAdFailedToLoad: "+ i)
                inflateNativeAd(activity, adIds, container, layoutResourceId, count + 1)
            }
        })
    }

    private fun loadDataForAd(builder: AdLoader.Builder, adListener: AdListener) {
        val videoOptions = VideoOptions.Builder()
                .setStartMuted(true)
                .build()

        val adOptions = NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build()

        builder.withNativeAdOptions(adOptions)

        val adLoader = builder.withAdListener(adListener).build()

        adLoader.loadAd(AdUtils.getDefaultAdRequest())
    }

    private fun populateUnifiedNativeAdView(activity: BaseActivity?, nativeAd: UnifiedNativeAd, adView: UnifiedNativeAdView) {
        // Set the media view. Media content will be automatically populated in the media view once
        // adView.setNativeAd() is called.
        val mediaView: MediaView = adView.findViewById(R.id.ad_media)
        adView.mediaView = mediaView

        // Set other ad assets.
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
//        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
//        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

        // The headline is guaranteed to be in every UnifiedNativeAd.
        (adView.headlineView as TextView).text = nativeAd.headline

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.body == null) {
            adView.bodyView.visibility = View.INVISIBLE
        } else {
            adView.bodyView.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }

        if (nativeAd.callToAction == null) {
            adView.callToActionView.visibility = View.INVISIBLE
        } else {
            adView.callToActionView.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }

        if (nativeAd.icon == null) {
            adView.iconView.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                    nativeAd.icon.drawable)
            adView.iconView.visibility = View.VISIBLE
        }

        try {
            if (nativeAd.price == null) {
                adView.priceView.visibility = View.INVISIBLE
            } else {
                adView.priceView.visibility = View.VISIBLE
                (adView.priceView as TextView).text = nativeAd.price
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            if (nativeAd.store == null) {
                adView.storeView.visibility = View.INVISIBLE
            } else {
                adView.storeView.visibility = View.VISIBLE
                (adView.storeView as TextView).text = nativeAd.store
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


        if (nativeAd.starRating == null) {
            adView.starRatingView.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
            adView.starRatingView.visibility = View.VISIBLE
        }

        if (nativeAd.advertiser == null) {
            adView.advertiserView.visibility = View.INVISIBLE
        } else {
            (adView.advertiserView as TextView).text = nativeAd.advertiser
            adView.advertiserView.visibility = View.VISIBLE
        }

        val btnRemoveAds: View = adView.findViewById(R.id.btn_remove_ad)
        btnRemoveAds.setOnClickListener {
            if (activity != null && !activity.isFinishing) {
                activity.buySubItem(AppUtil.getSkuList()[0])
            }
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad. The SDK will populate the adView's MediaView
        // with the media content from this native ad.
        adView.setNativeAd(nativeAd)
    }
}
