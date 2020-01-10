package com.machucapps.tictactoe.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.machucapps.tictactoe.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * RegistroActivity Class
 */
public class RegistroActivity extends AppCompatActivity implements OnCompleteListener {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore mFirebaseFirestore;

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
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        ButterKnife.bind(this);
        mFirebaseAuth = FirebaseAuth.getInstance();
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

    private void createUser(String name, String email, String password) {
        setFormVisibilityGone(false);
        mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, this);
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
     * Update UI
     *
     * @param user - FirebaseUser
     */
    private void updateUI(FirebaseUser user) {
        if (null != user) {
            startActivity(new Intent(this, FindGameActivity.class));
            finish();
        } else {
            setFormVisibilityGone(true);
            mEtPassword.setError("Alguno de los campos es incorrecto");
            mEtPassword.requestFocus();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param task
     */
    @Override
    public void onComplete(@NonNull Task task) {
        if (task.isSuccessful()) {
            FirebaseUser user = mFirebaseAuth.getCurrentUser();

        } else {
            Toast.makeText(this, "Se ha producido un error", Toast.LENGTH_LONG).show();
            updateUI(null);
        }
    }

    /**
     * Set visibility of views
     * If mail and password are not empty - true, else - otherwise
     *
     * @param visibility - boolean
     */
    private void setFormVisibilityGone(boolean visibility) {
        mFormSignUp.setVisibility(visibility ? View.GONE : View.VISIBLE);
        mPbSignUp.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }
}
