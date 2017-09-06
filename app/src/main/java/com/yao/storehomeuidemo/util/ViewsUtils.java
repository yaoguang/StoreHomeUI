package com.yao.storehomeuidemo.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.yao.storehomeui.utils.ClassUtil;

/**
 * Created by Administrator on 2015/10/30.
 * <p/>
 * 特殊处理控件的工具类
 */
public class ViewsUtils {
    /**
     * 关联TextView与Edittext的点击事件
     */
    public static void editTextWithLeftText(final EditText editText, TextView textView) {
        textView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            editText.requestFocus();
                                            editText.setSelection(editText.getText().length());
                                            InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                                        }
                                    }
        );
    }

    /**
     * 计算listView全部item总高度，全部显示
     *
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        setListViewHeightBasedOnChildren(listView, null, Integer.MAX_VALUE);
    }

    /**
     * 计算listView全部item总高度，全部显示
     *
     * @param listView
     */
    public static int setListViewHeightBasedOnChildren(ListView listView, Integer maxWidth, Integer maxHeight) {
        return setListViewHeightBasedOnChildren(listView, maxWidth, maxHeight, null);
    }

    /**
     * 计算listView全部item总高度，全部显示
     *
     * @param listView
     */
    public static int setListViewHeightBasedOnChildren(ListView listView, Integer maxWidth, Integer maxHeight, Integer minHeight) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return 0;
        }
        int width = listView.getWidth();
        width = (width == 0) ? ScreenUtils.getScreenWidth() : width;
        if (maxWidth != null)
            width = Math.min(width, maxWidth);
        int height = ScreenUtils.getScreenHeight();

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.AT_MOST));
            totalHeight += listItem.getMeasuredHeight();
        }

        totalHeight = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        if (maxHeight != null)
            totalHeight = Math.min(totalHeight, maxHeight);
        else if (minHeight != null)
            totalHeight = Math.max(totalHeight, minHeight);
        listView.getLayoutParams().height = totalHeight;
        listView.requestLayout();
        return totalHeight;
    }

    /**
     * 计算listView全部item总高度，全部显示
     *
     * @param gridView
     */
    public static int setGridViewHeightBasedOnChildren(GridView gridView, Integer maxWidth, Integer maxHeight, Integer minHeight) {
        try {
            ListAdapter listAdapter = gridView.getAdapter();
            if (listAdapter == null) {
                return 0;
            }
            int gridNumColumns = ClassUtil.getPrivateParameter(gridView, "mRequestedNumColumns");
            int linNum = listAdapter.getCount() / gridNumColumns + (listAdapter.getCount() % gridNumColumns > 0 ? 1 : 0);
            int width = gridView.getWidth();
            width = (width == 0) ? ScreenUtils.getScreenWidth() : width;
            if (maxWidth != null)
                width = Math.min(width, maxWidth);
            int height = ScreenUtils.getScreenHeight();

            int totalHeight = gridView.getPaddingTop() + gridView.getPaddingBottom();
            View listItem = listAdapter.getView(0, null, gridView);
            listItem.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.AT_MOST));
            totalHeight += listItem.getMeasuredHeight() * linNum;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                totalHeight = totalHeight + (gridView.getVerticalSpacing() * (linNum - 1));
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                totalHeight = totalHeight + (gridView.getVerticalSpacing() * (linNum + 1));

            if (maxHeight != null)
                totalHeight = Math.min(totalHeight, maxHeight);
            else if (minHeight != null)
                totalHeight = Math.max(totalHeight, minHeight);
            gridView.getLayoutParams().height = totalHeight;
            gridView.requestLayout();
            return totalHeight;
        } catch (Exception e) {
            return 0;
        }
    }

    public static void addListViewTopBottomDivide(ListView listView) {
        View view = new View(listView.getContext());
        View view2 = new View(listView.getContext());
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        view.setLayoutParams(lp);
        view2.setLayoutParams(lp);
        listView.addHeaderView(view);
        listView.addFooterView(view);
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static int getVersionCode(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            int versionCode = info.versionCode;
            return versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    /**
     * 获取版本名
     *
     * @return 当前应用的版本名称
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String versionName = info.versionName;
            return versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "1.00";
        }
    }
}
