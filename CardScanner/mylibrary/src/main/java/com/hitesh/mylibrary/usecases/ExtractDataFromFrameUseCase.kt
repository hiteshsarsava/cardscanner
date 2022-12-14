package com.hitesh.mylibrary.usecases

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.text.TextRecognizer
import com.hitesh.mylibrary.model.CardDetails
import com.hitesh.mylibrary.utils.Extractor
import com.hitesh.mylibrary.utils.await

@ExperimentalGetImage
internal class ExtractDataFromFrameUseCase(private val textRecognizer: TextRecognizer) {

    suspend operator fun invoke(imageProxy: ImageProxy): CardDetails? {

        val frame = imageProxy.image
        val rotationDegrees = imageProxy.imageInfo.rotationDegrees

        if (frame == null) {
            imageProxy.close()
            return null
        }

        val text = textRecognizer.process(frame, rotationDegrees).await().text
        val card = Extractor.extractData(text)

        if (card == null) imageProxy.close()

        return card
    }
}