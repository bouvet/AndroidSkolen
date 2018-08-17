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
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar

import androidskolen.sesjon1.displayingbitmaps.R
import androidskolen.sesjon1.displayingbitmaps.util.ImageFetcher
import androidskolen.sesjon1.displayingbitmaps.util.ImageWorker

/**
 * This fragment will populate the children of the ViewPager from ImageDetailActivity.
 */
class ImageDetailFragment : Fragment(), ImageWorker.OnImageLoadedListener {

    private var mImageUrl: String? = null
    private lateinit var mImageView: ImageView
    private lateinit var mProgressBar: ProgressBar
    private lateinit var mImageFetcher: ImageFetcher

    /**
     * Populate image using a url from extras, use the convenience factory method
     * [ImageDetailFragment.newInstance] to create this fragment.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mImageUrl = if (arguments != null) arguments!!.getString(IMAGE_DATA_EXTRA) else null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate and locate the main ImageView
        val v = inflater.inflate(R.layout.image_detail_fragment, container, false)
        mImageView = v.findViewById(R.id.imageView)
        mProgressBar = v.findViewById(R.id.progressbar)
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // Use the parent activity to load the image asynchronously into the ImageView (so a single
        // cache can be used over all pages in the ViewPager
        if (ImageDetailActivity::class.java.isInstance(activity)) {
            mImageFetcher = (activity as ImageDetailActivity).imageFetcher
            mImageFetcher.loadImage(mImageUrl, mImageView, this)
        }

        // Pass clicks on the ImageView to the parent activity to handle
        if (OnClickListener::class.java.isInstance(activity)) {
            mImageView.setOnClickListener(activity as OnClickListener?)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancel any pending image work
        ImageWorker.cancelWork(mImageView)
        mImageView.setImageDrawable(null)
    }

    override fun onImageLoaded(success: Boolean) {
        // Set loading spinner to gone once image has loaded. Cloud also show
        // an error view here if needed.
        mProgressBar.visibility = View.GONE
    }

    companion object {
        private val IMAGE_DATA_EXTRA = "extra_image_data"

        /**
         * Factory method to generate a new instance of the fragment given an image number.
         *
         * @param imageUrl The image url to load
         * @return A new instance of ImageDetailFragment with imageNum extras
         */
        fun newInstance(imageUrl: String): ImageDetailFragment {
            val f = ImageDetailFragment()

            val args = Bundle()
            args.putString(IMAGE_DATA_EXTRA, imageUrl)
            f.arguments = args

            return f
        }
    }
}
