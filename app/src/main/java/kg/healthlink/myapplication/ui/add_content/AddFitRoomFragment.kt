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

        vb.btnAddMoreCoach.setOnClickListener {
            vb.etCoaches2.visibility = View.VISIBLE
            vb.etCoaches3.visibility = View.VISIBLE
            vb.btnAddMoreCoach.visibility = View.GONE
        }
    }

    override fun initData() {
        super.initData()
        publishContent()
    }

    private fun publishContent() = with(vb) {
        //select from gallery
        var uri: Uri
        val imageFromGallery =
            registerForActivityResult(ActivityResultContracts.GetContent()) { galleryUri ->
                if (galleryUri != null) {
                    uri = galleryUri

                    //upload to backend
                    btnPublish.setOnClickListener {
                        progressBar.visibility = View.VISIBLE
                        val storage = FirebaseStorage.getInstance()
                        val reference = storage.reference
                            .child(FirebaseConstants.STORAGE_FIT_ROOM_IMAGE)
                            .child(System.currentTimeMillis().toString())
                        reference.putFile(uri).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                reference.downloadUrl.addOnSuccessListener { uri ->

                                    val fitRoomMap: MutableMap<String, Any> = HashMap()
                                    fitRoomMap[FirebaseConstants.NAME] = etName.text.toString()
                                    fitRoomMap[FirebaseConstants.DESCRIPTION] =
                                        etDesc.text.toString()
                                    fitRoomMap[FirebaseConstants.ADDRESS] =
                                        etAddress.text.toString()
                                    fitRoomMap[FirebaseConstants.COACHES] =
                                        etCoaches.text.toString() +
                                                "\n${etCoaches2.text}\n${etCoaches3.text}"
                                    fitRoomMap[FirebaseConstants.URL_TO_PHOTO] = uri.toString()

                                    if (etCoaches.text.isNotEmpty())
                                        db.collection(FirebaseConstants.FIT_ROOM_CONTENT)
                                            .add(fitRoomMap)
                                            .addOnSuccessListener {
                                                progressBar.visibility = View.GONE
                                                etName.setText("")
                                                etDesc.setText("")
                                                etDesc.setText("")
                                                etDesc.setText("")
                                                etAddress.setText("")
                                                etCoaches.setText("")
                                                etCoaches2.setText("")
                                                etCoaches3.setText("")
                                                imageContent.setImageDrawable(null)
                                                toast("new fit room added")
                                            }.addOnFailureListener {
                                                progressBar.visibility = View.GONE
                                                toast("new coach didn't added")
                                            }
                                    else toast("Добавьте штатных тренеров")
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