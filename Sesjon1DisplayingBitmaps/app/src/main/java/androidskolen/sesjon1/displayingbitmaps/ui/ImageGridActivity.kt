/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidskolen.sesjon1.displayingbitmaps.ui

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.util.Log

/**
 * Simple FragmentActivity to hold the main [ImageGridFragment] and not much else.
 */
class ImageGridActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.w("username", intent.extras?.getString("username"))
        Log.w("password", intent.extras?.getString("password"))

        if (supportFragmentManager.findFragmentByTag(TAG) == null) {
            val ft = supportFragmentManager.beginTransaction()
            ft.add(android.R.id.content, ImageGridFragment(), TAG)
            ft.commit()
        }
    }

    companion object {
        private val TAG = "ImageGridActivity"
    }
}
