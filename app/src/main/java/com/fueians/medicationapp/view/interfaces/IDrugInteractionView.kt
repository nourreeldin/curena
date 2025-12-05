package com.fueians.medicationapp.view.interfaces

import com.fueians.medicationapp.model.clients.DrugInfo
import com.fueians.medicationapp.model.entities.DrugInteractionEntity

interface IDrugInteractionView {

    // Loading indicators
    fun showLoading()
    fun hideLoading()

    // Error handling
    fun showErrorMessage(message: String)

    // Drug search results
    fun displaySearchResults(drugs: List<DrugInfo>)

    // Detailed drug info
    fun displayDrugInfo(drug: DrugInfo)

    // Interaction results
    fun displayInteractionResults(interactions: List<DrugInteractionEntity>)

    // Detailed interaction info
    fun displayInteractionDetails(interaction: DrugInteractionEntity)

    // Success callback when saving drug info locally
    fun onDrugInfoSaved()
}
