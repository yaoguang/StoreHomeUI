package com.yao.storehomeuidemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yao.storehomeuidemo.R;
import com.yao.storehomeuidemo.data.MenuInfo;

import java.util.ArrayList;
import java.util.List;

public class FirstMenuAdapter extends BaseAdapter {

    private List<MenuInfo> dataList = new ArrayList<>();

    private Context context;
    private LayoutInflater layoutInflater;

    public FirstMenuAdapter(Context context) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public FirstMenuAdapter(Context context, List<MenuInfo> dataList) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.dataList = dataList;
    }

    public void setDataList(List<MenuInfo> dataList) {
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
            convertView = layoutInflater.inflate(R.layout.item_first_menu, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.position = position;
        initHolderViews(getItem(position), holder, position);
        convertView.setOnClickListener(onItemClickListener);
        return convertView;
    }

    private void initHolderViews(MenuInfo data, ViewHolder holder, int position) {
        holder.firstMenuName.setText(data.getMenu());
    }

    private void onItemClick(View convertView, MenuInfo data, ViewHolder holder, int position) {
    }

    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ViewHolder holder = (ViewHolder) v.getTag();
            if (holder == null || holder.position >= dataList.size())
                return;
            onItemClick(v, dataList.get(holder.position), holder, holder.position);
        }
    };

    protected class ViewHolder {
        private TextView firstMenuName;
        private int position;

        public ViewHolder(View view) {
            firstMenuName = (TextView) view.findViewById(R.id.first_menu_name);
        }
    }
}
