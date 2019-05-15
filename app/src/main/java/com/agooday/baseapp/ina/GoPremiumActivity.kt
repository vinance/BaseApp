package com.agooday.baseapp.ina

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.agooday.baseapp.R
import com.agooday.baseapp.base.BaseActivity
import com.agooday.baseapp.util.AppUtil
import com.android.billingclient.api.SkuDetails
import com.dingmouren.layoutmanagergroup.banner.BannerLayoutManager
import kotlinx.android.synthetic.main.activity_go_premium.*
import java.util.*

class GoPremiumActivity : BaseActivity() {

    private var mLastSelectPosition = 0

    private val mImgList = ArrayList<ImageView>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_go_premium)
        initRecyclerView()
        initListener()
    }

    override fun setDataForViews(skuDetailsList: List<SkuDetails>) {
        val priceSubscription3Months = AppUtil.getPriceSubscription3Months()
        if (!TextUtils.isEmpty(priceSubscription3Months)) {
            tv_price_subscription_months.text = priceSubscription3Months
            tv_price_subscription_months.visibility = View.VISIBLE
        }

        val priceSubscriptionYearly = AppUtil.getPriceSubscriptionYearly()
        if (!TextUtils.isEmpty(priceSubscriptionYearly)) {
            tv_price_subscription_yearly.visibility = View.VISIBLE
            tv_price_subscription_yearly.text = priceSubscriptionYearly
        }

        val savePercentage = AppUtil.getSavePercentage()
        if (!TextUtils.isEmpty(savePercentage)) {
            tv_save_sub_second.visibility = View.VISIBLE
            tv_save_sub_second.text = savePercentage
        }

        progress_loading_price.visibility = View.GONE
    }

    private fun initRecyclerView() {
        progress_loading_price.visibility = View.VISIBLE
        mImgList.add(img_1)
        mImgList.add(img_2)
        mImgList.add(img_3)

        val myAdapter = MyAdapter()
        val bannerLayoutManager = BannerLayoutManager(this, recycler1, 3, OrientationHelper.HORIZONTAL)
        recycler1?.layoutManager = bannerLayoutManager
        recycler1.adapter = myAdapter
        bannerLayoutManager.setOnSelectedViewListener { _, position -> changeUI(position) }
        bannerLayoutManager.setTimeDelayed(3000L)
        changeUI(0)
    }

    private fun changeUI(position: Int) {
        if (position != mLastSelectPosition) {
            mImgList[position].setColorFilter(ContextCompat.getColor(this, android.R.color.holo_red_light))
            mImgList[mLastSelectPosition].setColorFilter(ContextCompat.getColor(this, R.color.iap_indicator_inactive))
            mLastSelectPosition = position
        }
        if (supportActionBar == null) {
            return
        }
        when (position) {
            0 -> supportActionBar!!.title = getString(R.string.no_ads)
            1 -> supportActionBar!!.title = getString(R.string.app_limits)
            2 -> supportActionBar!!.title = getString(R.string.pattern_lock)
        }
    }

    internal inner class MyAdapter : RecyclerView.Adapter<MyAdapter.ViewHolder>() {

        override fun getItemCount(): Int {
            return Integer.MAX_VALUE
        }

        var imgIds = intArrayOf(R.drawable.iap_no_ads, R.drawable.iap_app_limit, R.drawable.iap_pattern)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(applicationContext).inflate(R.layout.item_pro_function, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.onBind(position)
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private var imageView: ImageView = itemView.findViewById(R.id.image_view)
            fun onBind(position: Int) {
                imageView.setImageResource(imgIds[position % 3])
            }
        }
    }

    private fun initListener() {
        btn_buy_subscription_free_trial.setOnClickListener { buySubItem(AppUtil.getSkuList()[0]) }
        btn_buy_subscription_months.setOnClickListener { buySubItem(AppUtil.getSkuList()[1]) }
        btn_buy_subscription_year.setOnClickListener { buySubItem(AppUtil.getSkuList()[2]) }
        btn_restore_purchase.setOnClickListener {
            AppUtil.rotateView(iv_auto_restore_purchase)
            restorePurchase()
        }
    }
}
