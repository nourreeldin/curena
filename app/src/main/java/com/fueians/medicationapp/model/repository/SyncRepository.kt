package com.fueians.medicationapp.model.repository

import com.fueians.medicationapp.model.dao.MedicationDao
import com.fueians.medicationapp.model.dao.ScheduleDao
import com.fueians.medicationapp.model.dao.UserDao
import com.fueians.medicationapp.model.entities.Medication
import com.fueians.medicationapp.model.entities.MedicationSchedule
import com.fueians.medicationapp.model.entities.UserEntity

/**
 * A placeholder interface for a remote API client.
 */
interface RemoteApi {
    fun pullUsers(): List<UserEntity>
    fun pullMedications(): List<Medication>
    fun pushMedications(medications: List<Medication>)
}

/**
 * SyncRepository
 *
 * Responsibility: Handle the synchronization of data between the local database and a remote server.
 * All methods in this repository perform blocking I/O and MUST be called from a background thread.
 */
class SyncRepository {

    // DAOs and services are now private attributes with placeholder implementations.
    private val userDao: UserDao = object : UserDao {
        private val users = mutableMapOf<String, UserEntity>()
        override fun getUserById(id: String) = users[id]
        override fun getUserByEmail(email: String) = users.values.find { it.email == email }
        override fun insertUser(user: UserEntity) { users[user.id] = user }
        override fun updateUser(user: UserEntity) { users[user.id] = user }
        override fun deleteUser(user: UserEntity) { users.remove(user.id) }
    }

    private val medicationDao: MedicationDao = object : MedicationDao {
        private val medications = mutableMapOf<String, Medication>()
        override fun getMedicationsForUser(userId: String) = medications.values.filter { it.userId == userId }
        override fun getMedicationById(medicationId: String) = medications[medicationId]
        override fun searchMedicationsForUser(userId: String, query: String) = emptyList<Medication>()
        override fun insertMedication(medication: Medication) { medications[medication.id] = medication }
        override fun updateMedication(medication: Medication) { medications[medication.id] = medication }
        override fun deleteMedication(medication: Medication) { medications.remove(medication.id) }
    }

    private val scheduleDao: ScheduleDao = object : ScheduleDao {
        private val schedules = mutableMapOf<String, MedicationSchedule>()
        override fun getAllSchedules() = schedules.values.toList()
        override fun insertSchedule(schedule: MedicationSchedule) { schedules[schedule.id] = schedule }
        override fun updateSchedule(schedule: MedicationSchedule) { schedules[schedule.id] = schedule }
        override fun deleteScheduleById(scheduleId: String) { schedules.remove(scheduleId) }
    }

    private val remoteApi: RemoteApi = object : RemoteApi {
        override fun pullUsers(): List<UserEntity> = emptyList()
        override fun pullMedications(): List<Medication> = emptyList()
        override fun pushMedications(medications: List<Medication>) { println("Pushing ${medications.size} medications to remote.") }
    }

    /**
     * Pulls all data from the remote server and saves it to the local database.
     * This is a blocking method and must be called from a background thread.
     */
    fun pullFromServer() {
        // Fetch data from the remote API
        val remoteUsers = remoteApi.pullUsers()
        val remoteMedications = remoteApi.pullMedications()

        // Save data to the local DAOs
        remoteUsers.forEach { userDao.updateUser(it) }
        remoteMedications.forEach { medicationDao.updateMedication(it) }

        println("Sync complete: Pulled ${remoteUsers.size} users and ${remoteMedications.size} medications.")
    }

    /**
     * Pushes all local data to the remote server.
     * This is a blocking method and must be called from a background thread.
     */
    fun pushToServer() {
        // In a real app, you would fetch all local data that needs syncing.
        val localMedications = medicationDao.getMedicationsForUser("all") // Simplified
        remoteApi.pushMedications(localMedications)
        println("Push complete: Sent ${localMedications.size} medications to server.")
    }
}
