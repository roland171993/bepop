package com.stopgalere.presentation.ui.common

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed class UiText {
    data class DynamicString(val value: String) : UiText()

    data class Resource(@StringRes val resId: Int, val args: List<Any> = emptyList()) : UiText()

    @Composable
    fun asString(): String = when (this) {
        is DynamicString -> value
        is Resource -> stringResource(resId, *args.toTypedArray())
    }

    fun resolve(context: Context): String = when (this) {
        is DynamicString -> value
        is Resource -> context.getString(resId, *args.toTypedArray())
    }
}
