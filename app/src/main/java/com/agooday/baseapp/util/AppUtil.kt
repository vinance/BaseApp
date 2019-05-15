package com.agooday.baseapp.util

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.Log
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import com.agooday.baseapp.BuildConfig
import com.agooday.baseapp.MainActivity
import com.agooday.baseapp.R
import com.agooday.baseapp.base.BaseApp
import com.agooday.preference.AGDPreferenceManager
import com.agooday.preference.model.PrefBoolean
import com.agooday.preference.model.PrefInt
import com.agooday.preference.model.PrefLong


object AppUtil {


    fun log(ob:Any,content:String){
        if(BuildConfig.DEBUG){
            Log.d("tien.hien",ob.javaClass.simpleName+": "+content)
        }
    }


    fun log(content:String){
        if(BuildConfig.DEBUG){
            Log.d("tien.hien",content)
        }
    }


    var isPremium by PrefBoolean( "PREMIUM_VERSION", true)
    var isNeedAskReview by PrefBoolean( "NEED_ASK_REVIEW", false)
    var reviewCount by PrefInt( "REVIEW_COUNT", 0)


    var timeShowAskUpgrade by PrefLong( "TIME_SHOW_ASK_UP", 0L)


    fun openable(packageManager: PackageManager, packageName: String): Boolean {
        val value = packageManager.getLaunchIntentForPackage(packageName) != null
        //AppUtil.log("openable - "+packageName+" : "+value)
        return value
    }
    fun isHomeScreen(context:Context,componentName: ComponentName?): Boolean {
        try {
            if(componentName == null ) return false
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_HOME)
            val resolveInfo = context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
            return resolveInfo.activityInfo.packageName == componentName.packageName
        } catch (e: Exception) {
            return false
        }
    }
    fun sendBroadcast(context: Context?, action: String) {
        context?.sendBroadcast(Intent(action))
    }


    /*fun isServiceRunning(context: Context): Boolean {
        val am = context
            .getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager

        val runningServices = am
            .getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK)
        for (service in runningServices) {
            if (service.id.contains(context.packageName+"/.service."+ PermissionService::class.java.simpleName)) {
                return true
            }
        }

        return false
    }
*/

    fun sendEmail(context: Context) {

        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("agooday.cs@gmail.com"))
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name))
        //intent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.email_content));
        try {
            context.startActivity(Intent.createChooser(intent, "Send mail..."))
        } catch (ex: Exception) {
        }

    }

    fun viewDevPage(context: Context) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id=8809858758456933468")))
        } catch (e: Exception) {
        }
    }

    fun viewPrivacy(context: Context) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://agoodaymobile.wixsite.com/policy")))
        } catch (e: Exception) {
        }

    }

    fun viewMarket(context: Context?) {
        val uri = Uri.parse("market://details?id=" + context?.packageName)
        val myAppLinkToMarket = Intent(Intent.ACTION_VIEW, uri)
        try {
            myAppLinkToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context?.startActivity(myAppLinkToMarket)
        } catch (e: ActivityNotFoundException) {
        }

    }

    fun share(context: Context?) {
        if (context != null) {
            val appPackageName = BuildConfig.APPLICATION_ID
            val appName = context.getString(R.string.app_name)
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            val shareBodyText = "https://play.google.com/store/apps/details?id=$appPackageName"
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, appName)
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText)
            context.startActivity(Intent.createChooser(shareIntent, "Share via..."))
        }
    }

    fun getAppLabel(pkm: PackageManager, data: String): String {
        return try {
            val intent =  Intent("android.intent.action.MAIN")
            intent.setPackage(data)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)
            pkm.resolveActivity(intent, 0).loadLabel(pkm).toString()
        } catch (e: Exception) {
            data
        }
    }

    fun getAppIcon(context: Context, packageName: String): Drawable {
        val manager = context.packageManager
        try {
            return manager.getApplicationIcon(packageName)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return context.resources.getDrawable(R.mipmap.ic_launcher,null)
    }

    fun isSystemPackage(activityInfo: ActivityInfo): Boolean {
        return activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0 || activityInfo.applicationInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP != 0
    }
    fun isSystemPackage(context: Context,pkg:String): Boolean {
        var value = false
        try {
            val appInfo = context.packageManager.getApplicationInfo(pkg, 0)
            if(appInfo != null){
                value =  appInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0 || appInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP != 0
            }else{
                log("getApplicationInfo null")
            }
        } catch (e: Exception) {
        }
        log("isSystemPackage - $pkg : $value")
        return value
    }


    fun openAppInfo(context: Context,pkName:String){
        try {
            val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            intent.data = Uri.parse("package:$pkName")
            context.startActivity(intent)
        } catch (e: Exception) {
            log("openAppInfo error : $e")
        }

    }



    fun setLong( name: String, value: Long) {
        AGDPreferenceManager.getInstance().sADGPreferences.edit().putLong(name, value).apply()
    }


    fun getLong(name: String, defaultValue: Long): Long {
        return AGDPreferenceManager.getInstance().sADGPreferences.getLong(name, defaultValue)
    }


    fun setInt( name: String, value: Int) {
        AGDPreferenceManager.getInstance().sADGPreferences.edit().putInt(name, value).apply()
    }

    fun setBoolean( name: String, value: Boolean) {
        AGDPreferenceManager.getInstance().sADGPreferences.edit().putBoolean(name, value).apply()
    }

    fun getBoolean( name: String, defaultValue: Boolean): Boolean {
        return AGDPreferenceManager.getInstance().sADGPreferences.getBoolean(name, defaultValue)
    }


    fun setString(name: String, value: String) {
        AGDPreferenceManager.getInstance().sADGPreferences.edit().putString(name, value).apply()
    }

    fun getInt(name: String, defaultValue: Int): Int {
        return AGDPreferenceManager.getInstance().sADGPreferences.getInt(name, defaultValue)
    }


    fun getString( name: String, defaultValue: String): String {
        return AGDPreferenceManager.getInstance().sADGPreferences.getString(name, defaultValue)!!
    }


    fun openMainActivity(context: Context){
        try {
            val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            if(intent != null){
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
        } catch (e: Exception) {

        }
    }
    fun openMainActivity(context: Context,pkg:String){
        try {
            val intent = Intent(context, MainActivity::class.java) /*context.packageManager.getLaunchIntentForPackage(context.packageName)*/
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra("packageFromService",pkg)
            context.startActivity(intent)
        } catch (e: Exception) {

        }
    }

    fun viewAppInfo(context: Context,pkg:String) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.parse("package:${pkg}")
        context.startActivity(intent)
    }

    fun isOreoUp(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }

    fun canDrawOverlays(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }
    }


    fun getSkuList(): List<String> {
        return when (getIapLevel()) {
            2L -> listOf("sub_monthly_2", "sub_3_months_2", "sub_yearly_2")
            3L -> listOf("sub_monthly_3", "sub_3_months_3", "sub_yearly_3")
            4L -> listOf("sub_monthly_4", "sub_3_months_4", "sub_yearly_4")
            5L -> listOf("sub_monthly_5", "sub_3_months_5", "sub_yearly_5")
            else -> listOf("sub_monthly", "sub_3_months", "sub_yearly")
        }
    }

    fun rotateView(view: View) {
        val rotate = RotateAnimation(0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        rotate.duration = 500
        rotate.repeatCount = 1
        rotate.interpolator = LinearInterpolator()
        view.startAnimation(rotate)
    }



    fun getIapLevel(): Long {
        return getLong( Constant.REMOTE_CONFIGS_IAP_LEVEL, -1L)
    }

    fun setIapLevel(level: Long) {
        setLong( Constant.REMOTE_CONFIGS_IAP_LEVEL, level)
    }

    fun getLastTimeSuggestPurchase(): Long {
        return getLong("LastTimeSuggestPurchase", 0L)
    }

    fun setLastTimeSuggestPurchase(time: Long) {
        setLong("LastTimeSuggestPurchase", time)
    }

    fun setLastTimeAskReview(timeMillis: Long) {
        setLong("LastTimeAskReview", timeMillis)
    }

    fun getLastTimeAskReview(): Long {
        return getLong("LastTimeAskReview", 0L)
    }


    fun getPriceSubscription3Months(): String {
        return getString("PriceSubscription3Months", "")!!
    }

    fun getPriceSubscriptionYearly(): String {
        return getString("PriceSubscriptionYearly", "")!!
    }

    fun getSavePercentage(): String {
        return getString( "keySavePercentage", "")!!
    }

    fun setSavePercentage(savePercentage: String) {
        setString("keySavePercentage", savePercentage)
    }

    fun setPriceSubscription3Months(price: String) {
        setString("PriceSubscription3Months", price)
    }

    fun setPriceSubscriptionYearly(context: Context, price: String) {
        setString( "PriceSubscriptionYearly", price)
    }
}