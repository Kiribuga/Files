package com.example.files.files

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.files.data.Repository
import kotlinx.coroutines.launch

class ViewModelFiles : ViewModel() {

    private val repository = Repository()
    private val toastDownloadLiveData = MutableLiveData<String>()
    private val loaderLiveData = MutableLiveData<Boolean>()

    val toastDownload: LiveData<String>
        get() = toastDownloadLiveData

    val loader: LiveData<Boolean>
        get() = loaderLiveData

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadFile(link: String, context: Context) {
        viewModelScope.launch {
            loaderLiveData.postValue(true)
            try {
                val result = repository.downloadFile(link, context)
                toastDownloadLiveData.postValue(result)
            } catch (t: Throwable) {
                Log.d("ViewModelFiles", "Error", t)
            } finally {
                loaderLiveData.postValue(false)
            }
        }
    }
}