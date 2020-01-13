package com.machucapps.tictactoe.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.machucapps.tictactoe.R;
import com.machucapps.tictactoe.base.BaseActivity;

import butterknife.BindView;

/**
 * FindGameActivity Class
 */
public class FindGameActivity extends BaseActivity {

    /**
     * BindView's
     */
    @BindView(R.id.textViewLoading)
    TextView mTxtLoading;

    @BindView(R.id.progressBarGames)
    ProgressBar mPbGames;

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public int getLayoutID() {
        return R.layout.activity_find_game;
    }

    /**
     * {@inheritDoc}
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPbGames.setIndeterminate(true);
        mTxtLoading.setText("Cargando...");
    }

    /**
     * {@inheritDoc}
     *
     * @param firebaseUser - Firebase User
     * @param isSignUp
     */
    @Override
    public void updateUI(FirebaseUser firebaseUser, Boolean isSignUp) {

    }
}
