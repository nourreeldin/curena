package com.fueians.medicationapp.model.services
/**
 * AuthService manages all authentication-related operations such as login, signup, and session handling.
 *
 * Notes for developers:
 * - Acts as a bridge between the repository layer and the security layer (PasswordHasher, TokenManager, etc.).
 * - Interacts with other services related to Supabase and local SQLite to verify user credentials and manage authentication states.
 * - Ensure secure handling of API keys and tokens — never expose them in logs or UI.
 */
class AuthService {
    // TODO: Implement authentication logic
}