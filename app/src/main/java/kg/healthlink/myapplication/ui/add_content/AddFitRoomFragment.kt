package kg.healthlink.myapplication.ui.add_content

import android.net.Uri
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kg.healthlink.myapplication.databinding.FragmentAddFitRoomBinding
import kg.healthlink.myapplication.extensions.toast
import kg.healthlink.myapplication.ui.base.BaseFragment
import kg.healthlink.myapplication.utils.FirebaseConstants

class AddFitRoomFragment :
    BaseFragment<FragmentAddFitRoomBinding>(FragmentAddFitRoomBinding::inflate) {

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

                    //upload to backend
                    vb.btnPublish.setOnClickListener {
                        vb.progressBar.visibility = View.VISIBLE
                        val storage = FirebaseStorage.getInstance()
                        val reference = storage.reference
                            .child(FirebaseConstants.STORAGE_FIT_ROOM_IMAGE)
                            .child(System.currentTimeMillis().toString())
                        reference.putFile(uri).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                reference.downloadUrl.addOnSuccessListener { uri ->

                                    val fitRoomMap: MutableMap<String, Any> = HashMap()
                                    fitRoomMap[FirebaseConstants.NAME] = vb.etName.text.toString()
                                    fitRoomMap[FirebaseConstants.DESCRIPTION] = vb.etDesc.text.toString()
                                    fitRoomMap[FirebaseConstants.ADDRESS] = vb.etAddress.text.toString()
                                    fitRoomMap[FirebaseConstants.URL_TO_PHOTO] = uri.toString()

                                    db.collection(FirebaseConstants.FIT_ROOM_CONTENT)
                                        .add(fitRoomMap)
                                        .addOnSuccessListener {
                                            vb.progressBar.visibility = View.GONE
                                            vb.etName.setText("")
                                            vb.etDesc.setText("")
                                            vb.etAddress.setText("")
                                            toast("content added")
                                        }.addOnFailureListener {
                                            vb.progressBar.visibility = View.GONE
                                            toast("content didn't load")
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