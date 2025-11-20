package com.fueians.medicationapp.di

import android.content.Context
import androidx.room.Room
// Import all necessary DAOs, Repositories, Services, and Clients
import com.fueians.medicationapp.model.dao.*
import com.fueians.medicationapp.model.database.AppDatabase
import com.fueians.medicationapp.model.repository.*
import com.fueians.medicationapp.model.remote.* // ⚠️ Import Remote Clients
import com.fueians.medicationapp.model.services.AuthService
import com.fueians.medicationapp.security.*
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

// -------------------------------------------------------------------------
// 1. DATABASE MODULE (Unchanged)
// -------------------------------------------------------------------------

val databaseModule = module {
    single { provideDatabase(androidContext()) }
    // Provide all 8 DAOs (get<AppDatabase>()....)
    single { get<AppDatabase>().userDao() }
    single { get<AppDatabase>().medicationDao() }
    single { get<AppDatabase>().drugInfoDao() }
    single { get<AppDatabase>().caregiverPatientDao() }
    single { get<AppDatabase>().adherenceLogDao() }
    single { get<AppDatabase>().refillDao() }
    single { get<AppDatabase>().reportDao() }
    single { get<AppDatabase>().medicationScheduleDao() }
}

private fun provideDatabase(context: Context): AppDatabase {
    return Room.databaseBuilder(context, AppDatabase::class.java, "medication_db").build()
}

// -------------------------------------------------------------------------
// 2. REMOTE MODULE (NEW 🌐)
// -------------------------------------------------------------------------

val remoteModule = module {
    // These should be replaced by real implementations (e.g., Retrofit/Supabase client libraries)
    // For now, these interfaces are satisfied with mock implementations or simple placeholder classes.
    single<SupabaseClient> { SupabaseClientImpl() } // Assume SupabaseClientImpl() exists
    single<DrugAPIClient> { DrugAPIClientImpl() }   // Assume DrugAPIClientImpl() exists
}

// -------------------------------------------------------------------------
// 3. SECURITY MODULE (Unchanged)
// -------------------------------------------------------------------------

val securityModule = module {
    single<PasswordHasher> { PasswordHasherImpl() }
    single<TokenManager> { TokenManagerImpl(androidContext()) }
}

// -------------------------------------------------------------------------
// 4. REPOSITORY & SERVICE MODULE (Updated to inject Remote Clients)
// -------------------------------------------------------------------------

val repositoryModule = module {
    // All Repositories now require one or both remote clients (get())

    single { UserRepository(get()) }
    single { MedicationRepository(get(), get()) }
    single { DrugInfoRepository(get(), get()) }
    single { CaregiverRepository(get(), get()) }
    single { AdherenceLogRepository(get(), get()) }
    single { RefillRepository(get(), get()) }
    single { ReportRepository(get(), get()) }
    single { MedicationScheduleRepository(get(), get()) }

    // AuthService now requires the SupabaseClient
    single { AuthService(get(), get(), get(), get()) }
}

// List of all modules to be loaded by Koin
val appModules = listOf(databaseModule, remoteModule, securityModule, repositoryModule)