package com.example.damproject.util;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public abstract class UTIL {
    private static double KG_TO_LBS = 2.20462262;
    private static double LBS_TO_KG = 0.45359237;

    private static double CM_TO_INCHES = 0.393700787;
    private static double CM_TO_FEET = 0.032808399;
    private static double INCHES_TO_CM = 2.54;
    private static double INCHES_TO_FEET = 0.0833333333;
    private static double FEET_TO_CM = 30.48;
    private static double FEET_TO_INCHES = 12;

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

    public static float weightConverter(float weight, String from, String to) {
        if (from.equals("kg") && to.equals("lbs")) {
            return weight * (float) KG_TO_LBS;
        } else if (from.equals("lbs") && to.equals("kg")) {
            return weight * (float) LBS_TO_KG;
        }


        return -1;
    }

    public static float heightConverter(float height, String from, String to) {
        if (from.equals("cm")) {
            if (to.equals("inches")) {
                return height * (float) CM_TO_INCHES;
            } else if (to.equals("feet")) {
                return height * (float) CM_TO_FEET;
            }
        } else if (from.equals("inches")) {
            if (to.equals("cm")) {
                return height * (float) INCHES_TO_CM;
            } else if (to.equals("feet")) {
                return height * (float) INCHES_TO_FEET;
            }
        } else if (from.equals("feet")) {
            if (to.equals("cm")) {
                return height * (float) FEET_TO_CM;
            } else if (to.equals("inches")) {
                return height * (float) FEET_TO_INCHES;
            }
        }


        return -1;
    }
}
