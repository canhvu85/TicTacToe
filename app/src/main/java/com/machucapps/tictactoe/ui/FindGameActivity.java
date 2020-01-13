package com.machucapps.tictactoe.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.machucapps.tictactoe.R;
import com.machucapps.tictactoe.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * FindGameActivity Class
 */
public class FindGameActivity extends BaseActivity {

    private FirebaseUser currentUser;
    private String userId;

    /**
     * BindView's
     */
    @BindView(R.id.textViewLoading)
    TextView mTxtLoading;

    @BindView(R.id.progressBarGames)
    ProgressBar mPbGames;

    @BindView(R.id.scrollProgressBar)
    ScrollView mScrollProgressBar;

    @BindView(R.id.gameMenu)
    ScrollView mScrollGameView;

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
        changeMenuVisibility(true);
        currentUser = firebaseAuth.getCurrentUser();
        userId = currentUser.getUid();
    }

    /**
     * ChangeMenyVisbility
     *
     * @param visibility change visibility of Scrolls
     */
    private void changeMenuVisibility(Boolean visibility) {
        mScrollProgressBar.setVisibility(visibility ? View.GONE : View.VISIBLE);
        mScrollGameView.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    /**
     * On Button Play Click
     */
    @OnClick(R.id.buttonPlay)
    public void onButtonPlayOnClick() {

    }

    /**
     * On Button Ranking Click
     */
    @OnClick(R.id.buttonRanking)
    public void onButtonRankingOnClick() {

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

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResume() {
        super.onResume();
        changeMenuVisibility(true);
    }
}
