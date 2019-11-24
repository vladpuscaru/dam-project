package com.example.damproject.util;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public abstract class UTIL {
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter adapter = listView.getAdapter();

        if (adapter != null) {
            int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);

            int totalHeight = 0;
            View view = null;

            for (int i = 0; i < adapter.getCount(); i++) {
                view = adapter.getView(i, view, listView);

                if (i == 0) {
                    view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                }

                view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
                totalHeight += view.getMeasuredHeight();
            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();

            params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));

            listView.setLayoutParams(params);
            listView.requestLayout();
        }
    }
}
