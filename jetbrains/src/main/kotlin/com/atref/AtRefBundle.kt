package com.atref

import com.intellij.DynamicBundle
import org.jetbrains.annotations.PropertyKey

private const val BUNDLE_FQN = "messages.AtRefBundle"

object AtRefBundle : DynamicBundle(BUNDLE_FQN) {
    fun message(@PropertyKey(resourceBundle = BUNDLE_FQN) key: String, vararg params: Any): String =
        getMessage(key, *params)
}
