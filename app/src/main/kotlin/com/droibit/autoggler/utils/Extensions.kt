@file:JvmName("Extensions")
package com.droibit.autoggler.utils

import android.content.Context
import android.content.Intent

inline fun <reified T: Context> intent(context: Context) = Intent(context, T::class.java)