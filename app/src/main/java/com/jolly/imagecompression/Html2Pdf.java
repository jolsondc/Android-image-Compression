package com.jolly.imagecompression;

import android.content.Context;
import android.print.PdfConverter;
import android.webkit.WebView;

import java.io.File;

public class Html2Pdf {
    Context context;
    WebView html;
    File file;
    public Html2Pdf(Context context, WebView html, File file){
        this.context=context;
        this.file=file;
        this.html=html;
    }

    void convertToPdf(final OnCompleteConversion onCompleteConversion) throws Exception {
        PdfConverter.getInstance().convert(context, html, file, new PdfConverter.OnComplete() {
             public void onWriteComplete() {
                onCompleteConversion.onSuccess();
            }

            public void onWriteFailed() {
                onCompleteConversion.onFailed();
            }

        });
    }


    public void convertToPdf() throws Exception {
        PdfConverter.getInstance().convert(context, html, file, null);
    }

    interface OnCompleteConversion {

        void onSuccess();

        void onFailed();

    }
    static class Builder {

        private Context context;
        private WebView html;
        private File file;

        public Builder context(Context context) {
            this.context = context;
            return this;
        }

        public Builder html(WebView html)  {
            this.html = html;
            return this;
        }

        public Builder file(File file) {
            this.file = file;
            return this;
        }

        public Html2Pdf build() {
            return new Html2Pdf(context, html, file);
        }

    }

}
