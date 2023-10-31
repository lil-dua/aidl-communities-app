package com.advanced.mockserver.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.advanced.mockserver.User
import com.advanced.mockserver.utils.Constants

/***
 * Created by HoangRyan aka LilDua on 10/27/2023.
 */
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)

    @Query("DELETE FROM ${Constants.TABLE_USERS} WHERE ${Constants.COLUMN_ID} = :userId")
    fun deleteUserById(userId: Long)

    @Query("SELECT * FROM ${Constants.TABLE_USERS}")
    fun getAllUsers(): LiveData<List<User>>

    @Query("SELECT * FROM ${Constants.TABLE_USERS}")
    fun getRemoteAllUsers(): List<User>

    @Query("SELECT * FROM ${Constants.TABLE_USERS} WHERE ${Constants.COLUMN_ID} = :userId")
    fun getUserById(userId: Long): LiveData<User>

}