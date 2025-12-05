package com.fueians.medicationapp.model.clients
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest

class SupabaseClient(
    private val supabaseUrl: String = "https://sxhochciihjujmyuasbf.supabase.co",
    private val supabaseKey: String = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InN4aG9jaGNpaWhqdWpteXVhc2JmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjEyMzc3MTMsImV4cCI6MjA3NjgxMzcxM30.ylxVjXyl2cP1SgGjTR8h8O3RBTIVcLIxe56z4P0RwZg"
) {
    val supabase by lazy {
        createSupabaseClient(
            supabaseUrl = supabaseUrl,
            supabaseKey = supabaseKey
        ) {
            install(Auth)
            install(Postgrest)
        }
    }

    val authClient get() = supabase.auth
    val databaseClient get() = supabase.postgrest
}