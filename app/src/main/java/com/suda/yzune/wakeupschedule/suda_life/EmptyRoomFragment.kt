package com.suda.yzune.wakeupschedule.suda_life

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.base_view.BaseFragment
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.fragment_empty_room.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.sdk27.coroutines.onItemSelectedListener
import org.jetbrains.anko.support.v4.dip

class EmptyRoomFragment : BaseFragment() {

    private lateinit var viewModel: SudaLifeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(SudaLifeViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_empty_room, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spinner_campus.dropDownVerticalOffset = dip(48)
        spinner_building.dropDownVerticalOffset = dip(48)
        spinner_date.dropDownVerticalOffset = dip(48)

        initData()
        initEvent()

        rv_room.adapter = RoomAdapter(R.layout.item_suda_room, viewModel.roomData)
        rv_room.layoutManager = LinearLayoutManager(activity!!)
    }

    private fun initData() {
        launch {
            val task = withContext(Dispatchers.IO) {
                try {
                    viewModel.getBuildingData()
                } catch (e: Exception) {
                    e.message
                }
            }
            if (task == "ok") {
                spinner_campus.adapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, viewModel.buildingData.keys.toList())
                        .apply {
                            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        }
            } else {
                Toasty.error(activity!!, "发生异常>_<$task").show()
            }
        }

        launch {
            spinner_date.adapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, viewModel.getDateList())
                    .apply {
                        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
        }
    }

    private fun initEvent() {
        spinner_campus.onItemSelectedListener {
            this.onItemSelected { _, v, _, _ ->
                spinner_building.adapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_item,
                        viewModel.buildingData[(v as TextView).text]!!)
                        .apply {
                            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        }
            }
        }

        spinner_building.onItemSelectedListener {
            this.onItemSelected { _, _, _, _ ->
                queryRoomData()
            }
        }

        spinner_date.onItemSelectedListener {
            this.onItemSelected { _, _, _, _ ->
                queryRoomData()
            }
        }
    }

    private fun queryRoomData() {
        val building = spinner_building.selectedItem as String?
        val date = spinner_date.selectedItem as String?
        if (building != null && date != null) {
            launch {
                val task = withContext(Dispatchers.IO) {
                    try {
                        viewModel.getRoomData(building, date)
                    } catch (e: Exception) {
                        e.message
                    }
                }
                if (task == "ok") {
                    rv_room.adapter?.notifyDataSetChanged()
                } else {
                    Toasty.error(activity!!, "发生异常>_<$task").show()
                }
            }
        }
    }

}
