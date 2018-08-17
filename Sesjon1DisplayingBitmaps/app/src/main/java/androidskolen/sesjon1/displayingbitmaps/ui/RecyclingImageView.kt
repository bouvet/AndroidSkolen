/*
 * Copyright (C) 2013 The Android Open Source Project
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

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet

import androidskolen.sesjon1.displayingbitmaps.util.RecyclingBitmapDrawable

/**
 * Sub-class of ImageView which automatically notifies the drawable when it is
 * being displayed.
 */
class RecyclingImageView : AppCompatImageView {

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    /**
     * @see android.widget.ImageView.onDetachedFromWindow
     */
    override fun onDetachedFromWindow() {
        // This has been detached from Window, so clear the drawable
        setImageDrawable(null)

        super.onDetachedFromWindow()
    }

    /**
     * @see android.widget.ImageView.setImageDrawable
     */
    override fun setImageDrawable(drawable: Drawable?) {
        // Keep hold of previous Drawable
        val previousDrawable = getDrawable()

        // Call super to set new Drawable
        super.setImageDrawable(drawable)

        // Notify new Drawable that it is being displayed
        notifyDrawable(drawable, true)

        // Notify old Drawable so it is no longer being displayed
        notifyDrawable(previousDrawable, false)
    }

    /**
     * Notifies the drawable that it's displayed state has changed.
     */
    private fun notifyDrawable(drawable: Drawable?, isDisplayed: Boolean) {
        if (drawable is RecyclingBitmapDrawable) {
            // The drawable is a CountingBitmapDrawable, so notify it
            drawable.setIsDisplayed(isDisplayed)
        } else if (drawable is LayerDrawable) {
            // The drawable is a LayerDrawable, so recurse on each layer
            val layerDrawable = drawable as LayerDrawable?
            var i = 0
            val z = layerDrawable!!.numberOfLayers
            while (i < z) {
                notifyDrawable(layerDrawable.getDrawable(i), isDisplayed)
                i++
            }
        }
    }

}
