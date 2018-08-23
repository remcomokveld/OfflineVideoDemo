package com.disney.dss.sample.offlinevideodemo

import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.TransferListener

class TotalBytesTransferListener : TransferListener<DataSource> {

    private var totalBytes: Long = 0L

    val bytesTransferred get() = totalBytes

    override fun onTransferStart(source: DataSource?, dataSpec: DataSpec?) = Unit

    override fun onTransferEnd(source: DataSource?) = Unit

    override fun onBytesTransferred(source: DataSource?, bytesTransferred: Int) {
        totalBytes += bytesTransferred
    }

    fun reset() {
        totalBytes = 0
    }
}
