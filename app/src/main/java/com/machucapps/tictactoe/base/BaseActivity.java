package com.machucapps.tictactoe.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.ButterKnife;

/**
 * BaseActivity Class
 */
public abstract class BaseActivity extends AppCompatActivity {

    /**
     * FirebaseAuth
     */
    protected FirebaseAuth firebaseAuth;

    /**
     * {@inheritDoc}
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutID());
        ButterKnife.bind(this);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    /**
     * Get Layout Id
     *
     * @return - layout Id
     */
    public abstract int getLayoutID();

    /**
     * Update UI
     *
     * @param firebaseUser - Firebase User
     */
    public abstract void updateUI(FirebaseUser firebaseUser, Boolean isSignUp);


}
