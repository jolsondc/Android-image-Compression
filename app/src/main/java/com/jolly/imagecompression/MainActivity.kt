package com.jolly.imagecompression

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider.getUriForFile
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
    private var fileName: String?=null

    private val REQUEST_GALLERY_IMAGE = 1
    private val REQUEST_CAMERA_IMAGE = 2
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
            showImagePickerOptions(this@MainActivity)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            REQUEST_CAMERA_IMAGE->{
                if (resultCode == RESULT_OK) {
                    actualImage = FileUtil.from(this, fileName?.let { getCacheImagePath(it) })
                    compressImageAndSave()
                }
            }
            REQUEST_GALLERY_IMAGE ->{
                if (resultCode == RESULT_OK) {
                    //val imageUri = data!!.data
                    actualImage = FileUtil.from(this, data!!.data)
                    compressImageAndSave()
                }
            }

        }
    }

    private fun compressImageAndSave() {
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


    fun showImagePickerOptions(context: Context) {
        // setup the alert builder
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Select option")

        // add a list
        val optionList = arrayOf("Select for camera","Select from gallery"
        )
        builder.setItems(optionList) { dialog, which ->
            when (which) {
                0,1 -> onAction(which)
            }
        }

        // create and show the alert dialog
        val dialog = builder.create()
        dialog.show()
    }

    private fun onAction(which: Int) {
        when(which){
            0->{
                launchCameraIntent()
            }
            1->{
                launchGalleryIntent()
            }
        }
    }

    private fun launchCameraIntent() {
        fileName = System.currentTimeMillis().toString() + ".jpg"
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getCacheImagePath(fileName!!))
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_CAMERA_IMAGE)
        }
    }

    private fun getCacheImagePath(fileName: String): Uri {
        val path = File(externalCacheDir, "camera")
        if (!path.exists()) path.mkdirs()
        val image = File(path, fileName)
        return getUriForFile(this@MainActivity, "$packageName.provider", image)
    }
    private fun launchGalleryIntent() {
        val pickPhoto = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(pickPhoto, REQUEST_GALLERY_IMAGE)
    }
}
