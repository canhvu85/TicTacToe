package com.machucapps.tictactoe.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ListenerRegistration;
import com.machucapps.tictactoe.R;
import com.machucapps.tictactoe.base.BaseActivity;
import com.machucapps.tictactoe.model.Game;
import com.machucapps.tictactoe.model.User;
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

    /**
     * Variables
     */
    String userId, gameId, playerOneName = "", playerTwoName = "", winnerId = "", playerName = "";
    Game game;
    ListenerRegistration mListener = null;
    FirebaseUser mFirebaseUser;
    User userPlayerOne, userPlayerTwo;

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public int getLayoutID() {
        return R.layout.activity_game;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStart() {
        super.onStart();
        gameListener();
    }

    /**
     * Game Listener
     */
    private void gameListener() {
        mListener = db.collection(Constants.FIREBASE_COLLECTION_JUGADAS).document(gameId).addSnapshotListener(this, (documentSnapshot, e) -> {
            if (e != null) {
                Toast.makeText(this, getString(R.string.label_error), Toast.LENGTH_LONG).show();
                return;
            }

            String source = documentSnapshot != null && documentSnapshot.getMetadata().hasPendingWrites() ? Constants.LOCAL : Constants.SERVER;

            if (documentSnapshot.exists() && source.equals(Constants.SERVER)) {
                game = documentSnapshot.toObject(Game.class);
                if (playerOneName.isEmpty() || playerTwoName.isEmpty()) {
                    getPlayersNames();

                }

                updateUi();
            }
            refreshUserUI();
        });
    }

    /**
     * Update UI
     */
    private void updateUi() {
        for (int i = 0; i < mListViews.size(); i++) {
            int move = game.getSelectedCell().get(i);

            ImageView actualMove = mListViews.get(i);

            if (move == 0) {
                actualMove.setImageResource(R.drawable.ic_empty_square);
            } else if (move == 1) {
                actualMove.setImageResource(R.drawable.ic_player_one);
            } else {
                actualMove.setImageResource(R.drawable.ic_player_two);
            }

        }
    }

    /**
     * Refresh User UI
     */
    private void refreshUserUI() {
        if (game.getPlayerOneTurn()) {
            mTvPlayerOne.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            mTvPlayerTwo.setTextColor(ContextCompat.getColor(this, R.color.colorGrey));
        } else {
            mTvPlayerOne.setTextColor(ContextCompat.getColor(this, R.color.colorGrey));
            mTvPlayerTwo.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        }

        if (!game.getWinnerId().isEmpty()) {
            winnerId = game.getWinnerId();
            showGameOverDialog();
        }
    }

    /**
     * Get Players Names
     */
    private void getPlayersNames() {


        db.collection(Constants.FIREBASE_COLLECTION_USER).document(game.getPlayerOneId()).get().addOnSuccessListener(documentSnapshot -> {
            userPlayerOne = documentSnapshot.toObject(User.class);
            playerOneName = documentSnapshot.get(Constants.FIREBASE_FIELD_PLAYER_NAME).toString();
            mTvPlayerOne.setText(playerOneName);

            if (game.getPlayerOneId().equals(userId)) {
                playerName = playerOneName;
            }
        });

        db.collection(Constants.FIREBASE_COLLECTION_USER).document(game.getPlayerTwoId()).get().addOnSuccessListener(documentSnapshot -> {
            userPlayerTwo = documentSnapshot.toObject(User.class);
            playerTwoName = documentSnapshot.get(Constants.FIREBASE_FIELD_PLAYER_NAME).toString();
            mTvPlayerTwo.setText(playerTwoName);

            if (game.getPlayerTwoId().equals(userId)) {
                playerName = playerTwoName;
            }
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

    /**
     * Init Game
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (mListener != null) {
            mListener.remove();
        }

    }

    /**
     * OnCell Click
     *
     * @param view
     */
    public void onCellClicked(View view) {
        if (!game.getWinnerId().isEmpty()) {
            Toast.makeText(this, getString(R.string.label_game_finished), Toast.LENGTH_LONG).show();
        } else {
            if (game.getPlayerOneTurn() && game.getPlayerOneId().equals(userId)) {
                refreshGame(view.getTag().toString());
            } else if (!game.getPlayerOneTurn() && game.getPlayerTwoId().equals(userId)) {
                refreshGame(view.getTag().toString());
            } else {
                Toast.makeText(this, getString(R.string.label_wait_your_turn), Toast.LENGTH_LONG).show();
            }
        }

    }

    /**
     * Refresh Game
     *
     * @param tag
     */
    private void refreshGame(String tag) {
        int pos = Integer.parseInt(tag);
        if (game.getSelectedCell().get(pos) != 0) {
            Toast.makeText(this, getString(R.string.label_select_empty_cell), Toast.LENGTH_LONG).show();
        } else {
            if (game.getPlayerOneTurn()) {
                mListViews.get(pos).setImageResource(R.drawable.ic_player_one);
                game.getSelectedCell().set(pos, 1);
            } else {
                mListViews.get(pos).setImageResource(R.drawable.ic_player_two);
                game.getSelectedCell().set(pos, 2);

            }
            if (checkSolution()) {
                game.setWinnerId(userId);

            } else if (checkTie()) {
                game.setWinnerId(Constants.TIE);

            } else {
                changeTurn();
            }
            db.collection(Constants.FIREBASE_COLLECTION_JUGADAS).document(gameId).set(game).addOnSuccessListener(aVoid -> {

            }).addOnFailureListener(e -> {
                Log.w("Error", getString(R.string.label_error));
            });
        }
    }

    /**
     * Change player turn
     */
    private void changeTurn() {
        game.setPlayerOneTurn(!game.getPlayerOneTurn());
    }

    /**
     * Check a tie between players
     *
     * @return true is tie - false ottherwise
     */
    private boolean checkTie() {

        boolean emptySquare = false;
        boolean gameFinished = false;

        for (int i = 0; i < mListViews.size(); i++) {
            if (game.getSelectedCell().get(i) == 0) {
                emptySquare = true;
                break;
            }
        }

        if (!emptySquare) {
            gameFinished = true;
        }

        return gameFinished;
    }

    /**
     * Check Solution
     *
     * @return true game is finished - false otherwise
     */
    private boolean checkSolution() {

        boolean gameFinished = false;


        List<Integer> selectedCells = game.getSelectedCell();
        if (selectedCells.get(0).equals(selectedCells.get(1)) && selectedCells.get(1).equals(selectedCells.get(2)) && selectedCells.get(2) != 0) {
            gameFinished = true;
        } else if (selectedCells.get(3).equals(selectedCells.get(4)) && selectedCells.get(4).equals(selectedCells.get(5)) && selectedCells.get(5) != 0) {
            gameFinished = true;
        } else if (selectedCells.get(6).equals(selectedCells.get(7)) && selectedCells.get(7).equals(selectedCells.get(8)) && selectedCells.get(8) != 0) {
            gameFinished = true;

        } else if (selectedCells.get(0).equals(selectedCells.get(3)) && selectedCells.get(3).equals(selectedCells.get(6)) && selectedCells.get(6) != 0) {
            gameFinished = true;

        } else if (selectedCells.get(1).equals(selectedCells.get(4)) && selectedCells.get(4).equals(selectedCells.get(7)) && selectedCells.get(7) != 0) {
            gameFinished = true;

        } else if (selectedCells.get(2).equals(selectedCells.get(5)) && selectedCells.get(5).equals(selectedCells.get(8)) && selectedCells.get(8) != 0) {
            gameFinished = true;

        } else if (selectedCells.get(0).equals(selectedCells.get(4)) && selectedCells.get(4).equals(selectedCells.get(8)) && selectedCells.get(8) != 0) {
            gameFinished = true;

        } else if (selectedCells.get(2).equals(selectedCells.get(4)) && selectedCells.get(4).equals(selectedCells.get(6)) && selectedCells.get(6) != 0) {
            gameFinished = true;


        }

        return gameFinished;
    }

    /**
     * Show GameOver Dialog
     */
    private void showGameOverDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.label_game_over));
        View view = getLayoutInflater().inflate(R.layout.dialog_layout, null);

        TextView tvPoints = view.findViewById(R.id.tvPoints);
        TextView tvInfo = view.findViewById(R.id.tvInfo);
        LottieAnimationView gameOverView = view.findViewById(R.id.lottieAnimation);


        builder.setView(view);
        builder.setCancelable(false);


        if (winnerId.equals(Constants.TIE)) {
            tvInfo.setText(getString(R.string.label_player_tie, playerName));
            tvPoints.setText(getString(R.string.label_one_point));
            setUserPoints(1);
        } else if (winnerId.equals(userId)) {
            tvInfo.setText(getString(R.string.label_player_winner, playerName));
            tvPoints.setText(getString(R.string.label_three_point));
            setUserPoints(3);
        } else {
            tvInfo.setText(getString(R.string.label_player_looser, playerName));
            tvPoints.setText(getString(R.string.label_zero_point));
            gameOverView.setAnimation("thumbs_down_animation.json");
            setUserPoints(0);
        }
        gameOverView.playAnimation();
        builder.setPositiveButton(getString(R.string.label_ok), (dialogInterface, i) -> {
            finish();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Set User Points
     *
     * @param points
     */
    private void setUserPoints(Integer points) {
        User player;
        if (playerOneName.equals(userPlayerOne.getName())) {
            userPlayerOne.setPoints(userPlayerOne.getPoints() + points);
            userPlayerOne.setGamesPlayed(userPlayerOne.getGamesPlayed() + 1);
            player = userPlayerOne;
        } else {
            userPlayerTwo.setPoints(userPlayerTwo.getPoints() + points);
            userPlayerTwo.setGamesPlayed(userPlayerTwo.getGamesPlayed() + 1);
            player = userPlayerTwo;
        }

        db.collection(Constants.FIREBASE_COLLECTION_USER).document(userId).set(player).addOnSuccessListener(aVoid -> {

        }).addOnFailureListener(e -> {
        });

    }
}
