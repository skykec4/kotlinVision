package com.example.kotlinvision

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_analyze_view.*
import java.io.File

class MainActivity : AppCompatActivity() {


    private val CAMERA_PERMISSION_REQUEST = 100
    private val GALLERY_PERMISSION_REQUEST = 1001
    private val FILE_NAME = "picture.jpg"
    private var uploadChooser: UploadChooser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupListener()
    }


    private fun setupListener() {
        upload_image.setOnClickListener {
            uploadChooser = UploadChooser().apply {
                this.addNotifier(object : UploadChooser.UploadChooseNotifierInterface {
                    override fun cameraOnClick() {

                        Log.d("upload", "camera Click")
                        checkCameraPermission()
                    }

                    override fun galleryOnClick() {
                        Log.d("upload", "gallery Click")
                        checkGalleryPermission()
                    }
                })
            }

            uploadChooser!!.show(supportFragmentManager, "")
        }
    }

    private fun checkCameraPermission() {

        if (PermissionUtil().requestPermission(
                this,
                CAMERA_PERMISSION_REQUEST,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            openCamera()
        }


    }

    private fun checkGalleryPermission() {
        if (PermissionUtil().requestPermission(
                this,
                GALLERY_PERMISSION_REQUEST,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            openGallery()
        }
    }

    private fun openCamera() {
        val photoUri = FileProvider.getUriForFile(
            this,
            applicationContext.packageName + ".provider",
            createCameraFile()
        )
        startActivityForResult(Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }, CAMERA_PERMISSION_REQUEST)

    }

    private fun openGallery() {
        val intent = Intent().apply {
            setType("image/*")
            setAction(Intent.ACTION_GET_CONTENT)

        }

        startActivityForResult(
            Intent.createChooser(intent, "select a photo"),
            GALLERY_PERMISSION_REQUEST
        )

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            //카메라
            CAMERA_PERMISSION_REQUEST -> {
                if (resultCode != Activity.RESULT_OK) return
                val photoUri = FileProvider.getUriForFile(
                    this,
                    applicationContext.packageName + ".provider",
                    createCameraFile()
                )
                uploadImage(photoUri)
            }
            //갤러리
            GALLERY_PERMISSION_REQUEST -> data?.data?.let {
                uploadImage(it)
            }
        }

    }

    private fun uploadImage(imageUri: Uri) {
        //        val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
        //        uploaded_image.setImageBitmap(bitmap)

        if (Build.VERSION.SDK_INT < 28) {
            val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            uploaded_image.setImageBitmap(bitmap)
        } else {
            val source = ImageDecoder.createSource(this.contentResolver, imageUri)
            val bitmap = ImageDecoder.decodeBitmap(source)
            uploaded_image.setImageBitmap(bitmap)
        }

        uploadChooser?.dismiss()
    }

    private fun createCameraFile(): File {
        val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File(dir, FILE_NAME)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        Log.d("로그 테스트", "$requestCode , ${permissions[0]} ${grantResults[0]}")
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST -> {
                if (PermissionUtil().permissionGranted(
                        requestCode,
                        CAMERA_PERMISSION_REQUEST,
                        grantResults
                    )
                ) {
                    openCamera()
                }
            }

            GALLERY_PERMISSION_REQUEST -> {
                if (PermissionUtil().permissionGranted(
                        requestCode,
                        GALLERY_PERMISSION_REQUEST,
                        grantResults
                    )
                ) {
                    openGallery()
                }
            }
        }

    }
}