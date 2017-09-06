package com.yao.storehomeuidemo

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.GridView
import com.yao.storehomeuidemo.adapter.FirstMenuAdapter
import com.yao.storehomeuidemo.adapter.GoodsAdapter
import com.yao.storehomeuidemo.data.GoodsInfo
import com.yao.storehomeuidemo.data.MenuInfo
import com.yao.storehomeuidemo.store.ListMenus
import com.yao.storehomeuidemo.store.StoreContent
import com.yao.storehomeuidemo.util.ScreenUtils
import com.yao.storehomeuidemo.util.ViewsUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), StoreContent {
    private var goodsAdapter = GoodsAdapter()
    private val firstMenuAdapter: FirstMenuAdapter by lazy {
        FirstMenuAdapter(this@MainActivity)
    }
    private val listMenus: ListMenus by lazy {
        ListMenus(this@MainActivity)
    }

    private val secondMenuGrid: GridView by lazy {
        layoutInflater.inflate(R.layout.item_recycler_menu, recycler, false) as GridView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViewsBind()
        initStoreAbout()

        initDataAbout()
    }

    private fun initDataAbout() {
        val data = (0..99).map { GoodsInfo("商品#" + it, "") }
        goodsAdapter.setNewData(data)

        val menuList: List<MenuInfo> = (0..25).map { MenuInfo("菜单#" + it) }
        firstMenuAdapter.setDataList(menuList)

        listMenus.setGoodsMenus(menuList as ArrayList<MenuInfo>)
        refreshSecondMenuHeightAbout()
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
        goodsAdapter.setHeaderView(secondMenuGrid)

        first_menu_list.adapter = firstMenuAdapter
    }

    private fun initStoreAbout() {
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isMenuShown())
                    second_menu.visibility = View.GONE
                else {
                    second_menu.visibility = View.VISIBLE
                    second_menu.text = listMenus.secondMenuValue
                }
            }
        })

        second_menu.setOnClickListener { second_menu_list.animVisible() }

        second_menu_list.setListener {
            if (isMenuShown())
                second_menu.visibility = View.GONE
            else {
                second_menu.visibility = View.VISIBLE
                second_menu.text = listMenus.secondMenuValue
            }
        }
    }

    private fun isMenuShown(): Boolean {
        return second_menu_list.isMenuShow || recycler.isMenuShow
    }

    private fun refreshSecondMenuHeightAbout() {
        val secondMenuHeight = ViewsUtils.setGridViewHeightBasedOnChildren(second_menu_list, ScreenUtils.getScreenWidth() - ScreenUtils.dpToPx(85), null, null)
        second_menu_list.setMeasureHeight(secondMenuHeight, ScreenUtils.dpToPx(40))
        secondMenuGrid.layoutParams.height = secondMenuHeight
        secondMenuGrid.requestLayout()
        recycler.setHeadHeight(secondMenuHeight)
        recycler.smoothScrollToPosition(0)
        second_menu.visibility = View.GONE
    }

    override fun getActivity(): Activity {
        return this@MainActivity
    }

    override fun getMenuGrid(): GridView {
        return secondMenuGrid
    }
}
