@file:JvmName("Extensions")
package com.droibit.autoggler.utils

import android.content.Context
import android.content.Intent
import android.support.annotation.StringRes
import android.widget.Toast

inline fun <reified T: Context> intent(context: Context) = Intent(context, T::class.java)

fun Context.showShortToast(@StringRes resId: Int) = Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()