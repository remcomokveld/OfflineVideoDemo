package com.disney.dss.sample.offlinevideodemo

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.offline.DownloadManager.*
import com.google.android.exoplayer2.upstream.cache.Cache
import com.uber.autodispose.android.lifecycle.scope
import com.uber.autodispose.autoDisposable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.download_fragment.*
import java.util.concurrent.TimeUnit

@SuppressLint("SetTextI18n")
class DownloadFragment : Fragment() {

    private val downloadManager: DownloadManager get() = offlineVideoApplication.downloadManager
    private val cache: Cache get() = offlineVideoApplication.cache

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.download_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val presenter = DownloadPresenter(requireContext())
        startDownload.setOnClickListener { presenter.startDownload() }
        deleteVideo.setOnClickListener { presenter.deleteVideo() }
    }

    override fun onStart() {
        super.onStart()
        Observable.interval(0, 200, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .autoDisposable(scope())
                .subscribe {
                    status.text = downloadManager.currentStateDescription + "\n\nCache size: ${cache.cacheSpace.fileSize}"
                }
    }

    private val DownloadManager.currentStateDescription
        get() = if (allTaskStates.isNotEmpty()) allTaskStates.joinToString(
                "\n\n",
                prefix = "Active tasks:\n\n"
        ) { it.asString } else "No active tasks"

    private val DownloadManager.TaskState.asString
        get() = "Task $taskId:\nType %s\nState: %s\nPercentage: %.2f".format(
                if (action.isRemoveAction) "Remove" else "Download",
                TaskState.getStateString(state),
                downloadPercentage
        )


    private val Long.fileSize get() = Formatter.formatFileSize(context, this)
}


