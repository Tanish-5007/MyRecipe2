package com.example.myrecipe2.data

import com.example.myrecipe2.data.network.FoodRecipesApi
import com.example.myrecipe2.models.FoodJoke
import com.example.myrecipe2.models.FoodRecipe
import retrofit2.Response
import javax.inject.Inject


class RemoteDataSource @Inject constructor(
    private val foodRecipesApi: FoodRecipesApi
){

    suspend fun getRecipes(queries: Map<String, String>): Response<FoodRecipe>{
        return foodRecipesApi.getRecipes(queries)
    }

    suspend fun getFoodJoke(query: String): Response<FoodJoke>{
        return foodRecipesApi.getFoodJoke(query)
    }

}