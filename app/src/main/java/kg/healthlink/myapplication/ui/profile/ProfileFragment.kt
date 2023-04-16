package kg.healthlink.myapplication.ui.profile

import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kg.healthlink.myapplication.R
import kg.healthlink.myapplication.databinding.FragmentProfileBinding
import kg.healthlink.myapplication.extensions.glide
import kg.healthlink.myapplication.extensions.toast
import kg.healthlink.myapplication.ui.base.BaseFragment
import kg.healthlink.myapplication.utils.Constants
import kg.healthlink.myapplication.utils.FirebaseConstants
import kg.healthlink.myapplication.utils.FirebaseConstants.BORN
import kg.healthlink.myapplication.utils.FirebaseConstants.CITY
import kg.healthlink.myapplication.utils.FirebaseConstants.NAME
import kg.healthlink.myapplication.utils.FirebaseConstants.USERS_STORE
import kg.healthlink.myapplication.utils.FirebaseConstants.USER_IMAGE
import java.util.*

class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    private var urlPhotoToBundle = ""

    override fun initView() {
        super.initView()
        auth = FirebaseAuth.getInstance()
        getUserDataFromFireStore()
        uploadProfilePhoto()

        vb.imageLogOut.setOnClickListener { logOut() }

        vb.btnAddCoaches.setOnClickListener { findNavController().navigate(R.id.addCoachesFragment) }
        vb.btnAddFitRooms.setOnClickListener { findNavController().navigate(R.id.addFitRoomFragment) }

        vb.btnChangeName.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(Constants.USER_NAME, vb.tvName.text.toString())
            bundle.putString(Constants.USER_AGE, vb.tvAge.text.toString())
            bundle.putString(Constants.USER_CITY, vb.tvLocation.text.toString())
            bundle.putString(Constants.URL_TO_PHOTO, urlPhotoToBundle)
            findNavController().navigate(
                R.id.action_navigation_profile_to_editProfileFragment,
                bundle
            )
        }
    }

    private fun uploadProfilePhoto() {
        //select from gallery
        var uri: Uri
        val imageFromGallery =
            registerForActivityResult(ActivityResultContracts.GetContent()) { galleryUri ->
                if (galleryUri != null) {
                    uri = galleryUri

                    val storage = FirebaseStorage.getInstance()
                    val reference = storage.reference
                        .child(FirebaseConstants.STORAGE_PROFILE_IMAGE)
                        .child(System.currentTimeMillis().toString())
                    reference.putFile(uri).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            reference.downloadUrl.addOnSuccessListener { uri ->
                                val profileImageMap = HashMap<String, Any>()
                                profileImageMap[FirebaseConstants.IMAGE_URl_FIRESTORE] =
                                    uri.toString()

                                val userID = auth.currentUser?.uid
                                if (userID != null) {
                                    db.collection(USER_IMAGE).document(userID).set(profileImageMap)

                                }
                            }
                        }
                    }
                    vb.imageAvatar.setImageURI(uri)
                }
            }
        vb.imageAvatar.setOnClickListener {
            imageFromGallery.launch("image/*")
        }
    }

    private fun getUserDataFromFireStore() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection(USERS_STORE).document(userId)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val document = task.result
                        if (document.exists()) {
                            val email = auth.currentUser?.email
                            if (email != null)
                                vb.tvEmail.text = email

                            vb.tvName.text = document.getString(NAME)
                            vb.tvAge.text = document.getString(BORN)
                            vb.tvLocation.text = document.getString(CITY)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    toast("Ошибка в $exception")
                }

            //load profile image
            db.collection(USER_IMAGE)
                .document(userId)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val document = task.result
                        if (document.exists()) {
                            val urlToPhoto =
                                document.getString(FirebaseConstants.IMAGE_URl_FIRESTORE)
                            if (urlToPhoto != null) {
                                urlPhotoToBundle = urlToPhoto
                                vb.imageAvatar.glide(urlToPhoto)
                            }
                        }
                    }
                }
        }
    }

    private fun logOut() {
        FirebaseAuth.getInstance().signOut()
        activity?.finish()
        toast("Вы вышли из аккаунта")
    }
}