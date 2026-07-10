package com.aljunaidservices.app.data.network

import retrofit2.Response
import retrofit2.http.*

data class DashboardResponse(val success: Boolean, val source: String, val data: DashboardData)
data class DashboardData(val settings: SettingsModel, val services: List<ServiceModel>, val portfolio: List<PortfolioModel>, val reviews: List<ReviewModel>)
data class SettingsModel(val whatsapp_number: String, val contact_email: String, val office_address: String, val working_hours: String, val stats_completed_projects: String, val stats_satisfied_clients: String, val stats_years_experience: String, val stats_academic_consultants: String, val facebook_link: String, val twitter_link: String)
data class ServiceModel(val id: String, val title: String, val description: String, val features: String, val price: String, val duration: String, val media: String)
data class PortfolioModel(val id: String, val title: String, val description: String, val media: String, val type: String, val features: String, val price: String, val duration: String)
data class ReviewModel(val id: String?, val rowIndex: Int?, val name: String, val comment: String, val status: String)

data class AddReviewRequest(val action: String = "addReview", val name: String, val comment: String, val status: String = "Pending")
data class GenericResponse(val success: Boolean, val message: String)

interface SheetsApiService {
    @GET("api/sheets-data")
    suspend fun getDashboardData(): Response<DashboardResponse>

    @POST("api/submit-review")
    suspend fun submitReview(@Body request: AddReviewRequest): Response<GenericResponse>
}