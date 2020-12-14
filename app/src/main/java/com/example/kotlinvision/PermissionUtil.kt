package com.example.kotlinvision

import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionUtil {

    fun requestPermission(
        activity: Activity,
        requestCode: Int,
        vararg permissions: String
    ): Boolean {

        var granted = true
        val permissionNeeded = ArrayList<String>()

        permissions.forEach {
            val permissionCheck = ContextCompat.checkSelfPermission(activity, it)
            val hasPermission = permissionCheck == PackageManager.PERMISSION_GRANTED
            granted = granted and hasPermission //== granted && hasPermissionCheck

            if (!hasPermission) {
                permissionNeeded.add(it)
            }


        }
        return if (granted) {
            true
        } else {
            Log.d("퍼미션 테스트", "전")
            ActivityCompat.requestPermissions(
                activity,
                permissionNeeded.toTypedArray(),
                requestCode

            )
            Log.d("퍼미션 테스트", "후")
            false
        }

    }

    fun permissionGranted(
        requestCode: Int, permissionCode: Int, grantResults: IntArray
    ): Boolean {
        return requestCode == permissionCode && grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
    }
}