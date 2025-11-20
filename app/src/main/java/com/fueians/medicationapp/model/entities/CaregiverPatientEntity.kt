package com.fueians.medicationapp.model.entities
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Relationship types between caregiver and patient
 */
enum class RelationshipType {
    FAMILY,         // Family member
    SPOUSE,         // Spouse or partner
    PARENT,         // Parent caring for child
    CHILD,          // Adult child caring for parent
    PROFESSIONAL,   // Professional caregiver or nurse
    FRIEND,         // Friend providing care
    OTHER           // Other relationship
}

/**
 * Permission levels for caregiver access
 */
enum class PermissionLevel {
    VIEW_ONLY,      // Can only view medication schedules
    STANDARD,       // Can view and receive notifications
    MANAGER,        // Can modify medications and schedules
    FULL_ACCESS     // Complete access including reports
}

/**
 * Status of caregiver invitation
 */
enum class InvitationStatus {
    PENDING,        // Invitation sent, awaiting response
    ACCEPTED,       // Invitation accepted, relationship active
    DECLINED,       // Invitation declined
    REVOKED,        // Invitation cancelled by sender
    EXPIRED         // Invitation expired
}

/**
 * CaregiverPatientEntity
 *
 * Responsibility: Represent caregiver-patient relationship data.
 *
 * This entity manages the relationships between caregivers and patients,
 * enabling family members or professional caregivers to monitor and assist
 * with medication management. It handles permissions, invitations, and
 * access control for patient data.
 *
 * Related Classes: CaregiverPatientDao, CaregiverRepository
 */
