package com.agooday.baseapp.util

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import com.agooday.baseapp.R


/**
 * Created by Hungnd on 3/29/2017.
 */

object DialogUtils {
    fun showCustomDialog(context: Context, titleStringId: Int, messageStringID: Int, positiveStringId: Int, negativeStringId: Int, cancellable: Boolean, okListener: DialogInterface.OnClickListener, cancelListener: DialogInterface.OnClickListener) {
        val dialog = AlertDialog.Builder(context)
                .setTitle(titleStringId)
                .setMessage(messageStringID)
                .setPositiveButton(positiveStringId, okListener)
                .setCancelable(cancellable)
                .setNegativeButton(negativeStringId, cancelListener)
        dialog.show()
    }

    /*fun requestBuyPremiumIfNeeded(activity: Activity) {
        activity.let { context ->
            if (!AppUtil.isPremium) {
                showCustomDialog(context, R.string.upgrade, R.string.upgrade_sum, R.string.ok, R.string.cancel, false,
                        DialogInterface.OnClickListener { _, _ ->
                            run {
                                activity.onBackPressed()
                                activity.startActivity(Intent(context, GoPremiumActivity::class.java))
                            }
                        },
                        DialogInterface.OnClickListener { _, _ -> activity.onBackPressed() })
            }
        }
    }*/

}
