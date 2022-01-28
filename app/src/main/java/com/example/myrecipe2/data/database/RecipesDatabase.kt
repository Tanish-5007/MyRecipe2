package com.example.myrecipe2.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myrecipe2.data.database.entities.FoodJokeEntity
import com.example.myrecipe2.data.database.entities.RecipesEntity


@Database(
    entities = [RecipesEntity::class, FoodJokeEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(RecipesTypeConverter::class)
abstract class RecipesDatabase: RoomDatabase() {
    abstract fun recipesDao(): RecipesDao
}