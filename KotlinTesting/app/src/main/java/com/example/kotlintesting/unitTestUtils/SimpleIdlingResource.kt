package com.example.kotlintesting.unitTestUtils

import androidx.test.espresso.IdlingResource
import androidx.test.espresso.IdlingResource.ResourceCallback
import java.util.concurrent.atomic.AtomicBoolean

class SimpleIdlingResource : IdlingResource {
    @Volatile
    private var mCallback: ResourceCallback? = null
    // Idleness is controlled with this boolean.
    private val mIsIdleNow =
        AtomicBoolean(true)

    override fun getName(): String {
        return this.javaClass.name
    }

    override fun isIdleNow(): Boolean {
        return mIsIdleNow.get()
    }

    override fun registerIdleTransitionCallback(callback: ResourceCallback) {
        mCallback = callback
    }

    fun setIdleState(isIdleNow: Boolean) {
        mIsIdleNow.set(isIdleNow)
        if (isIdleNow && mCallback != null) {
            mCallback!!.onTransitionToIdle()
        }
    }
}