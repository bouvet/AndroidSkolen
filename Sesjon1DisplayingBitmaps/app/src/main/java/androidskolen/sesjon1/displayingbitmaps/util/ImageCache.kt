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

package androidskolen.sesjon1.displayingbitmaps.util

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Bitmap.Config
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.util.LruCache
import android.util.Log

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.lang.ref.SoftReference
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Collections
import java.util.HashSet

import androidskolen.sesjon1.displayingbitmaps.BuildConfig

/**
 * This class handles disk and memory caching of bitmaps in conjunction with the
 * [ImageWorker] class and its subclasses. Use
 * [ImageCache.getInstance] to get an instance of this
 * class, although usually a cache should be added directly to an [ImageWorker] by calling
 * [ImageWorker.addImageCache].
 */
class ImageCache
/**
 * Create a new ImageCache item using the specified parameters. This should not be
 * called directly by other classes, instead use
 * [ImageCache.getInstance] to fetch an ImageCache
 * instance.
 *
 * @param cacheParams The cache parameters to use to initialize the cache
 */
private constructor(cacheParams: ImageCacheParams) {

    private var mDiskLruCache: DiskLruCache? = null
    private lateinit var mMemoryCache: LruCache<String, BitmapDrawable>
    private lateinit var mCacheParams: ImageCacheParams
    private val mDiskCacheLock = Object()
    private var mDiskCacheStarting = true

    private lateinit var mReusableBitmaps: MutableSet<SoftReference<Bitmap>>
    private val mBitmapsLock = Object()

    init {
        init(cacheParams)
    }

    /**
     * Initialize the cache, providing all parameters.
     *
     * @param cacheParams The cache parameters to initialize the cache
     */
    private fun init(cacheParams: ImageCacheParams) {
        mCacheParams = cacheParams

        //BEGIN_INCLUDE(init_memory_cache)
        // Set up memory cache
        if (mCacheParams.memoryCacheEnabled) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Memory cache created (size = " + mCacheParams.memCacheSize + ")")
            }

            // If we're running on Honeycomb or newer, create a set of reusable bitmaps that can be
            // populated into the inBitmap field of BitmapFactory.Options. Note that the set is
            // of SoftReferences which will actually not be very effective due to the garbage
            // collector being aggressive clearing Soft/WeakReferences. A better approach
            // would be to use a strongly references bitmaps, however this would require some
            // balancing of memory usage between this set and the bitmap LruCache. It would also
            // require knowledge of the expected size of the bitmaps. From Honeycomb to JellyBean
            // the size would need to be precise, from KitKat onward the size would just need to
            // be the upper bound (due to changes in how inBitmap can re-use bitmaps).
            mReusableBitmaps = Collections.synchronizedSet(HashSet())

            mMemoryCache = object : LruCache<String, BitmapDrawable>(mCacheParams.memCacheSize) {

                /**
                 * Notify the removed entry that is no longer being cached
                 */
                override fun entryRemoved(evicted: Boolean, key: String?,
                                          oldValue: BitmapDrawable?, newValue: BitmapDrawable?) {
                    if (RecyclingBitmapDrawable::class.java.isInstance(oldValue)) {
                        // The removed entry is a recycling drawable, so notify it
                        // that it has been removed from the memory cache
                        (oldValue as RecyclingBitmapDrawable).setIsCached(false)
                    } else {
                        // The removed entry is a standard BitmapDrawable

                        // We're running on Honeycomb or later, so add the bitmap
                        // to a SoftReference set for possible use with inBitmap later
                        mReusableBitmaps.add(SoftReference(oldValue!!.bitmap))
                    }
                }

                /**
                 * Measure item size in kilobytes rather than units which is more practical
                 * for a bitmap cache
                 */
                override fun sizeOf(key: String?, value: BitmapDrawable?): Int {
                    val bitmapSize = getBitmapSize(value!!) / 1024
                    return if (bitmapSize == 0) 1 else bitmapSize
                }
            }
        }
        //END_INCLUDE(init_memory_cache)

        // By default the disk cache is not initialized here as it should be initialized
        // on a separate thread due to disk access.
        if (cacheParams.initDiskCacheOnCreate) {
            // Set up disk cache
            initDiskCache()
        }
    }

    /**
     * Initializes the disk cache.  Note that this includes disk access so this should not be
     * executed on the main/UI thread. By default an ImageCache does not initialize the disk
     * cache when it is created, instead you should call initDiskCache() to initialize it on a
     * background thread.
     */
    fun initDiskCache() {
        // Set up disk cache
        synchronized(mDiskCacheLock) {
            if (mDiskLruCache == null || mDiskLruCache!!.isClosed) {
                val diskCacheDir = mCacheParams.diskCacheDir
                if (mCacheParams.diskCacheEnabled && diskCacheDir != null) {
                    if (!diskCacheDir.exists()) {
                        diskCacheDir.mkdirs()
                    }
                    if (getUsableSpace(diskCacheDir) > mCacheParams.diskCacheSize) {
                        try {
                            mDiskLruCache = DiskLruCache.open(diskCacheDir, 1, 1, mCacheParams.diskCacheSize.toLong())
                            if (BuildConfig.DEBUG) {
                                Log.d(TAG, "Disk cache initialized")
                            }
                        } catch (e: IOException) {
                            mCacheParams.diskCacheDir = null
                            Log.e(TAG, "initDiskCache - $e")
                        }

                    }
                }
            }
            mDiskCacheStarting = false
            mDiskCacheLock.notifyAll()
        }
    }

    /**
     * Adds a bitmap to both memory and disk cache.
     * @param data Unique identifier for the bitmap to store
     * @param value The bitmap drawable to store
     */
    fun addBitmapToCache(data: String?, value: BitmapDrawable?) {
        //BEGIN_INCLUDE(add_bitmap_to_cache)
        if (data == null || value == null) {
            return
        }

        // Add to memory cache
        if (RecyclingBitmapDrawable::class.java.isInstance(value)) {
            // The removed entry is a recycling drawable, so notify it
            // that it has been added into the memory cache
            (value as RecyclingBitmapDrawable).setIsCached(true)
        }
        mMemoryCache.put(data, value)

        synchronized(mDiskCacheLock) {
            // Add to disk cache
            if (mDiskLruCache != null) {
                val key = hashKeyForDisk(data)
                var out: OutputStream? = null
                try {
                    val snapshot = mDiskLruCache!![key]
                    if (snapshot == null) {
                        val editor = mDiskLruCache!!.edit(key)
                        if (editor != null) {
                            out = editor.newOutputStream(DISK_CACHE_INDEX)
                            value.bitmap.compress(
                                    mCacheParams.compressFormat, mCacheParams.compressQuality, out)
                            editor.commit()
                            out.close()
                        }
                    } else {
                        snapshot.getInputStream(DISK_CACHE_INDEX).close()
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "addBitmapToCache - $e")
                } finally {
                    try {
                        out?.close()
                    } catch (e: IOException) {
                    }

                }
            }
        }
        //END_INCLUDE(add_bitmap_to_cache)
    }

    /**
     * Get from memory cache.
     *
     * @param data Unique identifier for which item to get
     * @return The bitmap drawable if found in cache, null otherwise
     */
    fun getBitmapFromMemCache(data: String): BitmapDrawable? {
        val memValue: BitmapDrawable? = mMemoryCache.get(data)

        if (BuildConfig.DEBUG && memValue != null) {
            Log.d(TAG, "Memory cache hit")
        }

        return memValue
        //END_INCLUDE(get_bitmap_from_mem_cache)
    }

    /**
     * Get from disk cache.
     *
     * @param data Unique identifier for which item to get
     * @return The bitmap if found in cache, null otherwise
     */
    fun getBitmapFromDiskCache(data: String): Bitmap? {
        //BEGIN_INCLUDE(get_bitmap_from_disk_cache)
        val key = hashKeyForDisk(data)
        var bitmap: Bitmap? = null

        synchronized(mDiskCacheLock) {
            while (mDiskCacheStarting) {
                try {
                    mDiskCacheLock.wait()
                } catch (e: InterruptedException) {
                }

            }
            if (mDiskLruCache != null) {
                var inputStream: InputStream? = null
                try {
                    val snapshot = mDiskLruCache!![key]
                    if (snapshot != null) {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "Disk cache hit")
                        }
                        inputStream = snapshot.getInputStream(DISK_CACHE_INDEX)
                        if (inputStream != null) {
                            val fd = (inputStream as FileInputStream).fd

                            // Decode bitmap, but we don't want to sample so give
                            // MAX_VALUE as the target dimensions
                            bitmap = ImageResizer.decodeSampledBitmapFromDescriptor(
                                    fd, Integer.MAX_VALUE, Integer.MAX_VALUE, this)
                        }
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "getBitmapFromDiskCache - $e")
                } finally {
                    try {
                        inputStream?.close()
                    } catch (e: IOException) {
                    }

                }
            }
            return bitmap
        }
        //END_INCLUDE(get_bitmap_from_disk_cache)
    }

    /**
     * @param options - BitmapFactory.Options with out* options populated
     * @return Bitmap that case be used for inBitmap
     */
    fun getBitmapFromReusableSet(options: BitmapFactory.Options): Bitmap? {
        //BEGIN_INCLUDE(get_bitmap_from_reusable_set)
        var bitmap: Bitmap? = null

        if (!mReusableBitmaps.isEmpty()) {
            synchronized(mBitmapsLock) {
                val iterator = mReusableBitmaps.iterator()
                var item: Bitmap?

                while (iterator.hasNext()) {
                    item = iterator.next().get()

                    if (null != item && item.isMutable) {
                        // Check to see it the item can be used for inBitmap
                        if (canUseForInBitmap(item, options)) {
                            bitmap = item

                            // Remove from reusable set so it can't be used again
                            iterator.remove()
                            break
                        }
                    } else {
                        // Remove from the set if the reference has been cleared.
                        iterator.remove()
                    }
                }
            }
        }

        return bitmap
        //END_INCLUDE(get_bitmap_from_reusable_set)
    }

    /**
     * Clears both the memory and disk cache associated with this ImageCache item. Note that
     * this includes disk access so this should not be executed on the main/UI thread.
     */
    fun clearCache() {
        mMemoryCache.evictAll()
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Memory cache cleared")
        }

        synchronized(mDiskCacheLock) {
            mDiskCacheStarting = true
            if (mDiskLruCache != null && !mDiskLruCache!!.isClosed) {
                try {
                    mDiskLruCache!!.delete()
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "Disk cache cleared")
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "clearCache - $e")
                }

                mDiskLruCache = null
                initDiskCache()
            }
        }
    }

    /**
     * Flushes the disk cache associated with this ImageCache item. Note that this includes
     * disk access so this should not be executed on the main/UI thread.
     */
    fun flush() {
        synchronized(mDiskCacheLock) {
            if (mDiskLruCache != null) {
                try {
                    mDiskLruCache!!.flush()
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "Disk cache flushed")
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "flush - $e")
                }

            }
        }
    }

    /**
     * Closes the disk cache associated with this ImageCache item. Note that this includes
     * disk access so this should not be executed on the main/UI thread.
     */
    fun close() {
        synchronized(mDiskCacheLock) {
            if (mDiskLruCache != null) {
                try {
                    if (!mDiskLruCache!!.isClosed) {
                        mDiskLruCache!!.close()
                        mDiskLruCache = null
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "Disk cache closed")
                        }
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "close - $e")
                }

            }
        }
    }

    /**
     * A holder class that contains cache parameters.
     */
    class ImageCacheParams
    /**
     * Create a set of image cache parameters that can be provided to
     * [ImageCache.getInstance] or
     * [ImageWorker.addImageCache].
     * @param context A context to use.
     * @param diskCacheDirectoryName A unique subdirectory name that will be appended to the
     * application cache directory. Usually "cache" or "images"
     * is sufficient.
     */
    (context: Context, diskCacheDirectoryName: String) {
        var memCacheSize = DEFAULT_MEM_CACHE_SIZE
        var diskCacheSize = DEFAULT_DISK_CACHE_SIZE
        var diskCacheDir: File? = null
        var compressFormat = DEFAULT_COMPRESS_FORMAT
        var compressQuality = DEFAULT_COMPRESS_QUALITY
        var memoryCacheEnabled = DEFAULT_MEM_CACHE_ENABLED
        var diskCacheEnabled = DEFAULT_DISK_CACHE_ENABLED
        var initDiskCacheOnCreate = DEFAULT_INIT_DISK_CACHE_ON_CREATE

        init {
            diskCacheDir = getDiskCacheDir(context, diskCacheDirectoryName)
        }

        /**
         * Sets the memory cache size based on a percentage of the max available VM memory.
         * Eg. setting percent to 0.2 would set the memory cache to one fifth of the available
         * memory. Throws [IllegalArgumentException] if percent is < 0.01 or > .8.
         * memCacheSize is stored in kilobytes instead of bytes as this will eventually be passed
         * to construct a LruCache which takes an int in its constructor.
         *
         * This value should be chosen carefully based on a number of factors
         * Refer to the corresponding Android Training class for more discussion:
         * http://developer.android.com/training/displaying-bitmaps/
         *
         * @param percent Percent of available app memory to use to size memory cache
         */
        fun setMemCacheSizePercent(percent: Float) {
            if (percent < 0.01f || percent > 0.8f) {
                throw IllegalArgumentException("setMemCacheSizePercent - percent must be " + "between 0.01 and 0.8 (inclusive)")
            }
            memCacheSize = Math.round(percent * Runtime.getRuntime().maxMemory() / 1024)
        }
    }

    /**
     * A simple non-UI Fragment that stores a single Object and is retained over configuration
     * changes. It will be used to retain the ImageCache item.
     */
    /**
     * Empty constructor as per the Fragment documentation
     */
    class RetainFragment : Fragment() {

        var item: Any? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            // Make sure this Fragment is retained over a configuration change
            retainInstance = true
        }
    }

    companion object {
        private val TAG = "ImageCache"

        // Default memory cache size in kilobytes
        private val DEFAULT_MEM_CACHE_SIZE = 1024 * 5 // 5MB

        // Default disk cache size in bytes
        private val DEFAULT_DISK_CACHE_SIZE = 1024 * 1024 * 10 // 10MB

        // Compression settings when writing images to disk cache
        private val DEFAULT_COMPRESS_FORMAT = CompressFormat.JPEG
        private val DEFAULT_COMPRESS_QUALITY = 70
        private val DISK_CACHE_INDEX = 0

        // Constants to easily toggle various caches
        private val DEFAULT_MEM_CACHE_ENABLED = true
        private val DEFAULT_DISK_CACHE_ENABLED = true
        private val DEFAULT_INIT_DISK_CACHE_ON_CREATE = false

        /**
         * Return an [ImageCache] instance. A [RetainFragment] is used to retain the
         * ImageCache item across configuration changes such as a change in device orientation.
         *
         * @param fragmentManager The fragment manager to use when dealing with the retained fragment.
         * @param cacheParams The cache parameters to use if the ImageCache needs instantiation.
         * @return An existing retained ImageCache item or a new one if one did not exist
         */
        fun getInstance(
                fragmentManager: FragmentManager, cacheParams: ImageCacheParams): ImageCache {

            // Search for, or create an instance of the non-UI RetainFragment
            val mRetainFragment = findOrCreateRetainFragment(fragmentManager)

            // See if we already have an ImageCache stored in RetainFragment
            var imageCache = mRetainFragment.item as ImageCache?

            // No existing ImageCache, create one and store it in RetainFragment
            if (imageCache == null) {
                imageCache = ImageCache(cacheParams)
                mRetainFragment.item = imageCache
            }

            return imageCache
        }

        /**
         * @param candidate - Bitmap to check
         * @param targetOptions - Options that have the out* value populated
         * @return true if `candidate` can be used for inBitmap re-use with
         * `targetOptions`
         */
        @TargetApi(VERSION_CODES.KITKAT)
        private fun canUseForInBitmap(
                candidate: Bitmap, targetOptions: BitmapFactory.Options): Boolean {
            //BEGIN_INCLUDE(can_use_for_inbitmap)
            // From Android 4.4 (KitKat) onward we can re-use if the byte size of the new bitmap
            // is smaller than the reusable bitmap candidate allocation byte count.
            val width = targetOptions.outWidth / targetOptions.inSampleSize
            val height = targetOptions.outHeight / targetOptions.inSampleSize
            val byteCount = width * height * getBytesPerPixel(candidate.config)
            return byteCount <= candidate.allocationByteCount
            //END_INCLUDE(can_use_for_inbitmap)
        }

        /**
         * Return the byte usage per pixel of a bitmap based on its configuration.
         * @param config The bitmap configuration.
         * @return The byte usage per pixel.
         */
        private fun getBytesPerPixel(config: Config): Int {
            return when (config) {
                Config.ARGB_8888 -> 4
                Config.RGB_565 -> 2
                Config.ARGB_4444 -> 2
                Config.ALPHA_8 -> 1
                else -> 1
            }
        }

        /**
         * Get a usable cache directory (external if available, internal otherwise).
         *
         * @param context The context to use
         * @param uniqueName A unique directory name to append to the cache dir
         * @return The cache dir
         */
        fun getDiskCacheDir(context: Context, uniqueName: String): File {
            // Check if media is mounted or storage is built-in, if so, try and use external cache dir
            // otherwise use internal cache dir
            val cachePath = if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() || !isExternalStorageRemovable)
                getExternalCacheDir(context)!!.path
            else
                context.cacheDir.path

            return File(cachePath + File.separator + uniqueName)
        }

        /**
         * A hashing method that changes a string (like a URL) into a hash suitable for using as a
         * disk filename.
         */
        fun hashKeyForDisk(key: String): String {
            return try {
                val mDigest = MessageDigest.getInstance("MD5")
                mDigest.update(key.toByteArray())
                bytesToHexString(mDigest.digest())
            } catch (e: NoSuchAlgorithmException) {
                key.hashCode().toString()
            }
        }

        private fun bytesToHexString(bytes: ByteArray): String {
            // http://stackoverflow.com/questions/332079
            val sb = StringBuilder()
            for (i in bytes.indices) {
                val asInt = bytes[i].toInt() and 0xFF
                val hex = Integer.toHexString(asInt)
                if (hex.length == 1) {
                    sb.append('0')
                }
                sb.append(hex)
            }
            return sb.toString()
        }

        /**
         * Get the size in bytes of a bitmap in a BitmapDrawable. Note that from Android 4.4 (KitKat)
         * onward this returns the allocated memory size of the bitmap which can be larger than the
         * actual bitmap data byte count (in the case it was re-used).
         *
         * @param value
         * @return size in bytes
         */
        fun getBitmapSize(value: BitmapDrawable): Int {
            val bitmap = value.bitmap

            // From KitKat onward use getAllocationByteCount() as allocated bytes can potentially be
            // larger than bitmap byte count.
            return bitmap.allocationByteCount
        }

        /**
         * Check if external storage is built-in or removable.
         *
         * @return True if external storage is removable (like an SD card), false
         * otherwise.
         */
        val isExternalStorageRemovable: Boolean
            get() = Environment.isExternalStorageRemovable()

        /**
         * Get the external app cache directory.
         *
         * @param context The context to use
         * @return The external cache dir
         */
        fun getExternalCacheDir(context: Context): File? {
            return context.externalCacheDir
        }

        /**
         * Check how much usable space is available at a given path.
         *
         * @param path The path to check
         * @return The space available in bytes
         */
        fun getUsableSpace(path: File): Long {
            return path.usableSpace
        }

        /**
         * Locate an existing instance of this Fragment or if not found, create and
         * add it using FragmentManager.
         *
         * @param fm The FragmentManager manager to use.
         * @return The existing instance of the Fragment or the new instance if just
         * created.
         */
        private fun findOrCreateRetainFragment(fm: FragmentManager): RetainFragment {
            //BEGIN_INCLUDE(find_create_retain_fragment)
            // Check to see if we have retained the worker fragment.
            var mRetainFragment: RetainFragment? = fm.findFragmentByTag(TAG) as RetainFragment?

            // If not retained (or first time running), we need to create and add it.
            if (mRetainFragment == null) {
                mRetainFragment = RetainFragment()
                fm.beginTransaction().add(mRetainFragment, TAG).commitAllowingStateLoss()
            }

            return mRetainFragment
            //END_INCLUDE(find_create_retain_fragment)
        }
    }

}
