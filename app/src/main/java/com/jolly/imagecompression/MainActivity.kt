package com.jolly.imagecompression

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
    val REQUEST_GALLERY_IMAGE = 1
    var actualImage: File? =null
    var compressedImage:File? = null
    private var tx_size:TextView? = null
    private var tx_path:TextView? = null
    private var imageView :ImageView?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        tx_path = findViewById(R.id.tx_path)
        tx_size = findViewById(R.id.tx_size)
        imageView= findViewById(R.id.imageView)
        fab.setOnClickListener {
            launchGalleryIntent()
        }
    }

    private fun launchGalleryIntent() {
        val pickPhoto = Intent(
            Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(pickPhoto, REQUEST_GALLERY_IMAGE)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQUEST_GALLERY_IMAGE ->{
                if (resultCode == RESULT_OK) {
                    //val imageUri = data!!.data
                    actualImage = FileUtil.from(this, data!!.data)
                    if(actualImage!=null){
                        compressedImage = Compressor(this)
                            .setMaxWidth(640f)
                            .setMaxHeight(480f)
                            .setQuality(75)
                            .setCompressFormat(Bitmap.CompressFormat.WEBP)
                            .setDestinationDirectoryPath(
                                Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES
                                ).absolutePath
                            )
                            .compressToFile(actualImage)

                        setImageNow()
                    }
                }
            }

        }
    }

    private fun setImageNow() {
        imageView?.setImageBitmap(BitmapFactory.decodeFile(compressedImage!!.absolutePath))
        tx_size?.text = String.format("Size : %s", getReadableFileSize(compressedImage!!.length()))
        tx_path?.text = compressedImage!!.path
        Toast.makeText(this, "Compressed image save in " + compressedImage!!.path, Toast.LENGTH_LONG).show()
        Log.d("Compressor", "Compressed image save in " + compressedImage!!.path)
    }


    fun getReadableFileSize(size: Long): String {
        if (size <= 0) {
            return "0"
        }
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
        return DecimalFormat("#,##0.#").format(
            size / Math.pow(
                1024.0,
                digitGroups.toDouble()
            )
        ) + " " + units[digitGroups]
    }
}
