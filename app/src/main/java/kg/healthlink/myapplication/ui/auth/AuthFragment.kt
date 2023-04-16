package kg.healthlink.myapplication.ui.auth

import android.util.Patterns
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kg.healthlink.myapplication.R
import kg.healthlink.myapplication.databinding.FragmentAuthBinding
import kg.healthlink.myapplication.extensions.toast
import kg.healthlink.myapplication.ui.base.BaseFragment
import kg.healthlink.myapplication.utils.KeyboardHelper

class AuthFragment : BaseFragment<FragmentAuthBinding>(FragmentAuthBinding::inflate) {

    private lateinit var auth: FirebaseAuth

    override fun initView() {
        super.initView()
        auth = Firebase.auth
        signIn()

        vb.tvSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_authFragment_to_signUpFragment)
        }
    }

    private fun signIn() = with(vb) {
        btnSignIn.setOnClickListener {
            progressBar.visibility= VISIBLE
            KeyboardHelper.hideKeyboard(activity)
            if (etEmail.text.toString().isNotEmpty()
                && Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString()).matches()
                && etPassword.text.toString().trim().length >= 8
            ) {
                signInWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString())
            } else {
                progressBar.visibility= GONE
                tvYouHaventSignedYet.visibility = GONE
                etEmail.error = "Неправильная почта"
                etPassword.error = "Пароль должен содержать минимум 8 символов"
            }
        }
    }

    private fun signInWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    vb.progressBar.visibility= GONE
                    findNavController().navigateUp()
                } else {
                    // If sign in fails, display a message to the user.
                    vb.tvYouHaventSignedYet.visibility = VISIBLE
                    vb.progressBar.visibility= GONE
                    KeyboardHelper.hideKeyboard(activity)
                    toast("Не удалось войти.")
                }
            }
    }
}