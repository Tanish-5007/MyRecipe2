package com.example.myrecipe2.viewmodels


import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.*
import com.example.myrecipe2.data.Repository
import com.example.myrecipe2.data.database.RecipesEntity
import com.example.myrecipe2.models.FoodJoke
import com.example.myrecipe2.models.FoodRecipe
import com.example.myrecipe2.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
): AndroidViewModel(application) {

    /** ROOM DATABASE*/

    val readRecipes: LiveData<List<RecipesEntity>> = repository.local.readDatabase().asLiveData()

    private fun insertRecipes(recipesEntity: RecipesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertRecipes(recipesEntity)
        }




    /** RETROFIT */


    var recipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()

    var foodJokeResponse: MutableLiveData<NetworkResult<FoodJoke>> = MutableLiveData()

    fun getRecipes(queries: Map<String,String>) = viewModelScope.launch {
        getRecipeSafeCall(queries)
    }

    fun getFoodJoke(apiKey: String) = viewModelScope.launch {
        getFoodJokeSafeCall(apiKey)
    }

    private suspend fun getFoodJokeSafeCall(apiKey: String) {
        foodJokeResponse.value = NetworkResult.Loading()
        if(hasInternetConnection()){
            try {
                val response = repository.remote.getFoodJoke(apiKey)
                foodJokeResponse.value = handleFoodJokeResponse(response)
            } catch (e: Exception){
                foodJokeResponse.value = NetworkResult.Error("")
            }
        }
        else{
            foodJokeResponse.value = NetworkResult.Error("No Internet Connection")
        }
    }

    private suspend fun getRecipeSafeCall(queries: Map<String, String>) {
        recipesResponse.value = NetworkResult.Loading()
        if(hasInternetConnection()){
            try {
                val response = repository.remote.getRecipes(queries)
                recipesResponse.value = handleFoodRecipesResponse(response)

                val foodRecipe = recipesResponse.value!!.data
                if(foodRecipe != null){
                    offlineCacheRecipe(foodRecipe)
                }

            }catch (e: Exception){
                recipesResponse.value = NetworkResult.Error("Recipes Not Found.")
            }
        }else{
            recipesResponse.value = NetworkResult.Error(message = "No Internet Connection.")
        }
    }

    private fun offlineCacheRecipe(foodRecipe: FoodRecipe) {
        val recipesEntity = RecipesEntity(foodRecipe)
        insertRecipes(recipesEntity)
    }

    private fun handleFoodRecipesResponse(response: Response<FoodRecipe>): NetworkResult<FoodRecipe> {
        when{
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                return NetworkResult.Error("API Key Limited.")
            }
            response.body()!!.results.isNullOrEmpty() -> {
                return NetworkResult.Error("Recipe Not Found")
            }
            response.isSuccessful -> {
                val foodRecipes = response.body()
                return NetworkResult.Success(foodRecipes!!)
            }
            else -> {
                return NetworkResult.Error(response.message())
            }
        }
    }

    private fun handleFoodJokeResponse(response: Response<FoodJoke>): NetworkResult<FoodJoke> {
        return when{
            response.message().toString().contains("timeout") -> {
                NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                NetworkResult.Error("API Key Limited.")
            }
            response.isSuccessful -> {
                val foodJoke = response.body()
                NetworkResult.Success(foodJoke!!)
            }
            else -> {
                NetworkResult.Error(response.message())
            }
        }
    }

    private fun hasInternetConnection(): Boolean{
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when{
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }


}