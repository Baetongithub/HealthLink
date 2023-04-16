package kg.healthlink.myapplication.ui.fit_rooms

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kg.healthlink.myapplication.R
import kg.healthlink.myapplication.data.model.fit_rooms.FitRoomsModel
import kg.healthlink.myapplication.databinding.FragmentFitRoomsBinding
import kg.healthlink.myapplication.extensions.toast
import kg.healthlink.myapplication.ui.base.BaseFragment
import kg.healthlink.myapplication.utils.Constants
import kg.healthlink.myapplication.utils.FirebaseConstants
import kg.healthlink.myapplication.utils.KeyboardHelper

class FitRoomFragment : BaseFragment<FragmentFitRoomsBinding>(FragmentFitRoomsBinding::inflate) {

    private val listFitnessRooms = arrayListOf<FitRoomsModel>()
    private val fitRoomsAdapter: FitRoomsAdapter by lazy {
        FitRoomsAdapter(
            this::onClickNews
        )
    }

    //проблема с непоказом списка фит залов решена здесь с перемещением setupRecyclerView()
    // & fitnessRoomsAdapter сюда
    override fun onResume() {
        super.onResume()
        setupRecyclerView()
        fitRoomsAdapter.setData(listFitnessRooms)
    }

    override fun initView() {
        super.initView()
        setupRecyclerView()
        searchFitRooms()
        fitRoomsAdapter.setData(listFitnessRooms)
    }

    override fun initData() {
        super.initData()
        val db = Firebase.firestore
        db.collection(FirebaseConstants.FIT_ROOM_CONTENT)
            .addSnapshotListener { value, error ->
                if (error!=null){
                    toast("Ошибка в $error")
                    return@addSnapshotListener
                }

                listFitnessRooms.clear()
                for (dc:DocumentChange in value?.documentChanges!!){
                    if (dc.type == DocumentChange.Type.ADDED){
                        listFitnessRooms.add(dc.document.toObject(FitRoomsModel::class.java))
                    }
                }
            }
    }

    private fun searchFitRooms() {
        vb.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                fitRoomsAdapter.filter.filter(p0.toString())
            }
        })
    }

    private fun setupRecyclerView() {
        vb.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = fitRoomsAdapter
            addOnScrollListener(object : OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {
                        KeyboardHelper.hideKeyboard(activity)
                        vb.etSearch.visibility = GONE
                    } else vb.etSearch.visibility = VISIBLE
                }
            })
        }
    }

    private fun onClickNews(fitRooms: FitRoomsModel) {
        val bundle = Bundle()
        bundle.putSerializable(Constants.FIT_TRAINERS_BUNDLE, fitRooms)
        findNavController().navigate(
            R.id.action_navigation_fitness_rooms_to_detailedFitRoomsFragment,
            bundle
        )
    }
}