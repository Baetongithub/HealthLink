package kg.healthlink.myapplication.ui.coaches

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kg.healthlink.myapplication.R
import kg.healthlink.myapplication.data.model.coaches.CoachesModel
import kg.healthlink.myapplication.databinding.FragmentCoachesBinding
import kg.healthlink.myapplication.extensions.toast
import kg.healthlink.myapplication.ui.base.BaseFragment
import kg.healthlink.myapplication.utils.Constants
import kg.healthlink.myapplication.utils.FirebaseConstants
import kg.healthlink.myapplication.utils.KeyboardHelper

class CoachesFragment : BaseFragment<FragmentCoachesBinding>(FragmentCoachesBinding::inflate) {

    private val listCoaches = arrayListOf<CoachesModel>()
    private val coachesAdapter: CoachesAdapter by lazy {
        CoachesAdapter(
            this::onItemClick
        )
    }

    override fun initView() {
        super.initView()
        setupRecyclerView()
        searchSomething()
        coachesAdapter.setData(listCoaches)
    }

    override fun initData() {
        super.initData()
        val db = Firebase.firestore
        db.collection(FirebaseConstants.COACH_CONTENT)
            .addSnapshotListener { value, error ->
                if (error!=null){
                    toast("Ошибка в $error")
                    return@addSnapshotListener
                }

                listCoaches.clear()
                for (dc: DocumentChange in value?.documentChanges!!){
                    if (dc.type == DocumentChange.Type.ADDED){
                        listCoaches.add(dc.document.toObject(CoachesModel::class.java))
                    }
                }
            }
    }

    private fun searchSomething() {
        vb.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                coachesAdapter.filter.filter(p0.toString())
            }
        })
    }

    private fun setupRecyclerView() {
        vb.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = coachesAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {
                        KeyboardHelper.hideKeyboard(activity)
                        vb.etSearch.visibility = View.GONE
                    } else vb.etSearch.visibility = View.VISIBLE
                }
            })
        }
    }

    private fun onItemClick(coachesModel: CoachesModel) {
        val bundle = Bundle()
        bundle.putSerializable(Constants.COACHES_BUNDLE, coachesModel)
        findNavController().navigate(
            R.id.action_navigation_coaches_to_detailedCoachFragment,
            bundle
        )
    }
}