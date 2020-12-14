package com.example.kotlinvision

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.upload_chooser.*

class UploadChooser : BottomSheetDialogFragment() {

    interface UploadChooseNotifierInterface {
        fun cameraOnClick()
        fun galleryOnClick()
    }

    var uploadChooseNotifierInterface: UploadChooseNotifierInterface? = null

    fun addNotifier(listener: UploadChooseNotifierInterface) {
        uploadChooseNotifierInterface = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.upload_chooser, container, false)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupListener()
    }

    private fun setupListener() {
        upload_camera.setOnClickListener {
            uploadChooseNotifierInterface?.cameraOnClick()
        }
        upload_gallery.setOnClickListener {
            uploadChooseNotifierInterface?.galleryOnClick()
        }
    }
}