package com.agooday.baseapp.base

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import com.agooday.baseapp.R
import com.agooday.baseapp.ina.BillingManager
import com.agooday.baseapp.util.AppUtil
import com.agooday.baseapp.util.Constant
import com.agooday.baseapp.util.DialogUtils
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsResponseListener
import com.google.android.gms.common.util.CollectionUtils
import com.google.firebase.analytics.FirebaseAnalytics
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

abstract class BaseActivity : AppCompatActivity(), BillingManager.BillingUpdatesListener {

    var mBillingManager: BillingManager? = null

    lateinit var compositeDisposable: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        compositeDisposable = CompositeDisposable()
        mBillingManager = BillingManager(this, this)
    }

    override fun onResume() {
        super.onResume()
        // Note: We query purchases in onResume() to handle purchases completed while the activity
        // is inactive. For example, this can happen if the activity is destroyed during the
        // purchase flow. This ensures that when the activity is resumed it reflects the user's
        // current purchases.
        if (mBillingManager != null && mBillingManager!!.billingClientResponseCode == BillingClient.BillingResponse.OK) {
            mBillingManager!!.queryPurchases()
        }
    }

    override fun onBillingClientSetupFinished() {
        querySkuDetails()
    }

    override fun onConsumeFinished(token: String, result: Int) {

    }

    override fun onPurchasesUpdated(purchases: List<Purchase>) {
        //LogUtils.logD("BaseBillingActivity", "onPurchasesUpdated: ")
        val oldPurchaseState = AppUtil.isPremium
        val newPurchaseState = !CollectionUtils.isEmpty(purchases)
        //AppUtil.log("newPurchaseState = "+newPurchaseState)
        //AppUtil.log("purchases = "+purchases.size)
        AppUtil.isPremium = newPurchaseState
        if (!oldPurchaseState && newPurchaseState) { //new user purchase item
            Toast.makeText(this, getString(R.string.thank_you_very_much), Toast.LENGTH_SHORT).show()
            FirebaseAnalytics.getInstance(this).logEvent("iap_" + purchases[0].sku, Bundle())
            restartApp()
        }
    }
    fun restorePurchase() {
        querySkuDetails()
    }

    fun restartApp() {
        val restartIntent = Intent(this, this.javaClass)
        val extras = intent.extras
        if (extras != null) {
            restartIntent.putExtras(extras)
        }
        ActivityCompat.finishAffinity(this)
        ActivityCompat.startActivity(
            this,
            restartIntent,
            ActivityOptionsCompat
                .makeCustomAnimation(this, android.R.anim.fade_in, android.R.anim.fade_out)
                .toBundle()
        )

    }

    private fun querySkuDetails() {
        val startTime = System.currentTimeMillis()
        if(AppUtil.getIapLevel() == -1L){
            AppUtil.log("querySkuDetails: wait for getting iapLevel from remoteConfigs")
            return
        }
        if (!isFinishing) {
            val skuList = AppUtil.getSkuList()
            mBillingManager!!.querySkuDetailsAsync(BillingClient.SkuType.SUBS, skuList, SkuDetailsResponseListener { _, skuDetailsList ->
                var costMonthly = 0.0
                var costYearly = 0.0
                if(skuDetailsList != null) {
                    for (skuDetail in skuDetailsList) {
                        if(skuDetail != null) {
                            AppUtil.log(this, "onSkuDetailsResponse  " + skuDetail.description + "   " + skuDetail.price + "   " + skuDetail.title + "   " + skuDetail.subscriptionPeriod)
                            when {
                                skuList[0] == skuDetail.sku -> costMonthly = (skuDetail.priceAmountMicros / 1000000).toDouble()
                                skuList[1] == skuDetail.sku -> AppUtil.setPriceSubscription3Months( skuDetail.price)
                                skuList[2] == skuDetail.sku -> {
                                    costYearly = (skuDetail.priceAmountMicros / 1000000).toDouble()
                                    AppUtil.setPriceSubscriptionYearly(this, skuDetail.price)
                                }
                            }
                        }
                    }
                    if (costMonthly != 0.0 && costYearly != 0.0) {
                        val savePercentageInt = ((12 * costMonthly - costYearly) / (12 * costMonthly) * 100).toInt()
                        AppUtil.setSavePercentage( getString(R.string.iap_save) + " " + savePercentageInt.toString() + "%")
                    }
                    setDataForViews(skuDetailsList)
                }
            })
        }
    }

    /**
     * Overridden by subclasses
     */
    open fun setDataForViews(skuDetailsList: List<SkuDetails>) {}

    /**
     * Overridden by subclasses
     */
    open fun onProductPurchased() {

    }

    open   fun buySubItem(sku: String) {
        mBillingManager!!.initiatePurchaseFlow(sku, BillingClient.SkuType.SUBS)
    }
    open  fun buyInApp(skuItem: String) {
        mBillingManager!!.initiatePurchaseFlow(skuItem, BillingClient.SkuType.INAPP)
    }

    /**
     * show dialog suggest purchase after closing interstitial ads
     * check time to prevent showing dialog continuously
     * should use remote configs to control logic showing this dialog
     */
    open  fun onInterAdClosed() {
        if (!isFinishing
            && System.currentTimeMillis() - AppUtil.getLastTimeSuggestPurchase() > Constant.MIN_TIME_TO_SHOW_SUGGEST_PURCHASE_DIALOG) {
            compositeDisposable.add(Completable.complete().delay(300, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    AppUtil.setLastTimeSuggestPurchase(System.currentTimeMillis())
                    DialogUtils.showCustomDialog(this, R.string.dialog_suggest_purchase_title, R.string.dialog_suggest_purchase_message, R.string.dialog_suggest_purchase_try_free,
                        R.string.dialog_suggest_purchase_later, false, DialogInterface.OnClickListener { _, _ ->
                            buySubItem(AppUtil.getSkuList()[0])
                        }, DialogInterface.OnClickListener { _, _ -> })
                }.subscribe())
        }
    }
}