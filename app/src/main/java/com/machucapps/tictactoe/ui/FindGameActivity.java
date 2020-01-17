package com.machucapps.tictactoe.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.machucapps.tictactoe.R;
import com.machucapps.tictactoe.base.BaseActivity;
import com.machucapps.tictactoe.model.Game;
import com.machucapps.tictactoe.utils.Constants;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * FindGameActivity Class
 */
public class FindGameActivity extends BaseActivity implements OnFailureListener {

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
    @BindView(R.id.lottieAnimation)
    LottieAnimationView mLottieView;

    /**
     * Variables
     */
    private FirebaseUser currentUser;
    private String userId = "", gameId = "";
    private ListenerRegistration mListener = null;

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
        mTxtLoading.setText(R.string.label_charging);
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
        changeMenuVisibility(false);
        lookForFreeGame();

    }

    /**
     * Look for a game where some player is waiting for another player
     */
    private void lookForFreeGame() {
        mTxtLoading.setText(getString(R.string.label_looking_for_new_game));
        mLottieView.playAnimation();

        db.collection(Constants.FIREBASE_COLLECTION_JUGADAS).whereEqualTo(Constants.FIREBASE_FIELD_PLAYER_TWO_ID, "").get().addOnCompleteListener(task -> {
            if (task.getResult().size() == 0) {

                createNewGame();

            } else {
                boolean found = false;

                for (DocumentSnapshot docJugada : task.getResult().getDocuments()) {
                    if (!docJugada.get(Constants.FIREBASE_FIELD_PLAYER_ONE_ID).equals(userId)) {
                        found = true;
                        gameId = docJugada.getId();
                        Game game = docJugada.toObject(Game.class);
                        game.setPlayerTwoId(userId);
                        db.collection(Constants.FIREBASE_COLLECTION_JUGADAS).document(gameId).set(game).addOnSuccessListener(aVoid -> startGame()).addOnFailureListener(this);
                    }
                    break;
                }
                if (!found) {
                    createNewGame();
                }
            }
        });

    }

    /**
     * Create new game
     */
    private void createNewGame() {
        mTxtLoading.setText(getString(R.string.label_creating_new_game));
        Game newGame = new Game(userId);
        db.collection(Constants.FIREBASE_COLLECTION_JUGADAS).add(newGame).addOnSuccessListener(documentReference -> {
            gameId = documentReference.getId();
            waitForOtherPlayer();
        }).addOnFailureListener(this);
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
        if (!gameId.equals("")) {
            changeMenuVisibility(false);
            waitForOtherPlayer();
        } else {
            changeMenuVisibility(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (mListener != null) {
            mListener.remove();
        }
        if (!gameId.equals("")) {
            db.collection(Constants.FIREBASE_COLLECTION_JUGADAS).document(gameId).delete().addOnCompleteListener(task -> gameId = "");
        }
    }

    /**
     * Wait for other player
     */
    private void waitForOtherPlayer() {
        mTxtLoading.setText(getString(R.string.label_waiting_player));
        mListener = db.collection(Constants.FIREBASE_COLLECTION_JUGADAS).document(gameId).addSnapshotListener((documentSnapshot, e) -> {
            if (!documentSnapshot.get(Constants.FIREBASE_FIELD_PLAYER_TWO_ID).equals("")) {
                mLottieView.setRepeatCount(0);
                mLottieView.setAnimation("checked_animation.json");
                mLottieView.playAnimation();
                mTxtLoading.setText(getString(R.string.label_player_found));
                final Handler handler = new Handler();
                final Runnable run = () -> startGame();
                handler.postDelayed(run, 1500);
            }
        });

    }

    /**
     * Start Game
     */
    private void startGame() {
        if (mListener != null) {
            mListener.remove();
        }
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(Constants.EXTRA_GAME_ID, gameId);

        final Handler handler = new Handler();
        final Runnable run = () -> startActivity(intent);
        handler.postDelayed(run, 1500);
        gameId = "";
    }

    /**
     * {@inheritDoc}
     *
     * @param e
     */
    @Override
    public void onFailure(@NonNull Exception e) {
        changeMenuVisibility(true);
        Toast.makeText(this, getString(R.string.label_error), Toast.LENGTH_LONG).show();
    }
}
