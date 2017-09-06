package com.yao.storehomeuidemo.store;

import android.app.Activity;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.yao.storehomeui.MenuGridView;
import com.yao.storehomeuidemo.R;
import com.yao.storehomeuidemo.data.MenuInfo;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/8/2.
 */

public class ListMenus {
    public ListMenus(StoreContent content) {
        this.content = content;
        this.binding = content.getActivity();
        recycleMenuList = content.getMenuGrid();
        initMenus();
    }

    protected StoreContent content;
    protected Activity binding;
    protected GridView recycleMenuList;

    // 商品菜单信息
    private ArrayList<MenuInfo> goodsMenus = new ArrayList<>();

    // 一级菜单选中项
    protected int firstSelectPosition = 0;
    // 一级菜单选中项
    protected int firstOldSelectPosition = 0;

    // 二级菜单选请求用确定选中项
    protected int secondSelectPosition = 0;
    protected int secondOldSelectPosition = 0;

    private void initMenus() {
        SecondAdapter.init(this);
        ((GridView) binding.findViewById(R.id.second_menu_list)).setAdapter(SecondAdapter.secondMenuInstance);
        recycleMenuList.setAdapter(SecondAdapter.recyclerMenuInstance);
    }

    public void refreshMenuState(boolean isHindMenu, boolean isValue) {
        if (isHindMenu) { // 隐藏商品菜单操作
            if (isValue) {
                firstOldSelectPosition = firstSelectPosition;
                secondOldSelectPosition = secondSelectPosition;
            }
            if (((MenuGridView) binding.findViewById(R.id.second_menu_list)).isMenuShow()) {
                ((MenuGridView) binding.findViewById(R.id.second_menu_list)).animGone();
            }
        } else { // 显示商品菜单操作
            SecondAdapter.notifyAllDataSetChanged();
        }
    }

    public void setGoodsMenus(ArrayList<MenuInfo> goodsMenus) {
        this.goodsMenus = goodsMenus;
        SecondAdapter.setAllDataList(this.goodsMenus);
    }

    public String getSecondMenuValue() {
        return goodsMenus.get(secondSelectPosition).getMenu();
    }

    protected void onSecondItemClick(View convertView, MenuInfo data, SecondAdapter.ViewHolder holder, int position) {
        // 判断点击是否有效
        if (firstOldSelectPosition != firstSelectPosition || secondSelectPosition != position) {
            // 防止连点
            // 刷新 viewHolder显示
            SecondAdapter.recyclerMenuInstance.refreshAllSelectState(data, position);
            SecondAdapter.secondMenuInstance.refreshAllSelectState(data, position);

            secondSelectPosition = position;

            ((TextView) binding.findViewById(R.id.second_menu)).setText(data.getMenu());

            refreshMenuState(true, true);
        } else {
            refreshMenuState(true, false);
        }
    }
}
