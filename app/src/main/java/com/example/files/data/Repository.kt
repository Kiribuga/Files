package com.example.files.data

import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.files.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.sql.Timestamp
import java.time.Instant
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class Repository {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun downloadFile(link: String, context: Context): String {
        return suspendCoroutine { continuation ->
            CoroutineScope(Dispatchers.IO).launch {
                if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) return@launch
                val sharedPrefs = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
                if (sharedPrefs.getBoolean("firstRun", true)) {
                    //context.resources.assets.open("links.txt").bufferedReader()
                    //Скачивание определенных файлов при первом запуске
                    sharedPrefs.edit()
                        .putBoolean("firstRun", false)
                        .apply()
                }
                val folder = context.getExternalFilesDir("testFolder")
                val file = File(
                    folder,
                    "${Timestamp.from(Instant.now()).time}_${link.substringAfterLast("/")}"
                )
                try {
                    if (sharedPrefs.contains(link)) {
                        continuation.resume(context.getString(R.string.file_already_download))
                    } else {
                        file.outputStream().use { outputStream ->
                            Network.api
                                .getFile(link)
                                .byteStream()
                                .use { inputStream ->
                                    inputStream.copyTo(outputStream)
                                }
                        }
                        continuation.resume(context.getString(R.string.download_successfuly))
                        sharedPrefs.edit()
                            .putString(link, file.name)
                            .commit()
                    }
                } catch (t: Throwable) {
                    Log.d("Repository", "Error download", t)
                    continuation.resumeWithException(t)
                    file.delete()
                }
            }
        }
    }
    companion object {
        const val SHARED_PREFS = "skillbox_shared_prefs"
    }
}