package com.liying.app;

import android.app.Activity;
import java.util.ArrayList;
import java.util.List;

public class AtyContainer {
    private static List<Activity> activityStack = new ArrayList();
    private static AtyContainer instance = new AtyContainer();

    private AtyContainer() {
    }

    public static AtyContainer getInstance() {
        return instance;
    }

    public void addActivity(Activity aty) {
        activityStack.add(aty);
    }

    public void removeActivity(Activity aty) {
        activityStack.remove(aty);
    }

    public void finishAllActivity() {
        int size = activityStack.size();
        for (int i = 0; i < size; i++) {
            if (activityStack.get(i) != null) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }
}
