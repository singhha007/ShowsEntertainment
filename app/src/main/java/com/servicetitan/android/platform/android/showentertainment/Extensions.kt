package com.servicetitan.android.platform.android.showentertainment

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

fun <T> Flow<T>.handleErrors(tag: String): Flow<T> =
    catch { Log.d(tag, it.localizedMessage ?: "Error Occurred") }