@Entity(
    tableName = "caregiver_patient",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["caregiver_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["patient_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["caregiver_id"]),
        Index(value = ["patient_id"]),
        Index(value = ["invitation_status"]),
        Index(value = ["permission_level"]),
        Index(value = ["caregiver_id", "patient_id"], unique = true)
    ]
)
data class CaregiverPatientEntity(
    /**
     * Relationship ID - Primary key
     * Unique identifier for this caregiver-patient relationship
     */
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    /**
     * Caregiver user ID - Foreign key
     * References the user who is providing care
     */
    @ColumnInfo(name = "caregiver_id")
    val caregiverId: String,

    /**
     * Patient user ID - Foreign key
     * References the user receiving care
     */
    @ColumnInfo(name = "patient_id")
    val patientId: String,

    /**
     * Type of relationship
     * Describes the nature of the caregiver-patient relationship
     * (FAMILY, SPOUSE, PARENT, CHILD, PROFESSIONAL, FRIEND, OTHER)
     */
    @ColumnInfo(name = "relationship_type")
    val relationshipType: String,

    /**
     * Access permission level
     * Defines what the caregiver can do
     * (VIEW_ONLY, STANDARD, MANAGER, FULL_ACCESS)
     */
    @ColumnInfo(name = "permission_level")
    val permissionLevel: String,

    /**
     * Invitation status
     * Current status of the caregiver invitation
     * (PENDING, ACCEPTED, DECLINED, REVOKED, EXPIRED)
     */
    @ColumnInfo(name = "invitation_status")
    val invitationStatus: String,

    /**
     * Relationship creation timestamp
     * Unix timestamp (milliseconds) when relationship was initiated
     */
    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    /**
     * Last update timestamp
     * Unix timestamp (milliseconds) of the last status or permission change
     */
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Relationships (defined in class diagram):
     * - 1 CaregiverPatientEntity references 1 UserEntity as caregiver
     * - 1 CaregiverPatientEntity references 1 UserEntity as patient
     * - 1 UserEntity can have 0..* CaregiverPatientEntity as caregiver
     * - 1 UserEntity can have 0..* CaregiverPatientEntity as patient
     *
     * Managed by:
     * - CaregiverPatientDao: Database operations (CRUD)
     * - CaregiverRepository: Relationship management logic
     * - Synced with Supabase via SupabaseClient
     * - Used by CaregiverPresenter for UI display
     */

    companion object {
        // Invitation expiry duration (7 days in milliseconds)
        const val INVITATION_EXPIRY_DURATION = 7L * 24 * 60 * 60 * 1000L
    }

    /**
     * Helper function to get relationship type as enum
     */
    fun getRelationshipTypeEnum(): RelationshipType {
        return try {
            RelationshipType.valueOf(relationshipType.uppercase())
        } catch (e: IllegalArgumentException) {
            RelationshipType.OTHER
        }
    }

    /**
     * Helper function to get permission level as enum
     */
    fun getPermissionLevelEnum(): PermissionLevel {
        return try {
            PermissionLevel.valueOf(permissionLevel.uppercase())
        } catch (e: IllegalArgumentException) {
            PermissionLevel.VIEW_ONLY
        }
    }

    /**
     * Helper function to get invitation status as enum
     */
    fun getInvitationStatusEnum(): InvitationStatus {
        return try {
            InvitationStatus.valueOf(invitationStatus.uppercase())
        } catch (e: IllegalArgumentException) {
            InvitationStatus.PENDING
        }
    }

    /**
     * Helper function to check if relationship is active
     */
    fun isActive(): Boolean {
        return getInvitationStatusEnum() == InvitationStatus.ACCEPTED
    }

    /**
     * Helper function to check if invitation is pending
     */
    fun isPending(): Boolean {
        return getInvitationStatusEnum() == InvitationStatus.PENDING
    }

    /**
     * Helper function to check if invitation has expired
     */
    fun hasExpired(): Boolean {
        if (getInvitationStatusEnum() != InvitationStatus.PENDING) return false
        val now = System.currentTimeMillis()
        return (now - createdAt) > INVITATION_EXPIRY_DURATION
    }

    /**
     * Helper function to check if caregiver can view data
     */
    fun canView(): Boolean {
        return isActive()
    }

    /**
     * Helper function to check if caregiver can modify data
     */
    fun canModify(): Boolean {
        if (!isActive()) return false
        val permission = getPermissionLevelEnum()
        return permission == PermissionLevel.MANAGER ||
                permission == PermissionLevel.FULL_ACCESS
    }

    /**
     * Helper function to check if caregiver has full access
     */
    fun hasFullAccess(): Boolean {
        return isActive() && getPermissionLevelEnum() == PermissionLevel.FULL_ACCESS
    }

    /**
     * Helper function to check if caregiver can receive notifications
     */
    fun canReceiveNotifications(): Boolean {
        if (!isActive()) return false
        val permission = getPermissionLevelEnum()
        return permission != PermissionLevel.VIEW_ONLY
    }

    /**
     * Helper function to check if caregiver can generate reports
     */
    fun canGenerateReports(): Boolean {
        if (!isActive()) return false
        val permission = getPermissionLevelEnum()
        return permission == PermissionLevel.MANAGER ||
                permission == PermissionLevel.FULL_ACCESS
    }

    /**
     * Helper function to get user-friendly relationship description
     */
    fun getRelationshipDescription(): String {
        return when (getRelationshipTypeEnum()) {
            RelationshipType.FAMILY -> "Family Member"
            RelationshipType.SPOUSE -> "Spouse/Partner"
            RelationshipType.PARENT -> "Parent"
            RelationshipType.CHILD -> "Child"
            RelationshipType.PROFESSIONAL -> "Professional Caregiver"
            RelationshipType.FRIEND -> "Friend"
            RelationshipType.OTHER -> "Other"
        }
    }

    /**
     * Helper function to get user-friendly permission description
     */
    fun getPermissionDescription(): String {
        return when (getPermissionLevelEnum()) {
            PermissionLevel.VIEW_ONLY -> "View Only"
            PermissionLevel.STANDARD -> "View & Notifications"
            PermissionLevel.MANAGER -> "Manage Medications"
            PermissionLevel.FULL_ACCESS -> "Full Access"
        }
    }

    /**
     * Helper function to get status badge text
     */
    fun getStatusBadge(): String {
        return when (getInvitationStatusEnum()) {
            InvitationStatus.PENDING -> "Pending"
            InvitationStatus.ACCEPTED -> "Active"
            InvitationStatus.DECLINED -> "Declined"
            InvitationStatus.REVOKED -> "Revoked"
            InvitationStatus.EXPIRED -> "Expired"
        }
    }

    /**
     * Helper function to get status color for UI
     */
    fun getStatusColor(): String {
        return when (getInvitationStatusEnum()) {
            InvitationStatus.PENDING -> "#FFA726"    // Orange
            InvitationStatus.ACCEPTED -> "#66BB6A"   // Green
            InvitationStatus.DECLINED -> "#EF5350"   // Red
            InvitationStatus.REVOKED -> "#9E9E9E"    // Grey
            InvitationStatus.EXPIRED -> "#BDBDBD"    // Light Grey
        }
    }

    /**
     * Helper function to get days remaining until invitation expires
     */
    fun getDaysUntilExpiry(): Long? {
        if (getInvitationStatusEnum() != InvitationStatus.PENDING) return null
        val expiryTime = createdAt + INVITATION_EXPIRY_DURATION
        val timeRemaining = expiryTime - System.currentTimeMillis()
        return if (timeRemaining > 0) timeRemaining / (24 * 60 * 60 * 1000L) else 0
    }
}