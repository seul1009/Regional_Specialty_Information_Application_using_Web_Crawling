package com.example.souvenir

import android.app.Activity
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.example.souvenir.retrofit.RetrofitClient
import com.example.souvenir.retrofit.UserDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GoogleLogin(
    private val activity: Activity,
    private val onSuccess: () -> Unit
) {
    private val googleSignInClient: GoogleSignInClient

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(activity, gso)
    }

    fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        activity.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                val uid = account.id ?: ""
                val email = account.email ?: ""
                val displayName = account.displayName ?: ""

                // 로그인 성공 시 LocalActivity로 전환
                onSuccess()

                // 만약 서버에 사용자 데이터를 저장하려면 주석을 해제하고 아래 코드로 사용
                saveUserDataToServer(uid, email, displayName)
            }
        } catch (e: ApiException) {
            e.printStackTrace()
        }
    }

    // 서버 데이터 저장 메서드
    private fun saveUserDataToServer(uid: String, email: String, displayName: String) {
        val user = UserDto(uid, email, displayName)
        RetrofitClient.houseService.saveUser(user).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    println("User data saved successfully")
                } else {
                    println("Failed to save user data: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    companion object {
        private const val RC_SIGN_IN = 9001
    }
}
