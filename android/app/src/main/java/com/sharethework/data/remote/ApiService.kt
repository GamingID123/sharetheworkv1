package com.sharethework.data.remote

import com.sharethework.data.model.*
import retrofit2.http.*
import okhttp3.MultipartBody
import okhttp3.RequestBody

data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val name: String, val email: String, val password: String, val className: String, val section: String)
data class AuthResponse(val token: String, val user: User)
data class HomeworkRequest(val subject: String, val title: String, val description: String, val className: String, val section: String, val dueDate: String)
data class AiChatRequest(val message: String, val conversationId: String? = null)

interface ApiService {
    @POST("auth/login") suspend fun login(@Body req: LoginRequest): AuthResponse
    @POST("auth/register") suspend fun register(@Body req: RegisterRequest): AuthResponse
    @POST("auth/reset-password") suspend fun resetPassword(@Body body: Map<String, String>): Map<String, String>
    @GET("auth/me") suspend fun me(): User

    @GET("homework") suspend fun getHomework(@Query("class") className: String?, @Query("section") section: String?, @Query("subject") subject: String?, @Query("q") q: String?): List<Homework>
    @POST("homework") suspend fun createHomework(@Body req: HomeworkRequest): Homework
    @PUT("homework/{id}") suspend fun updateHomework(@Path("id") id: String, @Body req: HomeworkRequest): Homework
    @DELETE("homework/{id}") suspend fun deleteHomework(@Path("id") id: String): Map<String, String>
    @POST("homework/{id}/complete") suspend fun markComplete(@Path("id") id: String): Map<String, String>

    @GET("classwork") suspend fun getClasswork(@Query("class") c: String?, @Query("section") s: String?, @Query("q") q: String?): List<Classwork>
    @POST("classwork") suspend fun createClasswork(@Body req: HomeworkRequest): Classwork

    @GET("announcements") suspend fun getAnnouncements(): List<Announcement>
    @POST("announcements") suspend fun createAnnouncement(@Body a: Announcement): Announcement

    @GET("conversations") suspend fun getConversations(): List<Conversation>
    @GET("conversations/{id}/messages") suspend fun getMessages(@Path("id") id: String, @Query("page") page: Int = 1): List<Message>
    @POST("conversations/{id}/messages") suspend fun sendMessage(@Path("id") id: String, @Body body: Map<String, String>): Message
    @DELETE("messages/{id}") suspend fun deleteMessage(@Path("id") id: String): Map<String, String>
    @POST("messages/{id}/report") suspend fun reportMessage(@Path("id") id: String, @Body body: Map<String, String>): Map<String, String>

    @GET("ai/conversations") suspend fun getAiConversations(): List<AiChat>
    @POST("ai/chat") suspend fun aiChat(@Body req: AiChatRequest): Map<String, String>
    @GET("ai/conversations/{id}") suspend fun getAiMessages(@Path("id") id: String): List<AiMessage>

    @GET("notifications") suspend fun getNotifications(): List<NotificationItem>
    @GET("admin/stats") suspend fun getStats(): Map<String, Int>
    @GET("admin/users") suspend fun getUsers(@Query("q") q: String?): List<User>
    @PUT("admin/users/{id}/role") suspend fun updateRole(@Path("id") id:String, @Body body: Map<String,String>): User
    @PUT("admin/users/{id}/status") suspend fun updateStatus(@Path("id") id:String, @Body body: Map<String,String>): User
    @GET("admin/reports") suspend fun getReports(): List<Report>

    // Firebase Storage (messages & other data + files) - primary is /storage, /drive kept as alias
    @Multipart @POST("storage/upload") suspend fun uploadStorageFile(@Part file: okhttp3.MultipartBody.Part, @Part("folder") folder: okhttp3.RequestBody): Map<String, String>
    @Multipart @POST("drive/upload") suspend fun uploadDriveFile(@Part file: okhttp3.MultipartBody.Part, @Part("folder") folder: okhttp3.RequestBody): Map<String, String>
    @GET("storage/files/{id}") suspend fun getStorageFile(@Path("id") id: String): Map<String, String>
    @GET("drive/files/{id}") suspend fun getDriveFile(@Path("id") id: String): Map<String, String>
    @POST("storage/backup") suspend fun backupToStorage(@Body body: Map<String, Any>): Map<String, String>
    @POST("drive/backup") suspend fun backupToDrive(@Body body: Map<String, Any>): Map<String, String>
}
