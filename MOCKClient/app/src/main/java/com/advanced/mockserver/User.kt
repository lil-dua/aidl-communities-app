package com.advanced.mockserver

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

/***
 * Created by HoangRyan aka LilDua on 10/27/2023.
 */
@Parcelize
data class User(
    val id: Long = 0,
    val name: String,
    @DrawableRes val image: Int
): Parcelable