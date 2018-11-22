package com.don.working.with.dropboxapi

import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.dropbox.core.android.Auth
import android.content.Intent
import android.util.Log
import com.don.working.with.dropboxapi.task.DropboxClientFactory
import com.don.working.with.dropboxapi.task.GetCurrentAccountTask
import com.dropbox.core.v2.users.FullAccount
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : DropboxActivity() {
    override fun loadData() {
        GetCurrentAccountTask(
            DropboxClientFactory.getClient(),
            object : GetCurrentAccountTask.Callback {
                override fun onComplete(result: FullAccount) {
                    tv_email.text = result.email
                    tv_name.text = result.name.displayName
                    tv_status.text = result.accountType.name
                }

                override fun onError(e: Exception) {
                    Log.e(javaClass.name, "Failed to get account details.", e)
                }
            }).execute()
    }

    private val TAG = MainActivity::class.java.simpleName

    val MULTIPLE_PERMISSIONS = 10 // code you want.
    internal var permissions = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (checkPermission()) {
            btn_login.setOnClickListener {
                Auth.startOAuth2Authentication(this, getString(R.string.app_key));
            }

            btn_files.setOnClickListener {

                startActivity(Intent(this@MainActivity, FilesActivity::class.java))
            }

        }
    }


    private fun checkPermission(): Boolean {
        var result: Int
        val listPermissionsNeeded = ArrayList<String>()
        for (p in permissions) {
            result = ContextCompat.checkSelfPermission(applicationContext, p)
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p)
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), MULTIPLE_PERMISSIONS)
            return false
        }
        return true
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MULTIPLE_PERMISSIONS -> {
                /*    val ReadPhoneState = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val Camera = grantResults[1] == PackageManager.PERMISSION_GRANTED*/
                val readExternalStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val writeExternalStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED

                if (readExternalStorage && writeExternalStorage) {

                } else {
                    Toast.makeText(this, "GAGAL", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


}
