package com.machucapps.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Login Activity Class
 */
public class LoginActivity extends AppCompatActivity {

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
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }


    /**
     * Set visibility of views
     * If mail and password are not empty - true, else - otherwise
     *
     * @param visibility - boolean
     */
    private void setFormVisibilityGone(boolean visibility) {
        mFormLogin.setVisibility(visibility ? View.GONE : View.VISIBLE);
        mPbLogin.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    /**
     * On Login Button Click
     */
    @OnClick(R.id.btn_registro)
    public void onLoginButtonClick() {
        String email = mEtMail.getText().toString();
        String password = mEtPassword.getText().toString();

        setFormVisibilityGone(!email.isEmpty() && !password.isEmpty());
    }

    /**
     * On Sign Up Button Click
     */
    @OnClick(R.id.btn_registro)
    public void onSignUpButtonClick() {
        startActivity(new Intent(this, RegistroActivity.class));
        finish();
    }
}
