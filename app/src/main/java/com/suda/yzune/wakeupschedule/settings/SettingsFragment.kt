package com.suda.yzune.wakeupschedule.settings

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.drakeet.multitype.MultiTypeAdapter
import com.suda.yzune.wakeupschedule.base_view.BaseListFragment
import com.suda.yzune.wakeupschedule.settings.bean.CategoryItem
import com.suda.yzune.wakeupschedule.settings.bean.HorizontalItem
import com.suda.yzune.wakeupschedule.settings.bean.SwitchItem
import com.suda.yzune.wakeupschedule.settings.bean.VerticalItem
import com.suda.yzune.wakeupschedule.settings.view_binder.CategoryItemViewBinder
import com.suda.yzune.wakeupschedule.settings.view_binder.HorizontalItemViewBinder
import com.suda.yzune.wakeupschedule.settings.view_binder.SwitchItemViewBinder
import com.suda.yzune.wakeupschedule.settings.view_binder.VerticalItemViewBinder

class SettingsFragment : BaseListFragment() {

    private val mAdapter: MultiTypeAdapter = MultiTypeAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onAdapterCreated(mAdapter)
        val items = mutableListOf<Any>()
        onItemsCreated(items)
        mAdapter.items = items
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        mRecyclerView.adapter = mAdapter
    }


    private fun onItemsCreated(items: MutableList<Any>) {
        items.add(VerticalItem("课程数据"))
        items.add(VerticalItem("外观"))
        items.add(VerticalItem("桌面小部件"))
        items.add(VerticalItem("提醒"))
        items.add(VerticalItem("备份"))
        items.add(VerticalItem("关于"))
    }

    private fun onAdapterCreated(adapter: MultiTypeAdapter) {
        adapter.register(CategoryItem::class, CategoryItemViewBinder())
        adapter.register(HorizontalItem::class, HorizontalItemViewBinder { onHorizontalItemClick(it) })
        adapter.register(VerticalItem::class, VerticalItemViewBinder({ onVerticalItemClick(it) }, { false }))
        adapter.register(SwitchItem::class, SwitchItemViewBinder { item, isCheck -> onSwitchItemCheckChange(item, isCheck) })
    }

    private fun onSwitchItemCheckChange(item: SwitchItem, isChecked: Boolean) {
        when (item.title) {

        }
        item.checked = isChecked
    }

    private fun onHorizontalItemClick(item: HorizontalItem) {
        when (item.title) {

        }
    }

    private fun onVerticalItemClick(item: VerticalItem) {
        when (item.title) {

        }
    }
}