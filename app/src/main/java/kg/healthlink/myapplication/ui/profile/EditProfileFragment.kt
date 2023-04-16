package kg.healthlink.myapplication.ui.profile

import android.net.Uri
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kg.healthlink.myapplication.databinding.FragmentEditProfileBinding
import kg.healthlink.myapplication.extensions.glide
import kg.healthlink.myapplication.extensions.toast
import kg.healthlink.myapplication.ui.base.BaseFragment
import kg.healthlink.myapplication.utils.Constants
import kg.healthlink.myapplication.utils.FirebaseConstants

class EditProfileFragment :
    BaseFragment<FragmentEditProfileBinding>(FragmentEditProfileBinding::inflate) {

    private val db = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    override fun initView() {
        super.initView()
        auth = FirebaseAuth.getInstance()

        vb.imageBack.setOnClickListener { findNavController().navigateUp() }

        val name = arguments?.getString(Constants.USER_NAME)
        val age = arguments?.getString(Constants.USER_AGE)
        val city = arguments?.getString(Constants.USER_CITY)
        val urlToPhoto = arguments?.getString(Constants.URL_TO_PHOTO)

        vb.etName.setText(name)
        vb.etBorn.setText(age)
        vb.etCity.setText(city)
        vb.imageProfile.glide(urlToPhoto)

        updateUserData()
    }

    private fun updateUserData() {
        //select from gallery
        var uri: Uri
        val imageFromGallery =
            registerForActivityResult(ActivityResultContracts.GetContent()) { galleryUri ->
                if (galleryUri != null) {
                    uri = galleryUri

                    //update in backend
                    vb.btnUpdate.setOnClickListener {
                        vb.progressBar.visibility = View.VISIBLE
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
                                        db.collection(FirebaseConstants.USER_IMAGE).document(userID)
                                            .update(profileImageMap)

                                        val userMap: MutableMap<String, Any> = HashMap()
                                        userMap[FirebaseConstants.NAME] = vb.etName.text.toString()
                                        userMap[FirebaseConstants.BORN] = vb.etBorn.text.toString()
                                        userMap[FirebaseConstants.CITY] = vb.etCity.text.toString()

                                        db.collection(FirebaseConstants.USERS_STORE)
                                            .document(userID)
                                            .update(userMap)
                                            .addOnSuccessListener {
                                                findNavController().navigateUp()
                                                vb.progressBar.visibility = View.GONE
                                                toast("Обновлено")
                                            }
                                            .addOnFailureListener {
                                                vb.progressBar.visibility = View.GONE
                                                toast("Не удалось обновить")
                                            }
                                    }
                                }
                            }
                        }
                    }
                    vb.imageProfile.setImageURI(uri)
                }
            }
        vb.btnAddPhoto.setOnClickListener {
            imageFromGallery.launch("image/*")
        }
    }
}