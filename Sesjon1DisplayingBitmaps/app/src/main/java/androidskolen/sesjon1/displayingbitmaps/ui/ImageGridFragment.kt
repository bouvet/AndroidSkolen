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

import android.annotation.TargetApi
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewTreeObserver
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.Toast

import androidskolen.sesjon1.displayingbitmaps.BuildConfig
import androidskolen.sesjon1.displayingbitmaps.R
import androidskolen.sesjon1.displayingbitmaps.provider.Images
import androidskolen.sesjon1.displayingbitmaps.util.ImageCache
import androidskolen.sesjon1.displayingbitmaps.util.ImageFetcher

/**
 * The main fragment that powers the ImageGridActivity screen. Fairly straight forward GridView
 * implementation with the key addition being the ImageWorker class w/ImageCache to load children
 * asynchronously, keeping the UI nice and smooth and caching thumbnails for quick retrieval. The
 * cache is retained over configuration changes like orientation change so the images are populated
 * quickly if, for example, the user rotates the device.
 */
/**
 * Empty constructor as per the Fragment documentation
 */
class ImageGridFragment : Fragment(), AdapterView.OnItemClickListener {

    private var mImageThumbSize: Int = 0
    private var mImageThumbSpacing: Int = 0
    private lateinit var mAdapter: ImageAdapter
    private lateinit var mImageFetcher: ImageFetcher
    private var hasShownRationale: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        mImageThumbSize = resources.getDimensionPixelSize(R.dimen.image_thumbnail_size)
        mImageThumbSpacing = resources.getDimensionPixelSize(R.dimen.image_thumbnail_spacing)

        mAdapter = ImageAdapter(activity!!)

        val cacheParams = ImageCache.ImageCacheParams(activity!!, IMAGE_CACHE_DIR)

