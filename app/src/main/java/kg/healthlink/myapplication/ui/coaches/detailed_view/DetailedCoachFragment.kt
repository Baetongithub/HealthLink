package kg.healthlink.myapplication.ui.coaches.detailed_view

import android.os.Build
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kg.healthlink.myapplication.R
import kg.healthlink.myapplication.data.model.coaches.CoachesModel
import kg.healthlink.myapplication.data.model.reviews.ReviewsModel
import kg.healthlink.myapplication.databinding.BottomSheetLeaveReviewBinding
import kg.healthlink.myapplication.databinding.FragmentDetailetViewCoachBinding
import kg.healthlink.myapplication.extensions.glide
import kg.healthlink.myapplication.ui.base.BaseFragment
import kg.healthlink.myapplication.ui.fit_rooms.detailed_view.ReviewsAdapter
import kg.healthlink.myapplication.utils.Constants
import kg.healthlink.myapplication.utils.KeyboardHelper
import java.io.Serializable
import kotlin.random.Random

class DetailedCoachFragment :
    BaseFragment<FragmentDetailetViewCoachBinding>(FragmentDetailetViewCoachBinding::inflate) {

    private val reviewsAdapter: ReviewsAdapter by lazy { ReviewsAdapter(this::onReviewClick) }
    private val listOfReview = arrayListOf<ReviewsModel>()

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    override fun initView() {
        super.initView()
        auth = FirebaseAuth.getInstance()
        val coachesModel = arguments?.customGetSerializable<CoachesModel>(Constants.COACHES_BUNDLE)

        vb.tvToolbarTitle.text = coachesModel?.name
        vb.tvCoachName.text = coachesModel?.name
        vb.tvFitRoomDescContent.text = coachesModel?.description
        vb.tvFitRoomName.text = coachesModel?.whichFitRoom
        vb.imageMainFitRoom.glide(coachesModel?.urlPhoto)

        vb.imageBack.setOnClickListener { findNavController().navigateUp() }

        vb.tvLeaveReview.setOnClickListener { openBottomSheetLeaveReviews() }
    }

    override fun initData() {
        super.initData()
        addREviews()
    }

    private fun openBottomSheetLeaveReviews() {
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
                        email,
                        vbDialog.etLeaveReview.text.toString(),
                        vbDialog.ratingBar.rating
                    )
                )
//
//                val reviewMap: MutableMap<String, Any> = HashMap()
//                reviewMap[FirebaseConstants.REVIEW_CONTENT] = vbDialog.etLeaveReview.text.toString()
//                reviewMap[FirebaseConstants.REVIEW_OWNER] = auth.currentUser?.email.toString()
//                reviewMap[FirebaseConstants.RATING] = vbDialog.ratingBar.rating.toString()
//
//                db.collection(FirebaseConstants.COACH_CONTENT)
//                    .document("reviewDoc")
//                    .collection("review")
//                    .add(reviewMap)
//                    .addOnSuccessListener {
//                        toast("success")
//                    }
//                    .addOnFailureListener {
//                        toast("failure")
//                    }
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

    private fun addREviews() {
        listOfReview.add(ReviewsModel(1, "example@gmail.com", "Хорошо", 4f))
        listOfReview.add(ReviewsModel(2, "example@gmail.com", "Все отлично", 5f))
        listOfReview.add(ReviewsModel(4, "example@gmail.com", "Все отлично", 5f))
        listOfReview.add(ReviewsModel(7, "example@gmail.com", "Плохо", 2f))
        listOfReview.add(ReviewsModel(6, "example@gmail.com", "Все отлично", 5f))
        listOfReview.add(ReviewsModel(8, "example@gmail.com", "Очень плохо", 1f))
        listOfReview.add(ReviewsModel(9, "example@gmail.com", "Пойдёт", 3f))
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
