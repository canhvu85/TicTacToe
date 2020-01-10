package com.machucapps.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * RegistroActivity Class
 */
public class RegistroActivity extends AppCompatActivity {

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
    ScrollView mFormLogin;

    @BindView(R.id.pb_registro)
    ProgressBar mPbLogin;

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
        }
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


}
