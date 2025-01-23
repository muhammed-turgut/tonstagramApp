package com.muhammedturgut.tonstagram

import android.graphics.Bitmap
import com.squareup.picasso.Transformation

class SquareTransformation :Transformation {
    //Resimlerin kırpılması için kullanılıyor.
    override fun transform(source: Bitmap?): Bitmap {

        //Kaynağın null olup olmadığını kontrol ettik.
        if(source == null){
            throw IllegalArgumentException("Source Hatalı...")
        }

        val size = source.width.coerceAtMost(source.height) // Kısa kenarı seç
        val x = (source.width - size) / 2 // Yatay kırpma
        val y = (source.height - size) / 2 // Dikey kırpma

        val result = Bitmap.createBitmap(source, x, y, size, size)
        if (result != source) {
            source.recycle()
        }
        return result
    }

    override fun key(): String {
        return "square()"
    }
}