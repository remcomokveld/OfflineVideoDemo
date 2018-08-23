package com.disney.dss.sample.offlinevideodemo

import android.content.Context
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.offline.DownloadHelper
import com.google.android.exoplayer2.offline.TrackKey
import com.google.android.exoplayer2.source.hls.offline.HlsDownloadHelper
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import java.io.IOException

class DownloadPresenter(val context: Context) {

    private val downloadHelper = HlsDownloadHelper(
            Constants.MANIFEST_URI,
            DefaultDataSourceFactory(context, "OfflineVideo")
    )

    fun startDownload() {
        downloadHelper.prepare(object : DownloadHelper.Callback {
            override fun onPrepared(helper: DownloadHelper) {
                VideoDownloadService.startWithAction(
                        context,
                        helper.getDownloadAction(null, selectSmallestFormat(downloadHelper))
                )
            }

            private fun selectSmallestFormat(helper: DownloadHelper): List<TrackKey> {
                val trackKeys = mutableListOf<TrackKey>()
                for (periodIndex in 0 until helper.periodCount) {
                    val trackGroups = helper.getTrackGroups(periodIndex)
                    for (groupIndex in 0 until trackGroups.length) {
                        val trackGroup = trackGroups[groupIndex]
                        var hightestFormat: Format? = null
                        for (trackIndex in 0 until trackGroup.length) {
                            val format = trackGroup.getFormat(trackIndex)
                            if (hightestFormat == null || hightestFormat.bitrate < format.bitrate) {
                                hightestFormat = format
                            }
                        }
                        if (hightestFormat != null)
                            trackKeys.add(TrackKey(periodIndex, groupIndex, trackGroup.indexOf(hightestFormat)))
                    }
                }
                return trackKeys
            }

            override fun onPrepareError(helper: DownloadHelper, e: IOException) {
                throw e
            }

        })
    }

    fun deleteVideo() {
        VideoDownloadService.startWithAction(context, downloadHelper.getRemoveAction(null))
    }
}
