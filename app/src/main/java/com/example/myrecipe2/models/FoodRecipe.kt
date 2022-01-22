package com.example.myrecipe2.models


import com.google.gson.annotations.SerializedName

data class FoodRecipe(

    @SerializedName("results")
    val results: List<Result>,

    )