package com.fueians.medicationapp.model.repository

import android.content.Context
import com.fueians.medicationapp.model.dao.CaregiverPatientDao
import com.fueians.medicationapp.model.dao.UserDao
import com.fueians.medicationapp.model.dao.MedicationDao
import com.fueians.medicationapp.model.entities.UserEntity
import com.fueians.medicationapp.model.entities.MedicationEntity
import com.fueians.medicationapp.model.entities.CaregiverPatientEntity
import com.fueians.medicationapp.model.database.AppDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CaregiverRepository(context: Context) {

    private val caregiverPatientDao: CaregiverPatientDao by lazy {
        AppDatabase.getInstance(context).caregiverPatientDao()
    }
    private val userDao: UserDao by lazy {
        AppDatabase.getInstance(context).userDao()
    }
    private val medicationDao: MedicationDao by lazy {
        AppDatabase.getInstance(context).medicationDao()
    }

    /**
     * Get all patients for caregiver
     */
    fun getPatientsForCaregiver(caregiverId: String): Flow<List<CaregiverPatientEntity>> {
        return caregiverPatientDao.getPatientsByCaregiver(caregiverId)
    }

    /**
     * Get patient details
     */
    suspend fun getPatientById(patientId: String): UserEntity? = withContext(Dispatchers.IO) {
        userDao.getUserById(patientId)
    }

    /**
     * Add patient relationship
     */
    suspend fun addPatient(relationship: CaregiverPatientEntity) = withContext(Dispatchers.IO) {
        caregiverPatientDao.insertRelationship(relationship)
    }

    /**
     * Remove patient from caregiver
     */
    suspend fun removePatient(relationshipId: String) = withContext(Dispatchers.IO) {
        val relationship = caregiverPatientDao.getRelationshipById(relationshipId)
        if (relationship != null) {
            caregiverPatientDao.deleteRelationship(relationship)
        }
    }

    /**
     * Get patient medications
     */
    fun getPatientMedications(patientId: String): Flow<List<MedicationEntity>> {
        return medicationDao.getAllMedications(patientId)
    }

    /**
     * Update relationship permissions
     */
    suspend fun updatePermissions(relationship: CaregiverPatientEntity) = withContext(Dispatchers.IO) {
        caregiverPatientDao.updateRelationship(relationship)
    }

    /**
     * Accept caregiver invitation
     */
    suspend fun acceptInvitation(relationshipId: String) = withContext(Dispatchers.IO) {
        val relationship = caregiverPatientDao.getRelationshipById(relationshipId)
        if (relationship != null && relationship.isPending()) {
            val updated = relationship.copy(
                invitationStatus = "ACCEPTED",
                acceptedAt = java.time.Instant.now()
            )
            caregiverPatientDao.updateRelationship(updated)
        }
    }

    /**
     * Decline caregiver invitation
     */
    suspend fun declineInvitation(relationshipId: String) = withContext(Dispatchers.IO) {
        val relationship = caregiverPatientDao.getRelationshipById(relationshipId)
        if (relationship != null && relationship.isPending()) {
            val updated = relationship.copy(invitationStatus = "DECLINED")
            caregiverPatientDao.updateRelationship(updated)
        }
    }

    /**
     * Get active relationships
     */
    fun getActiveRelationships(caregiverId: String): Flow<List<CaregiverPatientEntity>> {
        return caregiverPatientDao.getActiveRelationships(caregiverId)
    }

    /**
     * Get pending invitations
     */
    fun getPendingInvitations(patientId: String): Flow<List<CaregiverPatientEntity>> {
        return caregiverPatientDao.getPendingInvitationsByPatient(patientId)
    }
}