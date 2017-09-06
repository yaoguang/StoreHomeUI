package com.yao.storehomeuidemo.store;

import android.app.Activity;
import android.widget.GridView;

/**
 * Created by Administrator on 2016/10/17.
 */
public interface StoreContent {
    Activity getActivity();

    GridView getMenuGrid();
}
