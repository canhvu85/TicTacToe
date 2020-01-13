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
 * RegistroActivity Class
 */
public class RegistroActivity extends BaseActivity implements OnCompleteListener {

    /**
     * BindView's
     */
    @BindView(R.id.et_name)
    EditText mEtName;

    @BindView(R.id.et_mail)
    EditText mEtMail;

    @BindView(R.id.et_password)
    EditText mEtPassword;

    @BindView(R.id.form_registro)
    ScrollView mFormSignUp;

    @BindView(R.id.pb_registro)
    ProgressBar mPbSignUp;

    /**
     * {@inheritDoc}
     *
     * @return
     */
    @Override
    public int getLayoutID() {
        return R.layout.activity_registro;
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
            Toast.makeText(this, "Se ha producido un error", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Set visibility of progress bar
     *
     * @param visibility - boolean
     */
    private void setPbVisibility(boolean visibility) {
        mPbSignUp.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    /**
     * On Sign Up Button Click
     */
    @OnClick(R.id.btn_registro)
    public void onSignUpButtonClick() {
        if (isFieldEmpty(mEtName) && isFieldEmpty(mEtMail) && isFieldEmpty(mEtPassword)) {
            String name = mEtName.getText().toString();
            String email = mEtMail.getText().toString();
            String password = mEtPassword.getText().toString();

            createUser(name, email, password);
        } else {
            Toast.makeText(this, "Rellena todos los cambios", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Create User
     *
     * @param name
     * @param email
     * @param password
     */
    private void createUser(String name, String email, String password) {
        setPbVisibility(true);
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, this);
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
     * {@inheritDoc}
     *
     * @param task
     */
    @Override
    public void onComplete(@NonNull Task task) {
        setPbVisibility(false);
        if (task.isSuccessful()) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            updateUI(user, true);
        } else {
            Toast.makeText(this, "Se ha producido un error", Toast.LENGTH_LONG).show();
        }
    }

}
