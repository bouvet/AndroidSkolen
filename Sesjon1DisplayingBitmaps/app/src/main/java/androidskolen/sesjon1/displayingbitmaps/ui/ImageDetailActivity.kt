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
import android.support.v4.app.*
import android.support.v4.view.ViewPager
import android.util.DisplayMetrics
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.view.WindowManager.LayoutParams
import android.widget.Toast
import androidskolen.sesjon1.displayingbitmaps.R
import androidskolen.sesjon1.displayingbitmaps.provider.Images
import androidskolen.sesjon1.displayingbitmaps.util.ImageCache
import androidskolen.sesjon1.displayingbitmaps.util.ImageFetcher

class ImageDetailActivity : FragmentActivity(), OnClickListener {

    private lateinit var mAdapter: ImagePagerAdapter
    private lateinit var mPager: ViewPager
    /**
     * Called by the ViewPager child fragments to load images via the one ImageFetcher
     */
    lateinit var imageFetcher: ImageFetcher
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.image_detail_pager)

        // Fetch screen height and width, to use as our max size when loading images as this
        // activity runs full screen
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels

        // For this sample we'll use half of the longest width to resize our images. As the
        // image scaling ensures the image is larger than this, we should be left with a
        // resolution that is appropriate for both portrait and landscape. For best image quality
        // we shouldn't divide by 2, but this will use more memory and require a larger memory
        // cache.
        val longest = (if (height > width) height else width) / 2

        val cacheParams = ImageCache.ImageCacheParams(this, IMAGE_CACHE_DIR)
        cacheParams.setMemCacheSizePercent(0.25f) // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        imageFetcher = ImageFetcher(this, longest)
        imageFetcher.addImageCache(supportFragmentManager, cacheParams)
        imageFetcher.setLoadingImage(R.drawable.empty_photo)
        imageFetcher.setImageFadeIn(false)

        // Set up ViewPager and backing adapter
        mAdapter = ImagePagerAdapter(supportFragmentManager, Images.imageUrls.size)
        mPager = findViewById(R.id.pager)
        mPager.adapter = mAdapter
        mPager.pageMargin = resources.getDimension(R.dimen.horizontal_page_margin).toInt()
        mPager.offscreenPageLimit = 2

        // Set up activity to go full screen
        window.addFlags(LayoutParams.FLAG_FULLSCREEN)

        // Enable some additional newer visibility and ActionBar features to create a more
        // immersive photo viewing experience
        val actionBar = actionBar

        // Hide title text and set home as up
        actionBar?.setDisplayShowTitleEnabled(false)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        // Hide and show the ActionBar as the visibility changes
        mPager.setOnSystemUiVisibilityChangeListener { vis ->
            if (vis and View.SYSTEM_UI_FLAG_LOW_PROFILE != 0) {
                actionBar?.hide()
            } else {
                actionBar?.show()
            }
        }

        // Start low profile mode and hide ActionBar
        mPager.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE
        actionBar?.hide()

        // Set the current item based on the extra passed in to this activity
        val extraCurrentItem = intent.getIntExtra(EXTRA_IMAGE, -1)
        if (extraCurrentItem != -1) {
            mPager.currentItem = extraCurrentItem
        }
    }

    override fun onResume() {
        super.onResume()
        imageFetcher.setExitTasksEarly(false)
    }

    override fun onPause() {
        super.onPause()
        imageFetcher.setExitTasksEarly(true)
        imageFetcher.flushCache()
    }

    override fun onDestroy() {
        super.onDestroy()
        imageFetcher.closeCache()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                return true
            }
            R.id.clear_cache -> {
                imageFetcher.clearCache()
                Toast.makeText(this, R.string.clear_cache_complete_toast, Toast.LENGTH_SHORT).show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    /**
     * The main adapter that backs the ViewPager. A subclass of FragmentStatePagerAdapter as there
     * could be a large number of items in the ViewPager and we don't want to retain them all in
     * memory at once but create/destroy them on the fly.
     */
    private inner class ImagePagerAdapter(fm: FragmentManager, private val mSize: Int) : FragmentStatePagerAdapter(fm) {

        override fun getCount(): Int {
            return mSize
        }

        override fun getItem(position: Int): Fragment {
            return ImageDetailFragment.newInstance(Images.imageUrls[position])
        }
    }

    /**
     * Set on the ImageView in the ViewPager children fragments, to enable/disable low profile mode
     * when the ImageView is touched.
     */
    override fun onClick(v: View) {
        val vis = mPager.systemUiVisibility
        if (vis and View.SYSTEM_UI_FLAG_LOW_PROFILE != 0) {
            mPager.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        } else {
            mPager.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE
        }
    }

    companion object {
        private val IMAGE_CACHE_DIR = "images"
        val EXTRA_IMAGE = "extra_image"
    }
}
