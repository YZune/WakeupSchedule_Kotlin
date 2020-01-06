package com.suda.yzune.wakeupschedule.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavArgument
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseTitleActivity

class SettingsHostActivity : BaseTitleActivity() {

    override val layoutId: Int
        get() = R.layout.activity_settings_host

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_fragment) as NavHostFragment
        val navGraph = navHostFragment.navController.navInflater.inflate(R.navigation.nav_settings)
        val fragDestination = navGraph.findNode(R.id.settingsFragment)!!
        navHostFragment.navController.graph = navGraph
//        navController = Navigation.findNavController(this, R.id.nav_fragment)
//        navController.addOnDestinationChangedListener { _, destination, _ ->
//            mainTitle.text = destination.label
//        }
    }

}
