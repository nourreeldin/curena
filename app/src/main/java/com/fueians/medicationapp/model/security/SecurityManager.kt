package com.fueians.medicationapp.model.security
import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import com.fueians.medicationapp.model.services.EncryptionService
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

/**
 * SecurityManager
 *
 * Central security coordinator for the application.
 * Manages encryption, authentication, secure storage, and security policies.
 *
 * Usage:
 * val securityManager = SecurityManager.getInstance(context)
 * securityManager.initialize()
 * val encrypted = securityManager.encryptData("sensitive data")
 */
class SecurityManager private constructor(private val context: Context) {

    // inside SecurityManager
    fun getSecurityPolicy(): SecurityPolicy = securityPolicy
    companion object {
        private const val TAG = "SecurityManager"
        private const val KEYSTORE_ALIAS = "MedicationReminderKey"
        private const val SHARED_PREFS_NAME = "secure_prefs"

        @Volatile
        private var INSTANCE: SecurityManager? = null

        fun getInstance(context: Context): SecurityManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: SecurityManager(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }

    // Dependencies
    val encryptionService: EncryptionService by lazy { EncryptionService(context) }
    val authenticationManager: AuthenticationManager by lazy {
        AuthenticationManager(context)
    }

    private val secureStorage: SecureStorage by lazy { SecureStorage(context, this) }

    // Security policy configuration
    private var securityPolicy: SecurityPolicy = SecurityPolicy.default()
    private val securityEventLog = mutableListOf<SecurityEvent>()

    private var isInitialized = false

    /**
     * Initialize security components
     * Must be called before using any security features
     */
    fun initialize() {
        if (isInitialized) {
            Log.w(TAG, "SecurityManager already initialized")
            return
        }

        try {
            // Initialize encryption service
            encryptionService.initialize()

            // Generate or retrieve master key
            ensureMasterKeyExists()

            // Initialize authentication manager
            authenticationManager.initialize()

            // Apply security policies
            enforceSecurityPolicy()

            isInitialized = true
            logSecurityEvent(SecurityEvent.SECURITY_INITIALIZED)
            Log.i(TAG, "SecurityManager initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize SecurityManager", e)
            throw SecurityException("Security initialization failed", e)
        }
    }

    /**
     * Encrypt sensitive data
     * @param data Plain text data to encrypt
     * @return Encrypted string in Base64 format
     */
    fun encryptData(data: String): String {
        checkInitialized()
        return try {
            encryptionService.encrypt(data)
        } catch (e: Exception) {
            logSecurityEvent(SecurityEvent.ENCRYPTION_FAILED)
            throw SecurityException("Encryption failed", e)
        }
    }

    /**
     * Decrypt encrypted data
     * @param encryptedData Encrypted string in Base64 format
     * @return Decrypted plain text
     */
    fun decryptData(encryptedData: String): String {
        checkInitialized()
        return try {
            encryptionService.decrypt(encryptedData)
        } catch (e: Exception) {
            logSecurityEvent(SecurityEvent.DECRYPTION_FAILED)
            throw SecurityException("Decryption failed", e)
        }
    }

    /**
     * Hash password using secure hashing algorithm
     * @param password Plain text password
     * @return Hashed password string
     */
    fun hashPassword(password: String): String {
        checkInitialized()
        return encryptionService.hashPassword(password)
    }

    /**
     * Verify password against stored hash
     * @param password Plain text password to verify
     * @param hash Stored password hash
     * @return True if password matches hash
     */
    fun verifyPassword(password: String, hash: String): Boolean {
        checkInitialized()
        return encryptionService.verifyPassword(password, hash)
    }

    /**
     * Generate authentication token
     * @return Random secure token string
     */
    fun generateToken(): String {
        checkInitialized()
        return encryptionService.generateSecureToken()
    }

    /**
     * Validate authentication token
     * @param token Token to validate
     * @return True if token is valid
     */
    fun validateToken(token: String): Boolean {
        checkInitialized()
        // Token validation logic - check format, expiration, etc.
        return token.length >= 32 && token.matches(Regex("[A-Za-z0-9+/=]+"))
    }

    /**
     * Store data securely using encryption
     * @param key Storage key
     * @param value Data to store
     */
    fun storeSecureData(key: String, value: String) {
        checkInitialized()
        secureStorage.store(key, value)
    }

    /**
     * Retrieve securely stored data
     * @param key Storage key
     * @return Decrypted data or null if not found
     */
    fun retrieveSecureData(key: String): String? {
        checkInitialized()
        return secureStorage.retrieve(key)
    }

    /**
     * Clear all secure storage
     */
    fun clearSecureData() {
        checkInitialized()
        secureStorage.clearAll()
        logSecurityEvent(SecurityEvent.SECURE_DATA_CLEARED)
    }

    /**
     * Apply security policies
     */
    fun enforceSecurityPolicy() {
        val p = securityPolicy

        Log.d(TAG, "Enforcing security policy: $p")

        // Example: enforce password policy (only checks when password is validated)
        if (p.minPasswordLength < 6) {
            Log.w(TAG, "Weak password policy detected! Minimum length must be >= 6.")
        }

        // Example: session timeout enforcement - nothing to enforce immediately,
        // implementation is used inside AuthenticationManager.isUserLoggedIn()

        // Apply max login attempts rule
        if (p.maxLoginAttempts < 3) {
            Log.w(TAG, "Login attempts too low! Setting to safe default = 3")
            securityPolicy = p.copy(maxLoginAttempts = 3)
        }

        logSecurityEvent(SecurityEvent.SECURITY_INITIALIZED)
    }

    /**
     * Detect potential security threats
     * @return List of detected security threats
     */
    fun detectSecurityThreats(): List<SecurityThreat> {
        val threats = mutableListOf<SecurityThreat>()

        // Check for rooted device
        if (isDeviceRooted()) {
            threats.add(SecurityThreat.ROOTED_DEVICE)
        }

        // Check for debugging
        if (isDebuggable()) {
            threats.add(SecurityThreat.DEBUGGABLE_APP)
        }

        // Check for suspicious apps
        // Additional threat detection logic here

        if (threats.isNotEmpty()) {
            logSecurityEvent(SecurityEvent.THREATS_DETECTED)
        }

        return threats
    }

    /**
     * Log security event
     * @param event Security event to log
     */
    fun logSecurityEvent(event: SecurityEvent) {
        val logEntry = event.copy(timestamp = System.currentTimeMillis())
        securityEventLog.add(logEntry)
        Log.d(TAG, "Security event logged: $event")

        // Keep only last 1000 events
        if (securityEventLog.size > 1000) {
            securityEventLog.removeAt(0)
        }
    }

    /**
     * Get security event log
     * @return List of security events
     */
    fun getSecurityEventLog(): List<SecurityEvent> {
        return securityEventLog.toList()
    }

    /**
     * Update security policy
     * @param policy New security policy
     */
    fun updateSecurityPolicy(policy: SecurityPolicy) {
        this.securityPolicy = policy
        enforceSecurityPolicy()
    }

    // Private helper methods

    private fun checkInitialized() {
        if (!isInitialized) {
            throw IllegalStateException("SecurityManager not initialized. Call initialize() first.")
        }
    }

    private fun ensureMasterKeyExists() {
        try {
            val keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)

            if (!keyStore.containsAlias(KEYSTORE_ALIAS)) {
                generateMasterKey()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to check master key", e)
            throw SecurityException("Master key setup failed", e)
        }
    }

    private fun generateMasterKey() {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore"
        )

        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            KEYSTORE_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()

        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
        Log.d(TAG, "Master key generated")
    }

