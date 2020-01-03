package com.example.damproject.util;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

public abstract class UTIL {
    private static double KG_TO_LBS = 2.20462262;
    private static double LBS_TO_KG = 0.45359237;

    private static double CM_TO_INCHES = 0.393700787;
    private static double CM_TO_FEET = 0.032808399;
    private static double INCHES_TO_CM = 2.54;
    private static double INCHES_TO_FEET = 0.0833333333;
    private static double FEET_TO_CM = 30.48;
    private static double FEET_TO_INCHES = 12;

    private static double EARTH_RADIUS = 6371;

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

    public static double getDistanceBetweenTwoCoordonates(LatLng l1, LatLng l2) {
        double lat1 = l1.latitude,
                lng1 = l1.longitude,
                lat2 = l2.latitude,
                lng2 = l2.longitude;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2) * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double dist = EARTH_RADIUS * c;

        return dist;
    }
}
