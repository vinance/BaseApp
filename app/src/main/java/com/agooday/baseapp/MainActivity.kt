package com.agooday.baseapp

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import com.agooday.baseapp.base.BaseActivity
import com.agooday.baseapp.feature.Test2Fragment
import com.agooday.baseapp.feature.TestFragment
import com.agooday.baseapp.ina.GoPremiumActivity
import com.agooday.baseapp.util.AppBundle
import com.agooday.baseapp.util.AppUtil
import com.agooday.baseapp.util.Constant
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity()  {

    private lateinit var mPublisherInterstitialAd: PublisherInterstitialAd
    private var timeShowAds = 0L
    private fun showFullAds() {
        if (mPublisherInterstitialAd.isLoaded) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - timeShowAds > 26000) {
                mPublisherInterstitialAd.show()
                timeShowAds = currentTime
            }
        }
    }

    lateinit var mainViewModel: MainViewModel

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                showFragment(AppBundle(Constant.TEST_TAG,null))
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                showFragment(AppBundle(Constant.TEST2_TAG,null))
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        configBar()
        configObserver()



        nav_view.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        nav_view.selectedItemId  = R.id.navigation_home
        MobileAds.initialize(this, "ca-app-pub-4064594014466732~1990332119")
        mPublisherInterstitialAd = PublisherInterstitialAd(this)


        if(BuildConfig.DEBUG){
            mPublisherInterstitialAd.adUnitId = "ca-app-pub-3940256099942544/1033173712"
        }else{
            mPublisherInterstitialAd.adUnitId = "ca-app-pub-4064594014466732/2289715012"
        }
        //mPublisherInterstitialAd.adUnitId = "ca-app-pub-3940256099942544/1033173712" //test
        mPublisherInterstitialAd.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                /*loading?.let {
                    if(it.visibility == View.VISIBLE) showFullAds()
                }*/
                showFullAds()
            }

            override fun onAdClosed() {
                super.onAdClosed()
                if(System.currentTimeMillis() - AppUtil.timeShowAskUpgrade>86400000){
                    AppUtil.timeShowAskUpgrade = System.currentTimeMillis()
                    callUpgrade(true)
                }
            }
        }


        val countOpen = AppUtil.reviewCount
        if(countOpen>Constant.REVIEW_MAX_OPEN) AppUtil.reviewCount = 0 else AppUtil.reviewCount +countOpen
        if (AppUtil.isNeedAskReview && countOpen == Constant.REVIEW_MAX_OPEN - 1) {
            askReview()
        }
    }

    private fun configObserver() {
        compositeDisposable = CompositeDisposable()
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        mainViewModel.apply {

        }
    }

    private fun configBar() {
        //supportActionBar?.hide()
        supportActionBar?.elevation = 0F
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setCustomView(R.layout.action_bar)

    }

    private fun showFragment(appBundle: AppBundle) {
        supportFragmentManager.executePendingTransactions()
        val transaction = supportFragmentManager.beginTransaction()
        val newFragment = supportFragmentManager.findFragmentByTag(appBundle.tag)?:when (appBundle.tag) {
            Constant.TEST_TAG-> TestFragment()
            Constant.TEST2_TAG-> Test2Fragment()
            else->Fragment()
        }

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

    private fun askReview() {
        val mView = LayoutInflater.from(this).inflate(R.layout.dialog_rating, null)
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setView(mView)
        val alert = builder.create()

        val tvTitle = mView.findViewById<View>(R.id.dialog_rating_title) as TextView?
        val tvPositive = mView.findViewById<View>(R.id.dialog_rating_button_positive) as TextView?
        val tvFeedback = mView.findViewById<View>(R.id.dialog_rating_feedback_title) as TextView?
        val tvSubmit = mView.findViewById<View>(R.id.dialog_rating_button_feedback_submit) as TextView?
        val tvCancel = mView.findViewById<View>(R.id.dialog_rating_button_feedback_cancel) as TextView?
        val ratingBar = mView.findViewById<View>(R.id.dialog_rating_rating_bar) as RatingBar?
        val ivIcon = mView.findViewById<View>(R.id.dialog_rating_icon) as TextView?
        val etFeedback = mView.findViewById<View>(R.id.dialog_rating_feedback) as EditText?
        val ratingButtons = mView.findViewById<View>(R.id.dialog_rating_buttons) as LinearLayout?
        val feedbackButtons = mView.findViewById<View>(R.id.dialog_rating_feedback_buttons) as LinearLayout?


        ratingBar!!.onRatingBarChangeListener = RatingBar.OnRatingBarChangeListener { _, rate, _ ->
            if (rate >= 5) {
                val marketUri = Uri.parse("market://details?id=$packageName")
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, marketUri))
                } catch (ex: android.content.ActivityNotFoundException) {

                }
                AppUtil.isNeedAskReview = false
                alert.dismiss()
            } else {
                tvFeedback!!.visibility = View.VISIBLE
                etFeedback!!.visibility = View.VISIBLE
                feedbackButtons!!.visibility = View.VISIBLE
                ratingButtons!!.visibility = View.GONE
                ivIcon!!.visibility = View.GONE
                tvTitle!!.visibility = View.GONE
                ratingBar.visibility = View.GONE
            }
        }


        tvPositive?.setOnClickListener { alert.dismiss() }
        tvSubmit?.setOnClickListener { it ->
            val feedback = etFeedback!!.text.toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(feedback)) {

                val shake = AnimationUtils.loadAnimation(this, R.anim.rate_shake)
                etFeedback.startAnimation(shake)
            } else {
                showToast(getString(R.string.thank_you_very_much))
                alert.dismiss()
            }
        }
        tvCancel?.setOnClickListener {
            alert.dismiss()
        }
        alert.show()
    }
    private fun showToast(content:String){
        Toast.makeText(this,content,Toast.LENGTH_LONG).show()
    }

    private fun callUpgrade(buyInApp:Boolean) {
        if(buyInApp) {
            var position = 0
            val mView = LayoutInflater.from(this).inflate(R.layout.dialog_donate, null)
            val builder = AlertDialog.Builder(this)
            builder.setView(mView)
            builder.setTitle(resources.getString(R.string.upgrade))

            val buy_premium_check = mView.findViewById(R.id.buy_premium_check) as RadioButton
            val buy_a_coffee_check = mView.findViewById(R.id.buy_a_coffee_check) as RadioButton
            val support_team_check = mView.findViewById(R.id.support_team_check) as RadioButton

            val buy_premium = mView.findViewById(R.id.buy_premium) as LinearLayout
            val buy_a_coffee = mView.findViewById(R.id.buy_a_coffee) as LinearLayout
            val support_team = mView.findViewById(R.id.support_team) as LinearLayout


            buy_premium_check.isChecked = true

            builder.setPositiveButton(getString(android.R.string.ok)) { dialog, _ ->
                dialog.cancel()
                val productId = when (position) {
                    0 -> Constant.PRODUCT_ID_0
                    1 -> Constant.PRODUCT_ID_1
                    else -> Constant.PRODUCT_ID_2

                }
                buyInApp(productId)
                //bp.purchase(this,productId)
            }
            buy_premium.setOnClickListener {
                buy_premium_check.isChecked = true
                buy_a_coffee_check.isChecked = false
                support_team_check.isChecked = false
                position = 0

            }
            buy_a_coffee.setOnClickListener {
                buy_premium_check.isChecked = false
                buy_a_coffee_check.isChecked = true
                support_team_check.isChecked = false
                position = 1
            }
            support_team.setOnClickListener {
                buy_premium_check.isChecked = false
                buy_a_coffee_check.isChecked = false
                support_team_check.isChecked = true
                position = 2
            }
            val alert = builder.create()
            alert.window?.attributes?.windowAnimations = R.style.DialogTheme
            alert.show()
        }else{
            startActivity(Intent(this, GoPremiumActivity::class.java))
        }
    }

}
