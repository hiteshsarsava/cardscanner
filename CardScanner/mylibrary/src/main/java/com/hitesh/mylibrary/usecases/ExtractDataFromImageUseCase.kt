package com.hitesh.mylibrary.usecases

import android.media.Image
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognizer
import com.hitesh.mylibrary.model.CardDetails
import com.hitesh.mylibrary.utils.Extractor
import com.hitesh.mylibrary.utils.await

internal class ExtractDataFromImageUseCase(private val textRecognizer: TextRecognizer) {

    suspend operator fun invoke(image: Image, rotationDegrees: Int): CardDetails? {

        val imageInput = InputImage.fromMediaImage(image, rotationDegrees)
        val text = textRecognizer.process(imageInput).await().text
        return Extractor.extractData(text)
    }

}