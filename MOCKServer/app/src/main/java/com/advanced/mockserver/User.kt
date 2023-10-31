package com.advanced.mockserver

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.advanced.mockserver.utils.Constants
import kotlinx.parcelize.Parcelize

/***
 * Created by HoangRyan aka LilDua on 10/27/2023.
 */
@Parcelize
@Entity(tableName = Constants.TABLE_USERS)
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    @DrawableRes val image: Int
): Parcelable