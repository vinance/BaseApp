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
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import com.agooday.baseapp.BuildConfig
import com.agooday.baseapp.MainActivity
import com.agooday.baseapp.R
import com.agooday.preference.AGDPreferenceManager
import com.agooday.preference.model.PrefBoolean
import com.agooday.preference.model.PrefLong


object AppUtil {





    fun log(content:String){
        if(BuildConfig.DEBUG){
            Log.d("tien.hien",content)
        }
    }


    var isPremium by PrefBoolean( "PREMIUM_VERSION", true)


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

    fun setInt(context: Context, name: String, value: Int) {
        AGDPreferenceManager.getInstance().sADGPreferences.edit().putInt(name, value).apply()
    }

    fun setBoolean(context: Context, name: String, value: Boolean) {
        AGDPreferenceManager.getInstance().sADGPreferences.edit().putBoolean(name, value).apply()
    }

    fun getBoolean(context: Context, name: String, defaultValue: Boolean): Boolean {
        return AGDPreferenceManager.getInstance().sADGPreferences.getBoolean(name, defaultValue)
    }


    fun setString(context: Context, name: String, value: String) {
        AGDPreferenceManager.getInstance().sADGPreferences.edit().putString(name, value).apply()
    }

    fun getInt(context: Context, name: String, defaultValue: Int): Int {
        return AGDPreferenceManager.getInstance().sADGPreferences.getInt(name, defaultValue)
    }


    fun getString(context: Context, name: String, defaultValue: String): String {
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
}