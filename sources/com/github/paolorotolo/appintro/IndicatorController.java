package com.github.paolorotolo.appintro;

import android.content.Context;
import android.view.View;
import androidx.annotation.NonNull;

public interface IndicatorController {
    void initialize(int i);

    View newInstance(@NonNull Context context);

    void selectPosition(int i);

    void setSelectedIndicatorColor(int i);

    void setUnselectedIndicatorColor(int i);
}
