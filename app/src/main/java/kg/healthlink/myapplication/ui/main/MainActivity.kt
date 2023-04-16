package kg.healthlink.myapplication.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.window.OnBackInvokedDispatcher
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.BuildCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kg.healthlink.myapplication.R
import kg.healthlink.myapplication.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var vb: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    @SuppressLint("UnsafeOptInUsageError")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vb = ActivityMainBinding.inflate(layoutInflater)
        setContentView(vb.root)

        auth = Firebase.auth
        val bottomNavView: BottomNavigationView = vb.navView
        val navController = findNavController(R.id.nav_host_fragment)

        //if user == null дальше не пустить
        if (auth.currentUser == null) {
            navController.navigate(R.id.authFragment)
            if (BuildCompat.isAtLeastT()) {
                onBackInvokedDispatcher.registerOnBackInvokedCallback(
                    OnBackInvokedDispatcher.PRIORITY_DEFAULT
                ) {
                    finish()
                }
            } else {
                onBackPressedDispatcher.addCallback(this) {
                    finish()
                }
            }
        }

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            val list = arrayListOf<Int>()
            list.add(R.id.navigation_coaches)
            list.add(R.id.navigation_fitness_rooms)
            list.add(R.id.navigation_news)
            list.add(R.id.navigation_profile)
            if (list.contains(destination.id)) {
                vb.navView.visibility = VISIBLE
            } else {
                vb.navView.visibility = GONE
            }
        }
        bottomNavView.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp() || super.onSupportNavigateUp()
    }
}