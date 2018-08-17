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

import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.IOException
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

import androidskolen.sesjon1.displayingbitmaps.BuildConfig
import androidskolen.sesjon1.displayingbitmaps.R

/**
 * A simple subclass of [ImageResizer] that fetches and resizes images fetched from a URL.
 */
class ImageFetcher(context: Context, imageSize: Int) : ImageResizer(context, imageSize) {

    private var mHttpDiskCache: DiskLruCache? = null
    private var mHttpCacheDir: File
    private var mHttpDiskCacheStarting = true
    private val mHttpDiskCacheLock = Object()

    init {
        checkConnection(context)
        mHttpCacheDir = ImageCache.getDiskCacheDir(context, HTTP_CACHE_DIR)
    }

    override fun initDiskCacheInternal() {
        super.initDiskCacheInternal()
        initHttpDiskCache()
    }

    private fun initHttpDiskCache() {
        if (!mHttpCacheDir.exists()) {
            mHttpCacheDir.mkdirs()
        }
        synchronized(mHttpDiskCacheLock) {
            if (ImageCache.getUsableSpace(mHttpCacheDir) > HTTP_CACHE_SIZE) {
                try {
                    mHttpDiskCache = DiskLruCache.open(mHttpCacheDir, 1, 1, HTTP_CACHE_SIZE.toLong())
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "HTTP cache initialized")
                    }
                } catch (e: IOException) {
                    mHttpDiskCache = null
                }

            }
            mHttpDiskCacheStarting = false
            mHttpDiskCacheLock.notifyAll()
        }
    }

    override fun clearCacheInternal() {
        super.clearCacheInternal()
        synchronized(mHttpDiskCacheLock) {
            if (mHttpDiskCache != null && !mHttpDiskCache!!.isClosed) {
                try {
                    mHttpDiskCache!!.delete()
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "HTTP cache cleared")
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "clearCacheInternal - $e")
                }

                mHttpDiskCache = null
                mHttpDiskCacheStarting = true
                initHttpDiskCache()
            }
        }
    }

    override fun flushCacheInternal() {
        super.flushCacheInternal()
        synchronized(mHttpDiskCacheLock) {
            if (mHttpDiskCache != null) {
                try {
                    mHttpDiskCache!!.flush()
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "HTTP cache flushed")
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "flush - $e")
                }

            }
        }
    }

    override fun closeCacheInternal() {
        super.closeCacheInternal()
        synchronized(mHttpDiskCacheLock) {
            if (mHttpDiskCache != null) {
                try {
                    if (!mHttpDiskCache!!.isClosed) {
                        mHttpDiskCache!!.close()
                        mHttpDiskCache = null
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "HTTP cache closed")
                        }
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "closeCacheInternal - $e")
                }

            }
        }
    }

    /**
     * Simple network connection check.
     */
    private fun checkConnection(context: Context) {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo
        if (networkInfo == null || !networkInfo.isConnectedOrConnecting) {
            Toast.makeText(context, R.string.no_network_connection_toast, Toast.LENGTH_LONG).show()
            Log.e(TAG, "checkConnection - no connection found")
        }
    }

    /**
     * The main process method, which will be called by the ImageWorker in the AsyncTask background
     * thread.
     *
     * @param data The data to load the bitmap, in this case, a regular http URL
     * @return The downloaded and resized bitmap
     */
    private fun processBitmap(data: String): Bitmap? {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "processBitmap - $data")
        }

        val key = ImageCache.hashKeyForDisk(data)
        var fileDescriptor: FileDescriptor? = null
        var fileInputStream: FileInputStream? = null
        var snapshot: DiskLruCache.Snapshot?
        synchronized(mHttpDiskCacheLock) {
            // Wait for disk cache to initialize
            while (mHttpDiskCacheStarting) {
                try {
                    mHttpDiskCacheLock.wait()
                } catch (e: InterruptedException) {
                }

            }

            if (mHttpDiskCache != null) {
                try {
                    snapshot = mHttpDiskCache!![key]
                    if (snapshot == null) {
                        if (BuildConfig.DEBUG) {
                            Log.d(TAG, "processBitmap, not found in http cache, downloading...")
                        }
                        val editor = mHttpDiskCache!!.edit(key)
                        if (editor != null) {
                            if (downloadUrlToStream(data,
                                            editor.newOutputStream(DISK_CACHE_INDEX))) {
                                editor.commit()
                            } else {
                                editor.abort()
                            }
                        }
                        snapshot = mHttpDiskCache!![key]
                    }
                    if (snapshot != null) {
                        fileInputStream = snapshot!!.getInputStream(DISK_CACHE_INDEX) as FileInputStream
                        fileDescriptor = fileInputStream!!.fd
                    }
                } catch (e: IOException) {
                    Log.e(TAG, "processBitmap - $e")
                } catch (e: IllegalStateException) {
                    Log.e(TAG, "processBitmap - $e")
                } finally {
                    if (fileDescriptor == null && fileInputStream != null) {
                        try {
                            fileInputStream!!.close()
                        } catch (e: IOException) {
                        }

                    }
                }
            }
        }

        var bitmap: Bitmap? = null
        if (fileDescriptor != null) {
            bitmap = ImageResizer.decodeSampledBitmapFromDescriptor(fileDescriptor!!, mImageWidth,
                    mImageHeight, imageCache!!)
        }
        if (fileInputStream != null) {
            try {
                fileInputStream!!.close()
            } catch (e: IOException) {
            }

        }
        return bitmap
    }

    override fun processBitmap(data: Any): Bitmap? {
        return processBitmap(data.toString())
    }

    /**
     * Download a bitmap from a URL and write the content to an output stream.
     *
     * @param urlString The URL to fetch
     * @return true if successful, false otherwise
     */
    fun downloadUrlToStream(urlString: String, outputStream: OutputStream): Boolean {
        var urlConnection: HttpURLConnection? = null
        var out: BufferedOutputStream? = null
        var input: BufferedInputStream? = null

        try {
            val url = URL(urlString)
            urlConnection = url.openConnection() as HttpURLConnection
            input = BufferedInputStream(urlConnection.inputStream, IO_BUFFER_SIZE)
            out = BufferedOutputStream(outputStream, IO_BUFFER_SIZE)

            var b: Int = input.read()
            while (b != -1) {
                out.write(b)
                b = input.read()
            }
            return true
        } catch (e: IOException) {
            Log.e(TAG, "Error in downloadBitmap - $e")
        } finally {
            urlConnection?.disconnect()
            try {
                out?.close()
                input?.close()
            } catch (e: IOException) {
            }

        }
        return false
    }

    companion object {
        private val TAG = "ImageFetcher"
        private val HTTP_CACHE_SIZE = 10 * 1024 * 1024 // 10MB
        private val HTTP_CACHE_DIR = "http"
        private val IO_BUFFER_SIZE = 8 * 1024
        private val DISK_CACHE_INDEX = 0
    }
}
