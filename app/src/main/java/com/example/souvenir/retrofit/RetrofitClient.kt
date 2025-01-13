package com.example.souvenir.retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.security.cert.X509Certificate

// 데이터 클래스 정의
data class UserDto(
    val uid: String,
    val email: String,
    val displayName: String
)

// Retrofit 인터페이스 정의
interface HouseService {
    @GET("api/houses") // 실제 엔드포인트에 맞게 수정
    fun getHouseList(): Call<HouseDto>

    @POST("api/saveUser") // 사용자 데이터를 저장하는 엔드포인트
    fun saveUser(@Body user: UserDto): Call<Void>

    @GET("api/blogs") // 실제 엔드포인트에 맞게 수정
    fun getBlogs(): Call<BlogResponse> // 반환 타입 수정
}

// OkHttpClient의 SSL 설정을 위해 모든 인증서를 신뢰하도록 설정
fun getUnsafeOkHttpClient(): OkHttpClient {
    return try {
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())

        OkHttpClient.Builder()
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { hostname, session -> true } // 모든 호스트 이름 신뢰
            .build()
    } catch (e: Exception) {
        throw RuntimeException(e)
    }
}

// Retrofit 설정
object RetrofitClient {
    // 실제 BASE_URL 주소를 입력해주세요
    private const val BASE_URL = ""

    val houseService: HouseService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(getUnsafeOkHttpClient()) // OkHttpClient 설정 추가
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HouseService::class.java)
    }
}
