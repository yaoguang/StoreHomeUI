package com.yao.storehomeuidemo.store;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yao.storehomeuidemo.R;
import com.yao.storehomeuidemo.data.MenuInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Administrator on 2017/8/2.
 */

public class SecondAdapter extends BaseAdapter {
    protected static SecondAdapter secondMenuInstance;
    protected static SecondAdapter recyclerMenuInstance;

    private List<MenuInfo> dataList = new ArrayList<>();

    private ListMenus listMenus;
    private Context context;
    private LayoutInflater layoutInflater;

    // 二级菜单被选中的 viewHolder
    private HashSet<ViewHolder> selectViewList = new HashSet<>();

    public static void init(ListMenus listMenus) {
        secondMenuInstance = new SecondAdapter(listMenus);
        recyclerMenuInstance = new SecondAdapter(listMenus);
    }

    public static void notifyAllDataSetChanged() {
        secondMenuInstance.notifyDataSetChanged();
        recyclerMenuInstance.notifyDataSetChanged();
    }

    private SecondAdapter(ListMenus listMenus) {
        this.listMenus = listMenus;
        this.context = listMenus.content.getActivity();
        this.layoutInflater = LayoutInflater.from(context);
    }

    public static void setAllDataList(List<MenuInfo> dataList) {
        secondMenuInstance.setDataList(dataList);
        recyclerMenuInstance.setDataList(dataList);
    }

    private void setDataList(List<MenuInfo> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
    }

    @Override
    public MenuInfo getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_store_sec, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
            selectViewList.add(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.position = position;
        initHolderViews(getItem(position), holder, position);
        convertView.setOnClickListener(onItemClickListener);
        return convertView;
    }

    private void initHolderViews(MenuInfo data, ViewHolder holder, int position) {
        holder.content.setText(data.getMenu());
        refreshSelectState(data, holder, listMenus.secondSelectPosition == position);
    }

    protected void refreshAllSelectState(MenuInfo data, int selectPosition) {
        for (ViewHolder viewHolder : selectViewList) {
            refreshSelectState(data, viewHolder, selectPosition == viewHolder.position);
        }
    }

    private void refreshSelectState(MenuInfo data, ViewHolder holder, boolean isSelect) {
        if (isSelect) {
            if (holder.content.getCurrentTextColor() == 0xff888888)
                return;
            holder.content.setTextColor(0xff888888);
        } else {
            if (holder.content.getCurrentTextColor() == 0xff757575)
                return;
            holder.content.setTextColor(0xff757575);
        }
    }

    private int getColor(int id) {
        return context.getResources().getColor(id);
    }

    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ViewHolder holder = (ViewHolder) v.getTag();
            if (holder == null || holder.position >= dataList.size())
                return;
            listMenus.onSecondItemClick(v, dataList.get(holder.position), holder, holder.position);
        }
    };

    protected class ViewHolder {
        private TextView content;
        private int position;

        public ViewHolder(View view) {
            content = (TextView) view.findViewById(R.id.content);
        }
    }
}
