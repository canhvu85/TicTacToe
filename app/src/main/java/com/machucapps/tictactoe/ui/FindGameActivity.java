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
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.machucapps.tictactoe.R;
import com.machucapps.tictactoe.base.BaseActivity;
import com.machucapps.tictactoe.model.Game;
import com.machucapps.tictactoe.utils.Constants;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * FindGameActivity Class
 */
public class FindGameActivity extends BaseActivity implements OnCompleteListener<QuerySnapshot>, OnSuccessListener<Void>, OnFailureListener {

    private FirebaseUser currentUser;
    private String userId;
    private String gameId;
    private ListenerRegistration mListener = null;

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
        changeMenuVisibility(false);
        lookForFreeGame();

    }

    /**
     * Look for a game where some player is waiting for another player
     */
    private void lookForFreeGame() {
        changeMenuVisibility(false);
        mTxtLoading.setText("Buscando una partida empezada...");
        db.collection("jugadas").whereEqualTo("JugadorDosId", "").get().addOnCompleteListener(this);

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

    /**
     * {@inheritDoc}
     *
     * @param task
     */
    @Override
    public void onComplete(@NonNull Task<QuerySnapshot> task) {
        if (task.getResult().size() == 0) {
            mTxtLoading.setText("Creando una partida nueva...");
            Game newGame = new Game(userId);
            db.collection("jugadas").add(newGame).addOnSuccessListener(documentReference -> {
                gameId = documentReference.getId();
                waitForOtherPlayer();
            }).addOnFailureListener(this);

        } else {
            DocumentSnapshot docJugada = task.getResult().getDocuments().get(0);
            gameId = docJugada.getId();
            Game game = docJugada.toObject(Game.class);
            game.setPlayerTwoId(userId);
            db.collection("jugadas").document(gameId).set(game).addOnSuccessListener(this).addOnFailureListener(this);
        }

    }

    private void waitForOtherPlayer() {
        mTxtLoading.setText("Esperando a otro jugador...");
        mListener = db.collection("jugadas").document(gameId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.get("jugadorDosId") != "") {
                    mTxtLoading.setText("¡Jugador Encontrado!");
                    final Handler handler = new Handler();
                    final Runnable run = () -> startGame();
                    handler.postDelayed(run, 1500);
                }
            }
        });

    }

    /**
     * {@inheritDoc}
     *
     * @param aVoid
     */
    @Override
    public void onSuccess(Void aVoid) {
        startGame();
    }

    private void startGame() {
        if (mListener != null) {
            mListener.remove();
        }
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(Constants.EXTRA_GAME_ID, gameId);
        startActivity(intent);
    }

    /**
     * {@inheritDoc}
     *
     * @param e
     */
    @Override
    public void onFailure(@NonNull Exception e) {
        changeMenuVisibility(true);
        Toast.makeText(this, "Se ha producido un error", Toast.LENGTH_LONG).show();
    }
}
