package cn.xarequest.mylibrary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity


object IDCardCamera {

    const val PIC_PATH = "PIC_PATH"
    const val IS_FRONT = "IS_FRONT"

    fun openFront(activity: AppCompatActivity, picPath: String) {
        val intent = Intent(activity, IDCameraActivity::class.java)
        intent.putExtra(PIC_PATH, picPath)
        intent.putExtra(IS_FRONT, true)
        activity.startActivity(intent)
    }

    fun openBack(activity: AppCompatActivity, picPath: String) {
        val intent = Intent(activity, IDCameraActivity::class.java)
        intent.putExtra(PIC_PATH, picPath)
        intent.putExtra(IS_FRONT, false)
        activity.startActivity(intent)
    }
}