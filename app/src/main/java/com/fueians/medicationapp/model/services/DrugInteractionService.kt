package com.fueians.medicationapp.model.services

import com.fueians.medicationapp.model.entities.MedicationEntity
import com.fueians.medicationapp.model.entities.DrugInteractionEntity
import com.fueians.medicationapp.model.entities.InteractionSeverity
import com.fueians.medicationapp.model.repository.DrugInfoRepository

class DrugInteractionService(
    private val drugInfoRepository: DrugInfoRepository
) {
    private val interactionCache = mutableMapOf<String, DrugInteractionEntity?>()

    /**
     * Check interactions for full medication list
     */
    suspend fun checkInteractions(medications: List<MedicationEntity>): List<DrugInteractionEntity> {
        val results = mutableListOf<DrugInteractionEntity>()

        for (i in medications.indices) {
            for (j in i + 1 until medications.size) {
                val drug1 = medications[i].name
                val drug2 = medications[j].name

                val interaction = checkPairInteraction(drug1, drug2)
                if (interaction != null) {
                    results.add(interaction)
                }
            }
        }

        return results.sortedByDescending { it.getPriorityScore() }
    }

    /**
     * Check interaction for two specific drugs
     */
    suspend fun checkPairInteraction(drug1: String, drug2: String): DrugInteractionEntity? {
        val key = generateKey(drug1, drug2)

        // Check cache first
        if (interactionCache.containsKey(key)) {
            return interactionCache[key]
        }

        // Query repository
        val interaction = drugInfoRepository.getInteraction(drug1, drug2)

        // Save in cache
        interactionCache[key] = interaction

        return interaction
    }

    /**
     * Get interaction severity level
     */
    fun getInteractionSeverity(interaction: DrugInteractionEntity): InteractionSeverity {
        return interaction.getSeverityLevel()
    }

    /**
     * Get interaction description
     */
    fun getInteractionDescription(interaction: DrugInteractionEntity): String {
        return interaction.description
    }

    /**
     * Check if interaction requires immediate attention
     */
    fun requiresImmediateAttention(interaction: DrugInteractionEntity): Boolean {
        return interaction.requiresImmediateAttention()
    }

    /**
     * Generate warning message for interaction
     */
    fun generateWarningMessage(interaction: DrugInteractionEntity): String {
        return interaction.getWarningMessage()
    }

    /**
     * Cache multiple interactions
     */
    fun cacheInteractions(interactions: List<DrugInteractionEntity>) {
        for (interaction in interactions) {
            val key = generateKey(interaction.drug1Name, interaction.drug2Name)
            interactionCache[key] = interaction
        }
    }

    /**
     * Clear interaction cache
     */
    fun clearCache() {
        interactionCache.clear()
    }

    /**
     * Generate normalized cache key
     */
    private fun generateKey(drug1: String, drug2: String): String {
        return if (drug1 < drug2) {
            "${drug1.lowercase()}|${drug2.lowercase()}"
        } else {
            "${drug2.lowercase()}|${drug1.lowercase()}"
        }
    }
}