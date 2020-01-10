package com.machucapps.tictactoe.app;

import android.app.Application;

import com.google.firebase.FirebaseApp;

/**
 * MyApp Class
 */
public class MyApp extends Application {

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}
