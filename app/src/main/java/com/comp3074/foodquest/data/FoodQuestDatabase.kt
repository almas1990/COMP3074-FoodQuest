package com.comp3074.foodquest.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Quest::class, TeamMember::class], version = 1, exportSchema = true)
@TypeConverters(TagConverters::class)
abstract class FoodQuestDatabase : RoomDatabase() {
    abstract fun questDao(): QuestDao

    companion object {
        fun open(context: Context, name: String = "foodquest.db"): FoodQuestDatabase =
            Room.databaseBuilder(context.applicationContext, FoodQuestDatabase::class.java, name)
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        listOf(
                            arrayOf("Almas Sultana", "101564373"),
                            arrayOf("Arcadio Vega", "101569135"),
                            arrayOf("Juela Topi", "101087887")
                        ).forEach {
                            db.execSQL("INSERT INTO members(name, studentId, email, phone) VALUES(?, ?, '', '')", it)
                        }
                    }
                }).build()
    }
}
