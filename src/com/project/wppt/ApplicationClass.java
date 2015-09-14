package com.project.wppt;

import android.app.Application;

import com.parse.Parse;

public class ApplicationClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "QtcnU88fC5lnWKL7GKggbNDPQs9A7PJMXWuflNKF", "d12B2IdYz0QJAf7JgVVTvNLDycyqvLfmjf4MiKgW");    }
}
