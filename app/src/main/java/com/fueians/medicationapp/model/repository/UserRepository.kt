package com.fueians.medicationapp.model.repository

import com.fueians.medicationapp.model.dao.UserDao
import com.fueians.medicationapp.model.entities.UserEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class UserRepository(private val userDao: UserDao) {

    private val ioScheduler = Schedulers.io()

    fun fetchUserById(id: String): Single<UserEntity> {
        return userDao.getUserById(id)
            .subscribeOn(ioScheduler)
    }

    fun fetchUserByEmail(email: String): Single<UserEntity> {
        return userDao.getUserByEmail(email)
            .subscribeOn(ioScheduler)
    }

    fun saveUser(user: UserEntity): Completable {
        return userDao.insertUser(user)
            .subscribeOn(ioScheduler)
    }

    fun deleteUser(id: String): Completable {
        return userDao.deleteUser(id)
            .subscribeOn(ioScheduler)
    }
}