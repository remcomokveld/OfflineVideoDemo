package com.disney.dss.sample.offlinevideodemo

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.offline.DownloaderConstructorHelper
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.video_fragment.*
import java.util.concurrent.TimeUnit

class VideoFragment : Fragment() {
    lateinit var player: ExoPlayer

    private val transferListener = TotalBytesTransferListener()

    private fun createDataSourceFactory(): DataSource.Factory {
        val upstreamDataSourceFactory = DefaultDataSourceFactory(requireContext(), "OfflineVideo", transferListener)
        return if (arguments?.getBoolean(USE_CACHE) == true) {
            val helper = DownloaderConstructorHelper(offlineVideoApplication.cache, upstreamDataSourceFactory)
            DataSource.Factory { helper.buildCacheDataSource(false) }
        } else {
            upstreamDataSourceFactory
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        player = ExoPlayerFactory.newSimpleInstance(requireContext(), DefaultTrackSelector())
        val mediaSourceFactory = HlsMediaSource.Factory(createDataSourceFactory())
        player.prepare(mediaSourceFactory.createMediaSource(Constants.MANIFEST_URI))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.video_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        playerView.player = player
    }

    override fun onStart() {
        super.onStart()
        player.playWhenReady = true
        Observable.interval(0, 200, TimeUnit.MILLISECONDS)
                .map { Formatter.formatFileSize(context, transferListener.bytesTransferred) }
                .observeOn(AndroidSchedulers.mainThread())
                .autoDisposable(scope())
                .subscribe { bytesDownloaded.text = it }
    }

    override fun onStop() {
        player.playWhenReady = false
        super.onStop()
    }

    override fun onDestroyView() {
        playerView.player = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        player.release()
        super.onDestroy()
    }

    companion object {
        private const val USE_CACHE = "USE_CACHE"

        fun create(useCache: Boolean) = VideoFragment().apply {
            arguments = Bundle().apply { putBoolean(USE_CACHE, useCache) }
        }
    }
}
