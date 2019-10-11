package android.print

import android.content.Context
import android.os.Handler
import android.os.ParcelFileDescriptor
import android.util.Log
import android.webkit.WebView
import java.io.File

internal class PdfConverter private constructor() : Runnable {

    private var mContext: Context? = null
    private var mwebView: WebView? = null
    private var mPdfFile: File? = null
    private var pdfPrintAttrs: PrintAttributes? = null
        get() = if (field != null) field else defaultPrintAttrs
    private var mIsCurrentlyConverting: Boolean = false
    private var mOnComplete: OnComplete? = null

    private val outputFileDescriptor: ParcelFileDescriptor?
        get() {
            try {
                mPdfFile!!.createNewFile()
                return ParcelFileDescriptor.open(mPdfFile, ParcelFileDescriptor.MODE_TRUNCATE or ParcelFileDescriptor.MODE_READ_WRITE)
            } catch (e: Exception) {
                Log.d(TAG, "Failed to open ParcelFileDescriptor", e)
            }

            return null
        }

    private val defaultPrintAttrs: PrintAttributes?
        get() = PrintAttributes.Builder()
            .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
            .setResolution(PrintAttributes.Resolution("RESOLUTION_ID", "RESOLUTION_ID", 600, 600))
            .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
            .build()

    override fun run() {


                    val documentAdapter =mwebView!!.createPrintDocumentAdapter("doc")

                    documentAdapter.onLayout(null, pdfPrintAttrs, null, object : PrintDocumentAdapter.LayoutResultCallback() {
                        override fun onLayoutFinished(info: PrintDocumentInfo?, changed: Boolean) {
                            super.onLayoutFinished(info, changed)

                            documentAdapter.onWrite(arrayOf(PageRange.ALL_PAGES), outputFileDescriptor, null, object : PrintDocumentAdapter.WriteResultCallback() {
                                override fun onWriteFinished(pages: Array<PageRange>) {
                                    mOnComplete?.onWriteComplete()
                                    destroy()
                                }

                                override fun onWriteFailed(error: CharSequence?) {
                                    super.onWriteFailed(error)
                                    mOnComplete?.onWriteFailed()
                                }
                            })

                        }
                    }, null)



       // mWebView!!.loadDataWithBaseURL("", mwebView, "text/html", "UTF-8", null)
    }

    @Throws(Exception::class)
    fun convert(context: Context?, htmlString: WebView?, file: File?, onComplete: OnComplete?) {
        if (context == null)
            throw Exception("context can't be null")
        if (htmlString == null)
            throw Exception("htmlString can't be null")
        if (file == null)
            throw Exception("file can't be null")

        if (mIsCurrentlyConverting)
            return

        mContext = context
        mwebView = htmlString
        mPdfFile = file
        mIsCurrentlyConverting = true
        mOnComplete = onComplete
        runOnUiThread(this)
    }

    private fun runOnUiThread(runnable: Runnable) {
        val handler = Handler(mContext!!.mainLooper)
        handler.post(runnable)
    }

    private fun destroy() {
        mContext = null
        mwebView = null
        mPdfFile = null
        pdfPrintAttrs = null
        mIsCurrentlyConverting = false
        mwebView = null
        mOnComplete = null
    }

    companion object {

        private const val TAG = "PdfConverter"
        private var sInstance: PdfConverter? = null

        val instance: PdfConverter
            @Synchronized get() {
                if (sInstance == null)
                    sInstance = PdfConverter()

                return sInstance!!
            }

        interface OnComplete {
            fun onWriteComplete()
            fun onWriteFailed()
        }
    }
}