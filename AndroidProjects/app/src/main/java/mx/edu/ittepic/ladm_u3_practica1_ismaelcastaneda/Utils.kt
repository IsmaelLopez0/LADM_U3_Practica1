package mx.edu.ittepic.ladm_u3_practica1_ismaelcastaneda

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

object Utils{

    fun getBytes(bitmap: Bitmap): ByteArray{
        var stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream)
        return stream.toByteArray()
    }

    fun getImage(image: ByteArray): Bitmap{
        return BitmapFactory.decodeByteArray(image, 0, image.size)
    }

}