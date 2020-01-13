package com.machucapps.tictactoe.ui;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.machucapps.tictactoe.R;
import com.machucapps.tictactoe.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Login Activity Class
 */
public class LoginActivity extends BaseActivity implements OnCompleteListener {


    /**
     * BindView's
     */
    @BindView(R.id.et_mail)
    EditText mEtMail;

    @BindView(R.id.et_password)
    EditText mEtPassword;

    @BindView(R.id.form_login)
    ScrollView mFormLogin;

    @BindView(R.id.pb_login)
    ProgressBar mPbLogin;

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public int getLayoutID() {
        return R.layout.activity_login;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        updateUI(currentUser, false);
    }

    /**
     * {@inheritDoc}
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * {@inheritDoc}
     *
     * @param firebaseUser - Firebase User
     */
    @Override
    public void updateUI(FirebaseUser firebaseUser, Boolean isSignUp) {
        setPbVisibility(true);
        if (null != firebaseUser) {
            setPbVisibility(false);
            startActivity(new Intent(this, FindGameActivity.class));
            finish();
        } else {
            setPbVisibility(false);
            if (isSignUp) {
                Toast.makeText(this, "Se ha producido un error", Toast.LENGTH_LONG).show();
            }

        }

    }

    /**
     * Set visibility of progress bar
     *
     * @param visibility - boolean
     */
    private void setPbVisibility(boolean visibility) {
        mPbLogin.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    /**
     * On Login Button Click
     */
    @OnClick(R.id.btn_login)
    public void onLoginButtonClick() {
        String email = mEtMail.getText().toString();
        String password = mEtPassword.getText().toString();

        if (isFieldEmpty(mEtMail) && isFieldEmpty(mEtPassword)) {
            loginUser(email, password);
        }
    }

    /**
     * On Sign Up Button Click
     */
    @OnClick(R.id.btn_registro)
    public void onSignUpButtonClick() {
        startActivity(new Intent(this, RegistroActivity.class));
        finish();
    }

    /**
     * Check if field has text or is empty
     *
     * @param field - view
     * @return true is empty - false otherwise
     */
    private boolean isFieldEmpty(EditText field) {
        return !field.getText().toString().isEmpty();
    }

    /**
     * Log in user
     *
     * @param email
     * @param password
     */
    private void loginUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this);
    }

    /**
     * {@inheritDoc}
     *
     * @param task
     */
    @Override
    public void onComplete(@NonNull Task task) {
        if (task.isSuccessful()) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            updateUI(user, true);
        } else {
            Toast.makeText(this, "Se ha producido un error", Toast.LENGTH_LONG).show();
        }
    }


}
