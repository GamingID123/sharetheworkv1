package com.sharethework.data.local

import androidx.room.*

@Entity(tableName = "homework_cache")
data class HomeworkEntity(
    @PrimaryKey val id: String,
    val subject: String,
    val title: String,
    val description: String,
    val className: String,
    val section: String,
    val dueDate: String,
    val json: String
)

@Entity(tableName = "classwork_cache")
data class ClassworkEntity(
    @PrimaryKey val id: String,
    val title: String,
    val json: String
)

@Entity(tableName = "announcement_cache")
data class AnnouncementEntity(
    @PrimaryKey val id: String,
    val title: String,
    val json: String
)

@Dao
interface CacheDao {
    @Query("SELECT * FROM homework_cache") suspend fun getHomework(): List<HomeworkEntity>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertHomework(items: List<HomeworkEntity>)
    @Query("SELECT * FROM classwork_cache") suspend fun getClasswork(): List<ClassworkEntity>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertClasswork(items: List<ClassworkEntity>)
    @Query("SELECT * FROM announcement_cache") suspend fun getAnnouncements(): List<AnnouncementEntity>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertAnnouncements(items: List<AnnouncementEntity>)
    @Query("DELETE FROM homework_cache") suspend fun clearHomework()
}

@Database(entities = [HomeworkEntity::class, ClassworkEntity::class, AnnouncementEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cacheDao(): CacheDao
}
