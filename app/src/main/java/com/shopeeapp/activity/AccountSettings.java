package com.shopeeapp.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.shopeeapp.R;
import com.shopeeapp.utilities.AccountSessionManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AccountSettings extends AppCompatActivity {
    @BindView(R.id.menuLogin)
    LinearLayout menuLogin;
    @BindView(R.id.menuEmailVerified)
    LinearLayout menuEmailVerified;
    @BindView(R.id.menuForgotPassword)
    LinearLayout menuForgotPassword;
    @BindView(R.id.menuUpdateAccount)
    LinearLayout menuUpdateAccount;
    @BindView(R.id.menuLogout)
    LinearLayout menuLogout;

    private boolean checkVerifyClicked = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_settings);
        ButterKnife.bind(this);
        LinearLayout login = findViewById(R.id.menuLogin);
        LinearLayout logout = findViewById(R.id.menuLogout);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLoginClick(view);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLogoutClick(view);
            }
        });
        findViewById(R.id.btnBack).setOnClickListener(view -> finish());
    }

    private void setLoginClick(View view) {//Chuyển hướng đến LogIn activity khi nhấn vào tùy chọn đăng nhập.
        Intent intent = new Intent(this, LogIn.class);
        startActivity(intent);
    }

    private void setLogoutClick(View view) {//Hiển thị hộp thoại xác nhận khi người dùng muốn đăng xuất, và xử lý việc đăng xuất.
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có muốn đăng xuất khỏi ứng dụng?")
                .setPositiveButton("Có", (dialogInterface, i) -> {
                    // Clear all relevant shared preferences data
                    SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();

                    // Update UI after logout
                    setLoginVisible();
                    Toast.makeText(this, "Đã đăng xuất thành công", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Không", null)
                .setIcon(R.drawable.shutdown)
                .show();
    }


    private void setLogoutVisible() {
        menuLogin.setVisibility(View.GONE);
        menuLogout.setVisibility(View.VISIBLE);
        menuUpdateAccount.setVisibility(View.VISIBLE);
    }
    private void setLoginVisible() {
        menuLogout.setVisibility(View.GONE);
        menuLogin.setVisibility(View.VISIBLE);
        menuUpdateAccount.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }




    private void updateAccountInfo(View view) {
        Intent intent = new Intent(this, AccountInfoActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Remove Firebase activity result handling related to verification and password reset
    }
}
