package com.fueians.medicationapp.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fueians.medicationapp.model.entities.UserEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id")
    fun getUserById(id: String): Single<UserEntity>

    @Query("SELECT * FROM users WHERE email = :email")
    fun getUserByEmail(email: String): Single<UserEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: UserEntity): Completable

    @Update
    fun updateUser(user: UserEntity): Completable

    @Query("DELETE FROM users WHERE id = :id")
    fun deleteUser(id: String): Completable
}