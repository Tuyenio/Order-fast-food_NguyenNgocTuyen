package com.shopeeapp.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.shopeeapp.R;
import com.shopeeapp.activity.LogIn;
import com.shopeeapp.utilities.AccountSessionManager;

public class DashBoard extends AppCompatActivity {

    MaterialCardView user, product, category, store, discount, statis, productImg, payment;
    ImageView btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        user = findViewById(R.id.frameUser);
        product = findViewById(R.id.frameProduct);
        category = findViewById(R.id.frameCategory);

        discount = findViewById(R.id.frameDiscount);
        statis = findViewById(R.id.frameStatis);

        payment = findViewById(R.id.framePayment);
        btnLogout = findViewById(R.id.btnLogout);
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashBoard.this, ShowAdUserActivity.class);
                startActivity(intent);
            }
        });

        product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashBoard.this, ShowAdProductActivity.class);
                startActivity(intent);
            }
        });

        discount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashBoard.this, ShowAdDiscountActivity.class);
                startActivity(intent);
            }
        });

        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashBoard.this, ShowAdProTypeActivity.class);
                startActivity(intent);
            }
        });


        statis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashBoard.this, StaticActivity.class);
                startActivity(intent);
            }
        });


        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashBoard.this, ShowPaymentActivity.class);
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = new AlertDialog.Builder(DashBoard.this)
                        .setTitle("Đăng xuất")
                        .setMessage("Bạn có muốn đăng xuất khỏi ứng dụng?")
                        .setPositiveButton("Có", (dialogInterface, i) -> {
                            //AppUtilities.clearSession(view.getContext());
                            logout();
                            Toast.makeText(DashBoard.this, "Đã đăng xuất thành công", Toast.LENGTH_SHORT).show();
                            Intent login = new Intent(DashBoard.this, LogIn.class);
                            startActivity(login);
                            finish();
                        })
                        .setNegativeButton("Không", null)
                        .setIcon(R.drawable.shutdown)
                        .show();
            }
        });

    }
    public void logout() {
        FirebaseAuth.getInstance().signOut();
        AccountSessionManager.account = null;
        AccountSessionManager.user = null;
        deleteSharedPreferences("admin");

    }

}