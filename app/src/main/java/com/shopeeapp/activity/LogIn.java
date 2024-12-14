package com.shopeeapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.shopeeapp.MainActivity;
import com.shopeeapp.R;
import com.shopeeapp.admin.DashBoard;
import com.shopeeapp.dbhelper.AccountDbHelper;
import com.shopeeapp.dbhelper.UserDbHelper;
import com.shopeeapp.entity.Account;
import com.shopeeapp.entity.User;
import com.shopeeapp.utilities.AccountSessionManager;
import com.shopeeapp.utilities.AppUtilities;

public class LogIn extends AppCompatActivity {
    Button btnLogin;
    TextInputEditText txtEmail;
    TextInputEditText txtPassword;
    TextView txtSignUp;
    Button btnForgotPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        addControl();
        addEvent();
    }

    private void addControl() {
        btnLogin = findViewById(R.id.btnLogIn);
        txtEmail = findViewById(R.id.email);
        txtPassword = findViewById(R.id.txtPassword);
        txtSignUp = findViewById(R.id.txtSignUp);
        btnForgotPassword = findViewById(R.id.btnForgotPassword);
    }

    private void addEvent() {
        findViewById(R.id.btnBack).setOnClickListener(view -> finish());
        btnLogin.setOnClickListener(this::setLogin);
        txtSignUp.setOnClickListener(this::setSignUp);

    }

    private void setLogin(View view) {
        String email = txtEmail.getText().toString();
        String password = txtPassword.getText().toString();

        // Check if the credentials are for the admin
        if (email.equals("admin") && password.equals("123")) {
            AccountSessionManager.account = new Account(1, "admin", "admin", "123", 1, "");
            AccountSessionManager.user = new User(1, 1, "admin", null, null, null, null, null, "");
            SharedPreferences sharedPreferences = getSharedPreferences("admin", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Admin", "true");
            editor.commit();
            Intent adminActivity = new Intent(this, DashBoard.class);
            startActivity(adminActivity);
            finish();
            return;
        }

        // Check user credentials from SQLite database
        AccountDbHelper accountDbHelper = new AccountDbHelper(this);
        Account account = accountDbHelper.getAccountByEmail(email);

        if (account != null && account.getPassword().equals(password)) {
            UserDbHelper userDbHelper = new UserDbHelper(this);
            User user = userDbHelper.getUserByAccountId(account.getId());

            AccountSessionManager.account = account;
            AccountSessionManager.user = user;

            SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("accountId", account.getId());
            editor.putString("username", account.getUsername());
            editor.putString("email", account.getEmail());
            editor.putInt("roleId", account.getRoleID());
            editor.putString("fullName", user.getFullname());
            editor.commit();

            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
            if (account.getRoleID() == 1) {
                Intent adminActivity = new Intent(this, DashBoard.class);
                startActivity(adminActivity);
            } else if (account.getRoleID() == 2) {
                Intent mainActivity = new Intent(this, MainActivity.class);
                startActivity(mainActivity);
            } else {
                Toast.makeText(this, "Tài khoản không hợp lệ!", Toast.LENGTH_SHORT).show();
            }
            finish();
        } else {
            Toast.makeText(this, "Thông tin đăng nhập không chính xác!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setSignUp(View view) {
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
        finish();
    }

}
