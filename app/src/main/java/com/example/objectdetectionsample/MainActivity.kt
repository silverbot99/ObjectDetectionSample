
package com.example.objectdetectionsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.objects.FirebaseVisionObjectDetectorOptions
import com.otaliastudios.cameraview.frame.Frame
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cameraView.setLifecycleOwner(this)
        cameraView.addFrameProcessor {
            extractDataFromFrame(it) { result ->
                tvDetectedItem.text = result
            }
        }
    }

    private fun extractDataFromFrame(frame: Frame, callback: (String) -> Unit) {
        Log.e("TAG","In function extractDataFromFrame")
        val options = FirebaseVisionObjectDetectorOptions.Builder()
            .setDetectorMode(FirebaseVisionObjectDetectorOptions.STREAM_MODE)
            .enableMultipleObjects()  //Add this if you want to detect multiple objects at once
            .enableClassification()  // Add this if you want to classify the detected objects into categories
            .build()
        Log.e("TAG","options: ${options.zzns()}")

        val objectDetector = FirebaseVision.getInstance().getOnDeviceObjectDetector(options)
        Log.e("TAG","objectDetector: $objectDetector")

        objectDetector.processImage(getVisionImageFromFrame(frame))
            .addOnSuccessListener {
                Log.e("TAG","objectDetector.addOnSuccessListener")

                it.forEach { item ->
                    Log.e("TAG",item.entityId)
                }
                callback("Success")
            }
            .addOnFailureListener {
                callback("Unable to detect an object")
            }
    }

    private fun getVisionImageFromFrame(frame : Frame) : FirebaseVisionImage{
        Log.e("TAG","in function getVisionImageFromFrame")

        //ByteArray for the captured frame
        val data = frame.getData<ByteArray>()

        //Metadata that gives more information on the image that is to be converted to FirebaseVisionImage
        val imageMetaData = FirebaseVisionImageMetadata.Builder()
            .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
            .setRotation(FirebaseVisionImageMetadata.ROTATION_90)
            .setHeight(frame.size.height)
            .setWidth(frame.size.width)
            .build()

        val image = FirebaseVisionImage.fromByteArray(data, imageMetaData)

        return image
    }

}