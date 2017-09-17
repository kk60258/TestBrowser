package org.mozilla.materialfennec;

import android.app.Application;

import org.mozilla.materialfennec.dependency.Dependency;

/**
 * Created by nineg on 2017/9/17.
 */

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        new Dependency().init(this);
    }
}
