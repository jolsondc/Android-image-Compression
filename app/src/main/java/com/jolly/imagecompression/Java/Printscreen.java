package com.jolly.imagecompression.Java;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.jolly.imagecompression.R;
import com.jolly.imagecompression.html2pdf.Html2Pdf;
import com.jolly.imagecompression.models.InvoiceModel;

import java.io.File;
import java.util.ArrayList;

public class Printscreen extends AppCompatActivity implements Html2Pdf.OnCompleteConversion {

    private Intent mShareIntent = null;
    private File pdfFile= null;
    String imagePathStr=null;
    private int quant = 20;
    //private ArrayList<ContentData> list = new ArrayList<>();
    private InvoiceModel invoiceModel = null;
   // private val randomColors = arrayOf("#FF0000", "#008000", "#0000FF")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.print_layout);

        for (int i = 0; i < 10; i++) {
            InvoiceModel invoiceModel = new InvoiceModel();

            InvoiceModel.ContentData contentData = new InvoiceModel().new ContentData("","","");
        }


    }




    @Override
    public void onSuccess() {

    }

    @Override
    public void onFailed() {

    }
}
