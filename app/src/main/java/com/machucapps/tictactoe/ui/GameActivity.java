package com.machucapps.tictactoe.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

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

    private void gameListener() {
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

                updateUi();
            }
            changeNameColor();
        });
    }

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

    private void changeNameColor() {
        if (game.getPlayerOneTurn()) {
            mTvPlayerOne.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            mTvPlayerTwo.setTextColor(ContextCompat.getColor(this, R.color.colorGrey));
        } else {
            mTvPlayerOne.setTextColor(ContextCompat.getColor(this, R.color.colorGrey));
            mTvPlayerTwo.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));
        }
    }

    private void getPlayersNames() {
        db.collection("Users").document(game.getPlayerOneId()).get().addOnSuccessListener(documentSnapshot -> {
            playerOneName = documentSnapshot.get("name").toString();
            mTvPlayerOne.setText(playerOneName);
        });

        db.collection("Users").document(game.getPlayerTwoId()).get().addOnSuccessListener(documentSnapshot -> {
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

    public void onSquareClicked(View view) {
        if (!game.getWinnerId().isEmpty()) {
            Toast.makeText(this, "Partida finalizada", Toast.LENGTH_LONG).show();
        } else {
            if (game.getPlayerOneTurn() && game.getPlayerOneId().equals(userId)) {
                refreshGame(view.getTag().toString());
            } else if (!game.getPlayerOneTurn() && game.getPlayerTwoId().equals(userId)) {
                refreshGame(view.getTag().toString());
            } else {
                Toast.makeText(this, "Espera a tu turno", Toast.LENGTH_LONG).show();
            }
        }

    }

    private void refreshGame(String tag) {
        int pos = Integer.parseInt(tag);
        if (game.getSelectedCell().get(pos) != 0) {
            Toast.makeText(this, "Selecciones una casilla libre", Toast.LENGTH_LONG).show();
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
                game.setWinnerId("EMPATE");
            } else {
                changeTurn();
            }
            db.collection("jugadas").document(gameId).set(game).addOnSuccessListener(aVoid -> {

            }).addOnFailureListener(e -> {
                Log.w("Error", "Error al guardar la jugada");
            });
        }
    }

    private void changeTurn() {
        game.setPlayerOneTurn(!game.getPlayerOneTurn());
    }


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
}
