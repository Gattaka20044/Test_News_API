package com.bignerdranch.android.test2

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import java.util.concurrent.ConcurrentHashMap

private const val TAG = "ThumbnailDownloader"
private const val MESSAGE_DOWNLOAD = 0

class ThumbnailDownloader<in T>(private val responseHandler: Handler, private val onThumbnailDownloader: (T, Bitmap) -> Unit)
    : HandlerThread(TAG) {

    val fragmentLifecycleObserver: LifecycleObserver = object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        fun setup() {
            start()
            looper
        }
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun tearDown() {
            quit()
        }
    }

    val viewLifecycleObserver: LifecycleObserver = object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun clearQueue() {
            requestHandler.removeMessages(MESSAGE_DOWNLOAD)
            requestMap.clear()
        }
    }

    private var hasQuit = false
    private lateinit var requestHandler: Handler
    private val requestMap = ConcurrentHashMap<T, String> ()
    private val newsFetch = NewsFetch()

    override fun quit(): Boolean {
        hasQuit = true
        return super.quit()
    }

    fun queueThumbnail(target: T, url: String) {
        requestMap[target] = url
        requestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget()

    }

    @Suppress("UNCHECKED_CAST")
    @SuppressLint("HandlerLeak")
    override fun onLooperPrepared() {
        requestHandler = object : Handler() {
            override fun handleMessage(msg: Message) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    val target = msg.obj as T
                    handleRequest(target)
                }
            }
        }
    }

    private fun handleRequest(target: T) {
        val url = requestMap[target] ?: return
        val bitmap = newsFetch.fetchPhoto(url) ?: return

        responseHandler.post(Runnable {
            if (requestMap[target] != url || hasQuit) {
                return@Runnable
            }
            requestMap.remove(target)
            onThumbnailDownloader(target, bitmap)
        })

    }
}