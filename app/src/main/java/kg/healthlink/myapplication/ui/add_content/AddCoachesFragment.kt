package kg.healthlink.myapplication.ui.add_content

import android.net.Uri
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kg.healthlink.myapplication.databinding.FragmentAddCoachesBinding
import kg.healthlink.myapplication.extensions.toast
import kg.healthlink.myapplication.ui.base.BaseFragment
import kg.healthlink.myapplication.utils.FirebaseConstants
import kg.healthlink.myapplication.utils.FirebaseConstants.STORAGE_COACHES_IMAGE

class AddCoachesFragment :
    BaseFragment<FragmentAddCoachesBinding>(FragmentAddCoachesBinding::inflate) {

    private val db = Firebase.firestore

    override fun initView() {
        super.initView()
        vb.imageBack.setOnClickListener { findNavController().navigateUp() }
    }

    override fun initData() {
        super.initData()
        publishContent()
    }

    private fun publishContent() {
        //select from gallery
        var uri: Uri
        val imageFromGallery =
            registerForActivityResult(ActivityResultContracts.GetContent()) { galleryUri ->
                if (galleryUri != null) {
                    uri = galleryUri

                    //publish to backend
                    vb.btnPublish.setOnClickListener {
                        vb.progressBar.visibility = View.VISIBLE
                        val storage = FirebaseStorage.getInstance()
                        val reference = storage.reference
                            .child(STORAGE_COACHES_IMAGE)
                            .child(System.currentTimeMillis().toString())
                        reference.putFile(uri).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                reference.downloadUrl.addOnSuccessListener { uri ->

                                    val coachMap: MutableMap<String, Any> = HashMap()
                                    coachMap[FirebaseConstants.NAME] = vb.etName.text.toString()
                                    coachMap[FirebaseConstants.DESCRIPTION] =
                                        vb.etDesc.text.toString()
                                    coachMap[FirebaseConstants.URL_TO_PHOTO] = uri.toString()

                                    db.collection(FirebaseConstants.COACH_CONTENT)
                                        .add(coachMap)
                                        .addOnSuccessListener {
                                            vb.progressBar.visibility = View.GONE
                                            toast("fit room added")
                                            vb.etName.setText("")
                                            vb.etDesc.setText("")
                                        }.addOnFailureListener {
                                            toast("fit room didn't added")
                                            vb.progressBar.visibility = View.GONE
                                        }
                                }
                            }
                        }
                    }
                    vb.imageContent.setImageURI(uri)
                }
            }
        vb.btnAddPhoto.setOnClickListener {
            imageFromGallery.launch("image/*")
        }
    }
}