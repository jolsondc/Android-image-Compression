package android.print;

import android.content.Context;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.webkit.WebView;

import java.io.File;

public class PdfConverter implements Runnable {

    private PdfConverter(){}
    private Context mContext = null;
    private WebView mwebView= null;
    private File mPdfFile = null;
    private PrintAttributes defaultPrintAttrs = null;
    private PrintAttributes pdfPrintAttrs = null;
   // get() = if (field != null) field else defaultPrintAttrs
    private Boolean mIsCurrentlyConverting = false;
    private OnComplete mOnComplete = null;
    ParcelFileDescriptor parcelFileDescriptor=null;

    private static String TAG = "PdfConverter";
    private static PdfConverter sInstance = null;


    public static synchronized PdfConverter getInstance() {
        if (sInstance == null)
            sInstance = new PdfConverter();

        return sInstance;
    }

    public interface OnComplete {
        void onWriteComplete();
        void onWriteFailed();
    }

    private PrintAttributes getPdfPrintAttrs() {
        if(pdfPrintAttrs!=null){
        }else{
            pdfPrintAttrs = getDefaultPrintAttrs();
        }
        return pdfPrintAttrs;
    }

    private ParcelFileDescriptor getParcelFileDescriptor() {
        try {
            mPdfFile.createNewFile();
            return ParcelFileDescriptor.open(mPdfFile, ParcelFileDescriptor.MODE_TRUNCATE | ParcelFileDescriptor.MODE_READ_WRITE);
        } catch ( Exception e) {
            Log.d(TAG, "Failed to open ParcelFileDescriptor", e);
        }

        return null;
    }

    private PrintAttributes getDefaultPrintAttrs() {
        return new PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setResolution(new PrintAttributes.Resolution("RESOLUTION_ID", "RESOLUTION_ID", 600, 600))
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                .build();
    }

    @Override
    public void run() {
        final PrintDocumentAdapter documentAdapter =mwebView.createPrintDocumentAdapter("doc");

        documentAdapter.onLayout(null, getPdfPrintAttrs(), null,new PrintDocumentAdapter.LayoutResultCallback() {
            @Override
            public void onLayoutFinished(PrintDocumentInfo info, boolean changed) {
                super.onLayoutFinished(info, changed);
                documentAdapter.onWrite(new PageRange[]{PageRange.ALL_PAGES}, getParcelFileDescriptor(), null, new PrintDocumentAdapter.WriteResultCallback() {
                    @Override
                    public void onWriteFinished(PageRange[] pages) {
                        super.onWriteFinished(pages);
                        mOnComplete.onWriteComplete();
                        destroy();
                    }

                    @Override
                    public void onWriteFailed(CharSequence error) {
                        super.onWriteFailed(error);
                        mOnComplete.onWriteFailed();

                    }

                    @Override
                    public void onWriteCancelled() {
                        super.onWriteCancelled();
                    }
                });
            }
        },null);
    }
    private void runOnUiThread(Runnable runnable) {
        Handler handler =new Handler(mContext.getMainLooper());
        handler.post(runnable);
    }

    public void convert(Context context,WebView htmlString,File file,OnComplete onComplete) throws Exception {
        if (context == null)
            throw new Exception("context can't be null");
        if (htmlString == null)
            throw new Exception("htmlString can't be null");
        if (file == null)
            throw new Exception("file can't be null");

        if (mIsCurrentlyConverting)
            return;

        mContext = context;
        mwebView = htmlString;
        mPdfFile = file;
        mIsCurrentlyConverting = true;
        mOnComplete = onComplete;
        runOnUiThread(this);
    }

    private void destroy() {
        mContext = null;
        mwebView = null;
        mPdfFile = null;
        pdfPrintAttrs = null;
        mIsCurrentlyConverting = false;
        mwebView = null;
        mOnComplete = null;
    }
}
