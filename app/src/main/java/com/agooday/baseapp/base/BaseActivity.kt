package com.agooday.baseapp.base

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import com.agooday.baseapp.MainViewModel
import com.agooday.baseapp.R
import com.agooday.baseapp.util.AppBundle
import com.agooday.baseapp.util.AppUtil
import com.agooday.baseapp.util.Constant
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails
import com.google.android.gms.ads.doubleclick.PublisherAdRequest
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd
import io.reactivex.disposables.CompositeDisposable

abstract class BaseActivity : AppCompatActivity(),BillingProcessor.IBillingHandler{

    override fun onBillingInitialized() {
        if(bp.isPurchased(Constant.PRODUCT_ID_0) || bp.isPurchased(Constant.PRODUCT_ID_1) || bp.isPurchased(Constant.PRODUCT_ID_2)){
            AppUtil.isPremium = true
        }else{
            mPublisherInterstitialAd.loadAd(PublisherAdRequest.Builder().build())
            AppUtil.isPremium = false
        }
    }

    override fun onPurchaseHistoryRestored() {
    }

    override fun onProductPurchased(productId: String, details: TransactionDetails?) {
        AppUtil.isPremium = true
        showToast(getString(R.string.thank_you_very_much))
        mainViewModel.restartEvent.call()
        //mainViewModel.showAdBannerEvent.value = false
    }



    override fun onBillingError(errorCode: Int, error: Throwable?) {
    }


    private fun showToast(content:String){
        Toast.makeText(this,content, Toast.LENGTH_LONG).show()
    }


    lateinit var mainViewModel: MainViewModel
    private lateinit var bp: BillingProcessor
    private lateinit var mPublisherInterstitialAd: PublisherInterstitialAd
    private var timeShowAds = 0L
    public fun showFullAds() {
        if (mPublisherInterstitialAd.isLoaded) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - timeShowAds > 26000) {
                mPublisherInterstitialAd.show()
                timeShowAds = currentTime
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        bp = BillingProcessor.newBillingProcessor(this, Constant.LICENSE_KEY, this); // doesn't bind
        bp.initialize();
    }


    public fun showFragment(appBundle: AppBundle) {
        supportFragmentManager.executePendingTransactions()
        val transaction = supportFragmentManager.beginTransaction()
        val newFragment = supportFragmentManager.findFragmentByTag(appBundle.tag)?:createFragment(appBundle.tag)

        if (!appBundle.tag.contains("ROOT")) {
            transaction.addToBackStack(null)
            transaction.setCustomAnimations(
                R.anim.enter_from_left,
                R.anim.exit_to_right,
                R.anim.enter_from_right,
                R.anim.exit_to_left
            )
            transaction.replace(R.id.container, newFragment, appBundle.tag)
        } else {
            for (fragment in supportFragmentManager.fragments) {
                fragment?.let {
                    if (it.isVisible) {
                        transaction.hide(fragment)
                    }
                }
            }
            if (!newFragment.isAdded) {
                transaction.add(R.id.container, newFragment, appBundle.tag)
            } else {
                transaction.show(newFragment)
            }
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        newFragment.arguments = appBundle.bundle
        transaction.commitAllowingStateLoss()
    }

    abstract fun createFragment(tag: String): Fragment
}