package com.app.Utility;

import com.app.domain.Axis;

import java.util.ArrayList;

/**
 * Created by adenau on 11/10/16.
 */
public class AccelUtility {

    public static ArrayList<Axis> removeNoFix(ArrayList<Axis> axes) {
        ArrayList<Axis> toReturn = new ArrayList<>();
        for (int i = 0; i < axes.size(); i++) {
            Axis axis = axes.get(i);
            if (axis != null) {
                toReturn.add(axis);
            }
        }
        return toReturn;
    }
}
