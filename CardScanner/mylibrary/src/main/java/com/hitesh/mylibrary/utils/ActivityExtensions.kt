package com.hitesh.mylibrary.utils

import androidx.activity.ComponentActivity
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

internal fun ComponentActivity.launchWhenResumed(block: suspend CoroutineScope.() -> Unit): Job =
    lifecycle.coroutineScope.launchWhenResumed(block)
