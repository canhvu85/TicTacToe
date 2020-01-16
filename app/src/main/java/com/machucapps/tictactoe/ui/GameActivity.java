package com.machucapps.tictactoe.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ListenerRegistration;
import com.machucapps.tictactoe.R;
import com.machucapps.tictactoe.base.BaseActivity;
import com.machucapps.tictactoe.model.Game;
import com.machucapps.tictactoe.utils.Constants;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;

/**
 * GameActivity Class
 */
public class GameActivity extends BaseActivity {
    /**
     * BindView's
     */
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindViews({R.id.iv_zero, R.id.iv_one, R.id.iv_two, R.id.iv_three, R.id.iv_four, R.id.iv_five, R.id.iv_six, R.id.iv_seven, R.id.iv_eight})
    List<ImageView> mListViews;

    @BindView(R.id.tv_player_one)
    TextView mTvPlayerOne;

    @BindView(R.id.tv_player_two)
    TextView mTvPlayerTwo;

    String userId, gameId, playerOneName = "", playerTwoName = "";
    Game game;
    ListenerRegistration mListener = null;
    FirebaseUser mFirebaseUser;

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public int getLayoutID() {
        return R.layout.activity_game;
    }

    @Override
    protected void onStart() {
        super.onStart();
        gameListener();
    }

    private void gameListener(){
        mListener = db.collection("jugadas").document(gameId).addSnapshotListener(this, (documentSnapshot, e) -> {
            if (e != null) {
                Toast.makeText(this, "Error con la partida", Toast.LENGTH_LONG).show();
                return;
            }

            String source = documentSnapshot != null && documentSnapshot.getMetadata().hasPendingWrites() ? "Local" : "Server";

            if (documentSnapshot.exists() && source.equals("Server")) {
                game = documentSnapshot.toObject(Game.class);
                if (playerOneName.isEmpty() || playerTwoName.isEmpty()) {
                    getPlayersNames();
                }
            }
        });
    }

    private void getPlayersNames() {
        db.collection("users").document(game.getPlayeOneId()).get().addOnSuccessListener(documentSnapshot -> {
            playerOneName = documentSnapshot.get("name").toString();
            mTvPlayerOne.setText(playerOneName);
        });

        db.collection("users").document(game.getPlayerTwoId()).get().addOnSuccessListener(documentSnapshot -> {
            playerTwoName = documentSnapshot.get("name").toString();
            mTvPlayerTwo.setText(playerTwoName);
        });
    }

    /**
     * {@inheritDoc}
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(mToolbar);
        initGame();

    }

    private void initGame() {
        mFirebaseUser = firebaseAuth.getCurrentUser();
        userId = mFirebaseUser.getUid();

        Bundle extras = getIntent().getExtras();
        gameId = extras.getString(Constants.EXTRA_GAME_ID);

    }

    /**
     * On FAB's Button Click
     */
    @OnClick(R.id.fab)
    public void OnFabButtonClick() {

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

    @Override
    protected void onStop() {
        super.onStop();
        if (mListener != null) {
            mListener.remove();
        }

    }
}
