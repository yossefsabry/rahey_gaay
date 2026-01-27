package com.raheygaay.app.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.raheygaay.app.data.local.RoomChatDataSource
import com.raheygaay.app.data.local.db.AppDatabase
import com.raheygaay.app.data.local.db.ChatDao
import com.raheygaay.app.data.local.db.PerformanceDao
import com.raheygaay.app.data.local.db.SahbyDao
import com.raheygaay.app.data.source.ChatDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS chat_drafts (" +
                    "chatId TEXT NOT NULL, " +
                    "draft TEXT NOT NULL, " +
                    "updatedAt INTEGER NOT NULL, " +
                    "PRIMARY KEY(chatId))"
            )
        }
    }

    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS sahby_messages (" +
                    "id TEXT NOT NULL, " +
                    "threadId TEXT NOT NULL DEFAULT 'default', " +
                    "role TEXT NOT NULL, " +
                    "content TEXT NOT NULL, " +
                    "createdAt INTEGER NOT NULL, " +
                    "PRIMARY KEY(id))"
            )
        }
    }

    private val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS sahby_threads (" +
                    "id TEXT NOT NULL, " +
                    "title TEXT NOT NULL, " +
                    "lastMessage TEXT, " +
                    "updatedAt INTEGER NOT NULL, " +
                    "PRIMARY KEY(id))"
            )
            val cursor = db.query("PRAGMA table_info(sahby_messages)")
            var hasThreadId = false
            val nameIndex = cursor.getColumnIndex("name")
            while (cursor.moveToNext()) {
                if (cursor.getString(nameIndex) == "threadId") {
                    hasThreadId = true
                    break
                }
            }
            cursor.close()
            if (!hasThreadId) {
                db.execSQL("ALTER TABLE sahby_messages ADD COLUMN threadId TEXT NOT NULL DEFAULT 'default'")
            }
            db.execSQL(
                "INSERT OR IGNORE INTO sahby_threads (id, title, lastMessage, updatedAt) " +
                    "VALUES ('default', 'Sahby Chat', NULL, 0)"
            )
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "rahey_gaay.db")
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
            .build()
    }

    @Provides
    fun provideChatDao(database: AppDatabase): ChatDao = database.chatDao()

    @Provides
    fun providePerformanceDao(database: AppDatabase): PerformanceDao = database.performanceDao()

    @Provides
    fun provideSahbyDao(database: AppDatabase): SahbyDao = database.sahbyDao()

    @Provides
    fun provideChatDataSource(chatDao: ChatDao): ChatDataSource = RoomChatDataSource(chatDao)
}
