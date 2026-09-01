package com.sharethework.data.model

import java.util.Date

enum class UserRole { STUDENT, MODERATOR, ADMIN }
enum class AccountStatus { ACTIVE, SUSPENDED, PENDING }
enum class SubmissionStatus { PENDING, COMPLETED, OVERDUE }

data class User(
    val id: String,
    val name: String,
    val email: String,
    val className: String, // e.g. "8"
    val section: String, // e.g. "A"
    val profilePictureUrl: String? = null,
    val role: UserRole = UserRole.STUDENT,
    val joinDate: String,
    val status: AccountStatus = AccountStatus.ACTIVE
)

data class Homework(
    val id: String,
    val subject: String,
    val title: String,
    val description: String,
    val className: String,
    val section: String,
    val dateAssigned: String,
    val dueDate: String,
    val teacherName: String,
    val teacherId: String,
    val attachments: List<Attachment> = emptyList(),
    val submissionStatus: SubmissionStatus = SubmissionStatus.PENDING
)

data class Classwork(
    val id: String,
    val subject: String,
    val title: String,
    val description: String,
    val className: String,
    val section: String,
    val date: String,
    val teacherName: String,
    val attachments: List<Attachment> = emptyList()
)

data class Attachment(
    val id: String,
    val fileName: String,
    val fileUrl: String,
    val fileType: String, // pdf, jpg, png
    val fileSizeBytes: Long
)

data class Announcement(
    val id: String,
    val title: String,
    val description: String,
    val date: String,
    val author: String,
    val targetClass: String? = null,
    val targetSection: String? = null,
    val isImportant: Boolean = false,
    val attachmentUrl: String? = null
)

data class Conversation(
    val id: String,
    val name: String,
    val isGroup: Boolean,
    val isCommunity: Boolean = false,
    val avatarUrl: String? = null,
    val lastMessage: String? = null,
    val lastMessageTime: String? = null,
    val unreadCount: Int = 0,
    val online: Boolean = false
)

data class Message(
    val id: String,
    val conversationId: String,
    val senderId: String,
    val senderName: String,
    val text: String,
    val timestamp: String,
    val isMe: Boolean,
    val isRead: Boolean = false,
    val replyToId: String? = null,
    val isDeleted: Boolean = false
)

data class AiChat(
    val id: String,
    val title: String,
    val createdAt: String,
    val preview: String
)

data class AiMessage(
    val id: String,
    val role: String, // user | assistant
    val content: String,
    val timestamp: String
)

data class Report(
    val id: String,
    val messageId: String,
    val reportedBy: String,
    val reason: String,
    val status: String, // pending, reviewed, actioned
    val createdAt: String
)

data class NotificationItem(
    val id: String,
    val title: String,
    val body: String,
    val type: String, // homework, classwork, announcement, message
    val createdAt: String,
    val isRead: Boolean = false
)
