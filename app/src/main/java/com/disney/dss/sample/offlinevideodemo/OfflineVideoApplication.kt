package com.disney.dss.sample.offlinevideodemo

import android.app.Application
import android.content.Context
import android.support.v4.app.Fragment
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.offline.DownloaderConstructorHelper
import com.google.android.exoplayer2.source.hls.offline.HlsDownloadAction
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import java.io.File

class OfflineVideoApplication : Application() {
    val cache: Cache by lazy { SimpleCache(File(cacheDir, "downloads"), NoOpCacheEvictor()) }

    val downloadManager: DownloadManager by lazy {
        DownloadManager(
                DownloaderConstructorHelper(cache, DefaultDataSourceFactory(this, "OfflineVideo")),
                1,
                10,
                File(cacheDir, "saveFile"),
                HlsDownloadAction.DESERIALIZER
        )
    }

}

val Fragment.offlineVideoApplication get() = requireContext().applicationContext as OfflineVideoApplication
val Context.offlineVideoApplication get() = applicationContext as OfflineVideoApplication
