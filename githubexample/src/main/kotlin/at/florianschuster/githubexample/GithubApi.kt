package at.florianschuster.githubexample

import com.google.gson.annotations.SerializedName
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface GithubApi {

    @GET("search/repositories")
    fun repos(@Query("q") query: String, @Query("page") page: Int): Single<Result>

    companion object Factory {
        private const val baseUrl = "https://api.github.com/"

        fun create(): GithubApi {
            val retrofit = Retrofit.Builder().apply {
                baseUrl(baseUrl)
                addConverterFactory(GsonConverterFactory.create())
                addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            }.build()

            return retrofit.create(GithubApi::class.java)
        }
    }
}

data class Result(val items: List<Repo>)

data class Repo(val id: Int, @SerializedName("full_name") val name: String) {
    val url: String
        get() = "https://github.com/$name"
}