package com.comp3074.foodquest.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Quest::class, TeamMember::class], version = 2, exportSchema = true)
@TypeConverters(TagConverters::class)
abstract class FoodQuestDatabase : RoomDatabase() {
    abstract fun questDao(): QuestDao

    companion object {
        private val correctStudentId = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("UPDATE members SET studentId = ? WHERE name = ? AND studentId = ?",
                    arrayOf("101568934", "Almas Sultana", "101564373"))
            }
        }

        fun open(context: Context, name: String = "foodquest.db"): FoodQuestDatabase =
            Room.databaseBuilder(context.applicationContext, FoodQuestDatabase::class.java, name)
                .addMigrations(correctStudentId)
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        listOf(
                            arrayOf("Almas Sultana", "101568934"),
                            arrayOf("Arcadio Vega", "101569135"),
                            arrayOf("Juela Topi", "101087887")
                        ).forEach {
                            db.execSQL("INSERT INTO members(name, studentId, email, phone) VALUES(?, ?, '', '')", it)
                        }
                    }
                }).build()
    }
}
