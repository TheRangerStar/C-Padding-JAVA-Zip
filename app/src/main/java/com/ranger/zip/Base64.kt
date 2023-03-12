package com.ranger.zip

import android.util.Base64
import android.util.Log
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * @author TheRangerStar
 * @date 2023/3/12 18:12
 */
object Base64 {

    fun encrypt(value: String): String? {
        try {
            val paddingValue = ZipCryptor.Encrypt(value)
            var byteArray: ByteArray? = null
            val byteArrayOutputStream = ByteArrayOutputStream()
            val zipOutputStream = ZipOutputStream(byteArrayOutputStream)
            val zipEntry = ZipEntry("zip")
            zipEntry.size = paddingValue?.size?.toLong() ?: 1024
            zipOutputStream.putNextEntry(zipEntry)
            zipOutputStream.write(paddingValue)
            zipOutputStream.closeEntry()
            zipOutputStream.close()
            byteArray = byteArrayOutputStream.toByteArray()
            byteArrayOutputStream.close()
            val resultData = Base64.encodeToString(byteArray, Base64.DEFAULT)
            return resultData
        } catch (e: Exception) {
            Log.e("encrypt: ", e.toString())
            return null
        }
    }


    fun decrypt(value: String): String? {
        try {
            val data = Base64.decode(value, Base64.DEFAULT)
            var byteArray: ByteArray? = null
            val byteArrayOutputStream = ByteArrayInputStream(data)
            val zipInputStream = ZipInputStream(byteArrayOutputStream)
            while (zipInputStream.nextEntry != null) {
                val buf = ByteArray(data.size + 1024)
                var num = -1
                val byte = ByteArrayOutputStream()
                while (zipInputStream.read(buf, 0, buf.size) != -1) {
                    byte.write(buf, 0, num)
                }
                byteArray = byte.toByteArray()
                byte.flush()
                byte.close()
            }
            zipInputStream.close()
            byteArrayOutputStream.close()
            return byteArray?.let { ZipCryptor.Decrypt(it) }
        } catch (e: Exception) {
            Log.e("decrypt: ", e.toString())
            return null
        }

    }

    fun unZip(data: ByteArray?): ByteArray? {
        var b: ByteArray? = null
        try {
            val bis = ByteArrayInputStream(data)
            val zip = ZipInputStream(bis)
            while (zip.getNextEntry() != null) {
                val buf = ByteArray(1024)
                var num = -1
                val baos = ByteArrayOutputStream()
                while (zip.read(buf, 0, buf.size).also { num = it } != -1) {
                    baos.write(buf, 0, num)
                }
                b = baos.toByteArray()
                baos.flush()
                baos.close()
            }
            zip.close()
            bis.close()
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
        return b
    }
}