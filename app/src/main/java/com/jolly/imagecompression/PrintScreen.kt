package com.jolly.imagecompression

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.jolly.imagecompression.html2pdf.Html2Pdf
import com.jolly.imagecompression.models.ContentData
import com.jolly.imagecompression.models.InvoiceModel
import kotlinx.android.synthetic.main.print_layout.*
import java.io.File


class PrintScreen : AppCompatActivity(), Html2Pdf.OnCompleteConversion {
    private var mShareIntent: Intent? = null
    private var pdfFile: File? = null
    var imagePathStr: String? = null
    private val quant = 20
    private val list = ArrayList<ContentData>()
    private var invoiceModel: InvoiceModel? = null
    private val randomColors = arrayOf("#FF0000", "#008000", "#0000FF")

    var webview: WebView? = null
    @SuppressLint("JavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.print_layout)
        webview = findViewById(R.id.webview)
        fab.setOnClickListener {
            createPDF()
        }
        imagePathStr = intent.extras!!.getString("path")
        var file = File(imagePathStr)
        imagePathStr = "file://" + file.absolutePath

        webview!!.settings.builtInZoomControls = true
        getItemData()
        val ws = webview!!.settings
        ws.javaScriptEnabled = true
        ws.useWideViewPort = true//Adaptive resolution
        ws.loadWithOverviewMode = true

        //webview!!.loadDataWithBaseURL("", htmlJs, "text/html", "UTF-8", null)
        webview!!.loadUrl("file:///android_asset/sample.html")
        invoiceModel = getInvoiceData()
        webview!!.addJavascriptInterface(invoiceModel!!, "invoice")
        webview!!.addJavascriptInterface(this, "javatojs")

        // webview!!.addJavascriptInterface(LoadListener(), "HTMLOUT") //uncomment when need to extract htmls code from webview


        webview!!.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                 webview!!.loadUrl("javascript:changeColor()")
                webview!!.loadUrl("javascript:loadInvoice()")
                webview!!.loadUrl("javascript:callListJS()")
                //view.loadUrl("javascript:window.HTMLOUT.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');") //uncomment when need to extract htmls code from webview
            }
        }


        val pdfDirPath = File(filesDir, "pdfs")
        pdfDirPath.mkdirs()
        pdfFile = File(pdfDirPath, "pdfsend.pdf")

    }

    private fun getInvoiceData(): InvoiceModel {
        return InvoiceModel().apply {
            imagePath = imagePathStr
            color = randomColors[(Math.random() * randomColors.size).toInt()]
            total = "$ " + (quant * 100) + ""
        }
    }

    private fun getItemData() {
        for (i in 1..20) {
            val p = ContentData()
            p.name = "loonggg"
            p.quantity = "28"
            p.price = "$ 100"
            list.add(p)
        }
    }

    @SuppressLint("JavascriptInterface")
    fun changeColor(view: View) {
        invoiceModel?.apply {
            color = randomColors[(Math.random() * randomColors.size).toInt()]
        }
        webview!!.addJavascriptInterface(invoiceModel, "invoice")
        webview!!.loadUrl("javascript:changeColor()")

    }

    private fun createPDF() {
        //Impl example
        val converter = Html2Pdf.Companion.Builder()
            .context(this)
            .html(webview!!)
            .file(pdfFile!!)
            .build()

        //can be called with a callback to warn the user
        converter.convertToPdf(this)

        converter.convertToPdf()

    }

    override fun onSuccess() {
        //do your thing
        // val contentUri = file?.let { FileProvider.getUriForFile(this, "com.jolly.imagecompression", it) }
        val contentUri = FileProvider.getUriForFile(this, packageName, pdfFile!!)
        if (contentUri != null) {
            shareDocument(contentUri)
        }
    }

    override fun onFailed() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    private fun shareDocument(uri: Uri) {
        mShareIntent = Intent()
        mShareIntent!!.action = Intent.ACTION_SEND
        mShareIntent!!.type = "application/pdf"
        mShareIntent!!.putExtra(Intent.EXTRA_SUBJECT, "Here is a PDF from PdfSend")
        mShareIntent!!.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(mShareIntent)
        return
    }

    /**
     * This method will be invoked in the js script through window.javatojs.... ()
     *
     * @return
     */
    @JavascriptInterface
    fun getPersonObject(index: Int): ContentData {
        return list[index]
    }

    @JavascriptInterface
    fun getSize(): Int {
        return list.size
    }


    internal inner class LoadListener {
        @JavascriptInterface
        fun processHTML(html: String) {
            //   htmlJs=html
            //   Log.i("TAG","Html str :$htmlJs");
        }
    }
}
