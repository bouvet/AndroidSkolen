package no.bouvet.androidskolen.storecontacts.fragments

import android.Manifest
import android.app.Activity
import android.support.v4.app.ActivityCompat
import android.support.v4.content.PermissionChecker

class PermissionHandling {

    companion object {

        fun hasPermissionsForContacts(activity: Activity) : Boolean {
            return PermissionChecker.checkSelfPermission(activity, Manifest.permission.READ_CONTACTS) == PermissionChecker.PERMISSION_GRANTED
                    && PermissionChecker.checkSelfPermission(activity, Manifest.permission.WRITE_CONTACTS) == PermissionChecker.PERMISSION_GRANTED
        }

        fun requestPermissionForContacts(activity: Activity) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS), 10)
        }

    }
}