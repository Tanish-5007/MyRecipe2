package com.example.myrecipe2.data

import com.example.myrecipe2.data.database.RecipesDao
import com.example.myrecipe2.data.database.entities.FoodJokeEntity
import com.example.myrecipe2.data.database.entities.RecipesEntity
import com.example.myrecipe2.models.FoodJoke
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val recipesDao: RecipesDao
) {

    fun readDatabase(): Flow<List<RecipesEntity>>{
        return recipesDao.readRecipes()
    }

    suspend fun insertRecipes(recipesEntity: RecipesEntity){
        recipesDao.insertRecipes(recipesEntity)
    }

    fun readFoodJoke(): Flow<List<FoodJoke>>{
        return recipesDao.readFoodJoke()
    }

    suspend fun insertFoodJoke(foodJokeEntity: FoodJokeEntity){
        return recipesDao.insertFoodJoke(foodJokeEntity)
    }

}