package com.yao.storehomeuidemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import com.yao.storehomeuidemo.adapter.FirstMenuAdapter
import com.yao.storehomeuidemo.adapter.GoodsAdapter
import com.yao.storehomeuidemo.data.GoodsInfo
import com.yao.storehomeuidemo.data.MenuInfo
import kotlinx.android.synthetic.main.activity_main.*

class StoreActivity : AppCompatActivity() {

    private var goodsAdapter = GoodsAdapter()
    private val firstMenuAdapter: FirstMenuAdapter by lazy {
        FirstMenuAdapter(this@StoreActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViewsBind()

        initDataAbout()
    }

    private fun initDataAbout() {
        val data = (0..99).map { GoodsInfo("商品#" + it, "") }
        goodsAdapter.setNewData(data)

        val menuList = (0..50).map { MenuInfo("菜单#" + it) }
        firstMenuAdapter.setDataList(menuList)
    }

    private fun initViewsBind() {
        content_view.bindViews(top_content, back_scroll)
        content_view.bindViews(top_content, back_scroll, resources.getDimension(R.dimen.activity_title_height).toInt())
        top_content.bindChildViews(first_menu_list, recycler, second_menu_list)

        store_head.viewTreeObserver.addOnGlobalLayoutListener {
            top_content.refresh(store_head.bottom + 10)
        }

        recycler.layoutManager = GridLayoutManager(this, 2)
        goodsAdapter.bindToRecyclerView(recycler)

        first_menu_list.adapter = firstMenuAdapter
    }
}