package com.example.souvenir

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.view.View
import com.google.android.gms.common.SignInButton

class MainActivity : AppCompatActivity() {
    private lateinit var googleLogin: GoogleLogin

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v: View, insets: WindowInsetsCompat ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        googleLogin = GoogleLogin(
            activity = this,
            onSuccess = ::onLoginSuccess,
        )

        val signInButton = findViewById<SignInButton>(R.id.sign_in_button)
        signInButton.setOnClickListener { googleLogin.signIn() }
    }

    private fun onLoginSuccess() {
        // 로그인 성공 시 MapActivity로 전환
        val intent = Intent(this, LocalActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun onLoginFailure(exception: Exception) {
        // 로그인 실패 시 처리
        exception.printStackTrace()
        // 실패 시 사용자에게 메시지를 보여주는 로직을 추가할 수 있습니다.
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        googleLogin.handleActivityResult(requestCode, resultCode, data)
    }
}