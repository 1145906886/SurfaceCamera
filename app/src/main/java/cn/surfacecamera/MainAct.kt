package cn.surfacecamera

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import cn.xarequest.mylibrary.IDCameraActivity

class MainAct:AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.btn).setOnClickListener {
            startActivity(Intent(this, IDCameraActivity::class.java))
        }
    }
}