        cacheParams.setMemCacheSizePercent(0.25f) // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = ImageFetcher(activity!!, mImageThumbSize)
        mImageFetcher.setLoadingImage(R.drawable.empty_photo)
        mImageFetcher.addImageCache(activity!!.supportFragmentManager, cacheParams)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val v = inflater.inflate(R.layout.image_grid_fragment, container, false)
        val mGridView = v.findViewById<GridView>(R.id.gridView)
        mGridView.adapter = mAdapter
        mGridView.onItemClickListener = this
        // This listener is used to get the final width of the GridView and then calculate the
        // number of columns and the width of each column. The width of each column is variable
        // as the GridView has stretchMode=columnWidth. The column width is used to set the height
        // of each view so we get nice square thumbnails.
        mGridView.viewTreeObserver.addOnGlobalLayoutListener(
                object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        if (mAdapter.numColumns == 0) {
                            val numColumns = Math.floor((mGridView.width / (mImageThumbSize + mImageThumbSpacing)).toDouble()).toInt()
                            if (numColumns > 0) {
                                val columnWidth = mGridView.width / numColumns - mImageThumbSpacing
                                mAdapter.numColumns = numColumns
                                mAdapter.setItemHeight(columnWidth)
                                if (BuildConfig.DEBUG) {
                                    Log.d(TAG, "onCreateView - numColumns set to $numColumns")
                                }
                                mGridView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                            }
                        }
                    }
                })

        return v
    }

    override fun onResume() {
        super.onResume()
        mImageFetcher.setExitTasksEarly(false)
        mAdapter.notifyDataSetChanged()
    }

    override fun onPause() {
        super.onPause()
        mImageFetcher.setPauseWork(false)
        mImageFetcher.setExitTasksEarly(true)
        mImageFetcher.flushCache()
    }

    override fun onDestroy() {
        super.onDestroy()
        mImageFetcher.closeCache()
    }

    override fun onItemClick(parent: AdapterView<*>, v: View, position: Int, id: Long) {
        val i = Intent(activity, ImageDetailActivity::class.java)
        i.putExtra(ImageDetailActivity.EXTRA_IMAGE, id.toInt())
        // makeThumbnailScaleUpAnimation() looks kind of ugly here as the loading spinner may
        // show plus the thumbnail image in GridView is cropped. so using
        // makeScaleUpAnimation() instead.
        val options = ActivityOptions.makeScaleUpAnimation(v, 0, 0, v.width, v.height)
        activity!!.startActivity(i, options.toBundle())
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.clear_cache -> {
                mImageFetcher!!.clearCache()
                Toast.makeText(activity, R.string.clear_cache_complete_toast, Toast.LENGTH_SHORT).show()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * The main adapter that backs the GridView. This is fairly standard except the number of
     * columns in the GridView is used to create a fake top row of empty views as we use a
     * transparent ActionBar and don't want the real top row of images to start off covered by it.
     */
    private inner class ImageAdapter(private val mContext: Context) : BaseAdapter() {
        private var mItemHeight = 0
        var numColumns = 0
        private var mActionBarHeight = 0
        private var mImageViewLayoutParams: LayoutParams? = null

        init {
            mImageViewLayoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            // Calculate ActionBar height
            val tv = TypedValue()
            if (mContext.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                mActionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, mContext.resources.displayMetrics)
            }
        }

        override fun getCount(): Int {
            // If columns have yet to be determined, return no items
            return if (numColumns == 0) {
                0
            } else Images.imageThumbUrls.size + numColumns

            // Size + number of columns for top empty row
        }

        override fun getItem(position: Int): Any? {
            return if (position < numColumns)
                null
            else
                Images.imageThumbUrls[position - numColumns]
        }

        override fun getItemId(position: Int): Long {
            return (if (position < numColumns) 0 else position - numColumns).toLong()
        }

        override fun getViewTypeCount(): Int {
            // Two types of views, the normal ImageView and the top row of empty views
            return 2
        }

        override fun getItemViewType(position: Int): Int {
            return if (position < numColumns) 1 else 0
        }

        override fun hasStableIds(): Boolean {
            return true
        }

        override fun getView(position: Int, convertView: View?, container: ViewGroup): View {
            var convertView = convertView
            //BEGIN_INCLUDE(load_gridview_item)
            // First check if this is the top row
            if (position < numColumns) {
                if (convertView == null) {
                    convertView = View(mContext)
                }
                // Set empty view with height of ActionBar
                convertView.layoutParams = AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, mActionBarHeight)
                return convertView
            }

            // Now handle the main ImageView thumbnails
            val imageView: ImageView
            if (convertView == null) { // if it's not recycled, instantiate and initialize
                imageView = RecyclingImageView(mContext)
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP)
                imageView.setLayoutParams(mImageViewLayoutParams)
            } else { // Otherwise re-use the converted view
                imageView = (convertView as ImageView?)!!
            }

            // Check the height matches our calculated column width
            if (imageView.layoutParams.height != mItemHeight) {
                imageView.layoutParams = mImageViewLayoutParams
            }

            // Finally load the image asynchronously into the ImageView, this also takes care of
            // setting a placeholder image while the background thread runs
            mImageFetcher.loadImage(Images.imageThumbUrls[position - numColumns], imageView)
            mImageFetcher.setLoadingImage(R.drawable.empty_photo)
            return imageView
            //END_INCLUDE(load_gridview_item)
        }

        /**
         * Sets the item height. Useful for when we know the column width so the height can be set
         * to match.
         *
         * @param height
         */
        fun setItemHeight(height: Int) {
            if (height == mItemHeight) {
                return
            }
            mItemHeight = height
            mImageViewLayoutParams = LayoutParams(LayoutParams.MATCH_PARENT, mItemHeight)
            mImageFetcher.setImageSize(height)
            notifyDataSetChanged()
        }
    }

    companion object {
        private val TAG = "ImageGridFragment"
        private val IMAGE_CACHE_DIR = "thumbs"
        private val REQUEST_WRITE_EXTERNAL_STORAGE = 123
    }
}