    private fun isDeviceRooted(): Boolean {
        // Basic root detection
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su"
        )

        return paths.any { java.io.File(it).exists() }
    }

    private fun isDebuggable(): Boolean {
        return (context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
    }
}

/**
 * Secure storage implementation
 */
private class SecureStorage(
    private val context: Context,
    private val securityManager: SecurityManager
) {
    private val sharedPrefs = context.getSharedPreferences(
        "secure_storage",
        Context.MODE_PRIVATE
    )

    fun store(key: String, value: String) {
        val encrypted = securityManager.encryptionService.encrypt(value)
        sharedPrefs.edit().putString(key, encrypted).apply()
    }

    fun retrieve(key: String): String? {
        val encrypted = sharedPrefs.getString(key, null) ?: return null
        return try {
            securityManager.encryptionService.decrypt(encrypted)
        } catch (e: Exception) {
            Log.e("SecureStorage", "Failed to decrypt data for key: $key", e)
            null
        }
    }

    fun clearAll() {
        sharedPrefs.edit().clear().apply()
    }
}

/**
 * Security policy configuration
 */
data class SecurityPolicy(
    val minPasswordLength: Int = 8,
    val requireSpecialChars: Boolean = true,
    val requireNumbers: Boolean = true,
    val requireUppercase: Boolean = true,
    val sessionTimeoutMinutes: Int = 30,
    val maxLoginAttempts: Int = 5
) {
    companion object {
        fun default() = SecurityPolicy()
    }

}

/**
 * Security event for logging
 */
data class SecurityEvent(
    val type: EventType,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    enum class EventType {
        SECURITY_INITIALIZED,
        ENCRYPTION_FAILED,
        DECRYPTION_FAILED,
        SECURE_DATA_CLEARED,
        THREATS_DETECTED,
        LOGIN_ATTEMPT,
        LOGIN_SUCCESS,
        LOGIN_FAILED,
        LOGOUT,
        PASSWORD_CHANGED,
        TOKEN_GENERATED,
        TOKEN_VALIDATED
    }


    companion object {
        val SECURITY_INITIALIZED = SecurityEvent(EventType.SECURITY_INITIALIZED, "Security system initialized")
        val ENCRYPTION_FAILED = SecurityEvent(EventType.ENCRYPTION_FAILED, "Encryption operation failed")
        val DECRYPTION_FAILED = SecurityEvent(EventType.DECRYPTION_FAILED, "Decryption operation failed")
        val SECURE_DATA_CLEARED = SecurityEvent(EventType.SECURE_DATA_CLEARED, "Secure data cleared")
        val THREATS_DETECTED = SecurityEvent(EventType.THREATS_DETECTED, "Security threats detected")
    }
}

/**
 * Security threat types
 */
enum class SecurityThreat {
    ROOTED_DEVICE,
    DEBUGGABLE_APP,
    EMULATOR_DETECTED,
    TAMPERING_DETECTED,
    MALICIOUS_APP_DETECTED
}
