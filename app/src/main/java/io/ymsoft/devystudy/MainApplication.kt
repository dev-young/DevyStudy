package io.ymsoft.devystudy

import android.app.Application
import timber.log.Timber
import timber.log.Timber.DebugTree

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(MyDebugTree())
    }
}

class MyDebugTree : DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String? {
        return String.format(
            "[C:%s] [L:%s] [M:%s] ",
            super.createStackElementTag(element),
            element.lineNumber,
            element.methodName
        )
    }
}