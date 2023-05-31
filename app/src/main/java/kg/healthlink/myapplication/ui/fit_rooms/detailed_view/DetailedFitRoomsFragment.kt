package kg.healthlink.myapplication.ui.fit_rooms.detailed_view

import android.os.Build
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kg.healthlink.myapplication.R
import kg.healthlink.myapplication.data.model.fit_rooms.FitRoomsModel
import kg.healthlink.myapplication.data.model.reviews.ReviewsModel
import kg.healthlink.myapplication.databinding.BottomSheetLeaveReviewBinding
import kg.healthlink.myapplication.databinding.FragmentDetailedFitRoomsBinding
import kg.healthlink.myapplication.extensions.glide
import kg.healthlink.myapplication.extensions.toast
import kg.healthlink.myapplication.ui.base.BaseFragment
import kg.healthlink.myapplication.utils.Constants
import kg.healthlink.myapplication.utils.FirebaseConstants
import kg.healthlink.myapplication.utils.KeyboardHelper
import java.io.Serializable
import kotlin.random.Random

class DetailedFitRoomsFragment :
    BaseFragment<FragmentDetailedFitRoomsBinding>(FragmentDetailedFitRoomsBinding::inflate) {

    private val reviewsAdapter: ReviewsAdapter by lazy { ReviewsAdapter(this::onReviewClick) }
    private val listOfReview = arrayListOf<ReviewsModel>()

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    override fun initView() {
        super.initView()
        auth = FirebaseAuth.getInstance()
        val fitRoomModel =
            arguments?.customGetSerializable<FitRoomsModel>(Constants.FIT_TRAINERS_BUNDLE)

        vb.tvToolbarTitle.text = fitRoomModel?.name
        vb.tvFitRoomName.text = fitRoomModel?.name
        vb.tvFitRoomDescContent.text = fitRoomModel?.description
        vb.tvFitRoomLocation.text = fitRoomModel?.address
        vb.tvCoachName.text = fitRoomModel?.coaches
        vb.imageMainFitRoom.glide(fitRoomModel?.urlPhoto)

        vb.imageBack.setOnClickListener { findNavController().navigateUp() }

        vb.tvLeaveReview.setOnClickListener { openBottomSheetLeaveReviews() }
    }

    override fun initData() {
        super.initData()
        val fitRoomModel =
            arguments?.customGetSerializable<FitRoomsModel>(Constants.FIT_TRAINERS_BUNDLE)
        var reviewAmount = 0

        db.collection(FirebaseConstants.FIT_ROOM_CONTENT)
            .document(FirebaseConstants.REVIEW_DOC)
            .collection(fitRoomModel?.name.toString())
            .addSnapshotListener { value, error ->
                if (error != null) {
                    toast("Ошибка в $error")
                    return@addSnapshotListener
                }

                listOfReview.clear()
                for (dc: DocumentChange in value?.documentChanges!!) {
                    if (dc.type == DocumentChange.Type.ADDED) {
                        listOfReview.add(dc.document.toObject(ReviewsModel::class.java))
                        reviewAmount++
                    }
                }
                vb.tvReviewAmount.text = String.format("$reviewAmount оценки")
            }
    }

    private fun openBottomSheetLeaveReviews() {
        val fitRoomModel =
            arguments?.customGetSerializable<FitRoomsModel>(Constants.FIT_TRAINERS_BUNDLE)

        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        val vbDialog: BottomSheetLeaveReviewBinding = BottomSheetLeaveReviewBinding.inflate(
            layoutInflater
        )

        vbDialog.recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, true)
        vbDialog.recyclerView.adapter = reviewsAdapter

        val id: Int = Random(100).nextInt()
        vbDialog.btnShareReview.setOnClickListener {
            val email = auth.currentUser?.email
            if (email != null) {
                listOfReview.add(
                    ReviewsModel(
                        id,
                        vbDialog.ratingBar.rating.toDouble(),
                        vbDialog.etLeaveReview.text.toString(),
                        email
                    )
                )

                val reviewMap: MutableMap<String, Any> = HashMap()
                reviewMap[FirebaseConstants.REVIEW_CONTENT] = vbDialog.etLeaveReview.text.toString()
                reviewMap[FirebaseConstants.REVIEW_OWNER] = email
                reviewMap[FirebaseConstants.RATING] = vbDialog.ratingBar.rating

                db.collection(FirebaseConstants.FIT_ROOM_CONTENT)
                    .document(FirebaseConstants.REVIEW_DOC)
                    .collection(fitRoomModel?.name.toString())
                    .add(reviewMap)
                    .addOnSuccessListener {
                        toast("Опубликован")
                    }
                    .addOnFailureListener {
                        toast("Не удалось опубликовать")
                    }
            }
            vbDialog.etLeaveReview.setText("")
            vbDialog.ratingBar.rating = 0f
            KeyboardHelper.hideKeyboard(activity)
            reviewsAdapter.notifyItemInserted(listOfReview.size - 1)
        }
        reviewsAdapter.submitList(listOfReview)

        bottomSheetDialog.setContentView(vbDialog.root)
        bottomSheetDialog.show()
    }

    private fun onReviewClick(reviewsModel: ReviewsModel) {

    }

    @Suppress("DEPRECATION")
    inline fun <reified T : Serializable> Bundle.customGetSerializable(key: String): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getSerializable(key, T::class.java)
        } else {
            getSerializable(key) as? T
        }
    }
}