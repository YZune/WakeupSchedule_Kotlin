package com.suda.yzune.wakeupschedule.settings

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatTextView
import androidx.navigation.NavArgument
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseTitleActivity
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_time_settings.*
import kotlinx.coroutines.delay
import splitties.resources.color
import splitties.snackbar.longSnack

class TimeSettingsActivity : BaseTitleActivity() {
    override val layoutId: Int
        get() = R.layout.activity_time_settings

    override fun onSetupSubButton(tvButton: AppCompatTextView): AppCompatTextView? {
        tvButton.text = "保存"
        tvButton.typeface = Typeface.DEFAULT_BOLD
        tvButton.setTextColor(color(R.color.colorAccent))
        tvButton.setOnClickListener {
            when (navController.currentDestination?.id) {
                R.id.timeTableFragment -> {
                    setResult(Activity.RESULT_OK, Intent().putExtra("selectedId", viewModel.selectedId))
                    finish()
                }
                R.id.timeSettingsFragment -> {
                    launch {
                        try {
                            viewModel.saveDetailData(viewModel.entryPosition)
                            navController.navigateUp()
                            Toasty.success(applicationContext, "保存成功").show()
                        } catch (e: Exception) {
                            Toasty.error(applicationContext, "出现错误>_<${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }

                }
            }
        }
        return tvButton
    }

    private val viewModel by viewModels<TimeSettingsViewModel>()
    private lateinit var navController: NavController
    private var isExit: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_fragment) as NavHostFragment
        val navGraph = navHostFragment.navController.navInflater.inflate(R.navigation.nav_time_settings)
        val fragDestination = navGraph.findNode(R.id.timeTableFragment)!!
        fragDestination.addArgument("selectedId", NavArgument.Builder()
                .setType(NavType.IntType).setIsNullable(false).setDefaultValue(intent.extras!!.getInt("selectedId")).build())
//        fragDestination.setDefaultArguments(Bundle().apply {
//            this.putInt("selectedId", intent.extras!!.getInt("selectedId"))
//        })
        navHostFragment.navController.graph = navGraph
        navController = Navigation.findNavController(this, R.id.nav_fragment)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            mainTitle.text = destination.label
        }
    }

    private fun exitBy2Click() {
        if (!isExit) {
            isExit = true // 准备退出
            ll_root.longSnack("真的不保存吗？那再按一次退出编辑哦，就不保存啦。")
            launch {
                delay(2000)
                isExit = false
            }
        } else {
            when (navController.currentDestination?.id) {
                R.id.timeTableFragment -> {
                    finish()
                }
                R.id.timeSettingsFragment -> {
                    navController.navigateUp()
                }
            }
        }
    }

    override fun onBackPressed() {
        exitBy2Click()
    }

}
