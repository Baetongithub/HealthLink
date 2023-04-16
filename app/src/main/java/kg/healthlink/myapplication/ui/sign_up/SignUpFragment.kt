package kg.healthlink.myapplication.ui.sign_up

import android.util.Patterns
import android.view.View
import android.view.View.GONE
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kg.healthlink.myapplication.databinding.FragmentSignUpBinding
import kg.healthlink.myapplication.extensions.toast
import kg.healthlink.myapplication.ui.base.BaseFragment
import kg.healthlink.myapplication.utils.FirebaseConstants.BORN
import kg.healthlink.myapplication.utils.FirebaseConstants.CITY
import kg.healthlink.myapplication.utils.FirebaseConstants.NAME
import kg.healthlink.myapplication.utils.FirebaseConstants.USERS_STORE
import kg.healthlink.myapplication.utils.KeyboardHelper

class SignUpFragment : BaseFragment<FragmentSignUpBinding>(FragmentSignUpBinding::inflate) {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun initView() {
        super.initView()
        auth = Firebase.auth
        signUp()
    }

    private fun signUp() = with(vb) {
        btnSignIn.setOnClickListener {
            progressBar.visibility= View.VISIBLE
            KeyboardHelper.hideKeyboard(activity)
            if (etEmail.text.toString().isNotEmpty()
                && Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString()).matches()
                && etPassword.text.toString().length >= 8
                && etName.text.toString().isNotEmpty()
            ) {
                signUpWithEmailAndPassword(
                    etEmail.text.toString(),
                    etPassword.text.toString()
                )

            } else {
                etEmail.error = "Неправильная почта"
                etPassword.error = "Пароль должен содержать минимум 8 символов"
                etName.error = "Введите ваше имя"
            }
        }
    }

    private fun signUpWithEmailAndPassword(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    insertToFireStore()
                    findNavController().navigateUp()
                    vb.progressBar.visibility=GONE
                } else {
                    // If sign in fails, display a message to the user.
                    toast("Authentication failed.")
                    vb.progressBar.visibility=GONE
                }
            }
    }

    private fun insertToFireStore() {
        val userId = auth.currentUser?.uid
        db = FirebaseFirestore.getInstance()
        val userMap: MutableMap<String, Any> = HashMap()
        userMap[NAME] = vb.etName.text.toString()
        userMap[BORN] = vb.etAge.text.toString()
        userMap[CITY] = vb.etCity.text.toString()

        if (userId != null) {
            db.collection(USERS_STORE).document(userId).set(userMap)
                .addOnSuccessListener {
                    toast("Success")
                }
                .addOnFailureListener {
                    toast("Failed")
                }
        }
    }
}