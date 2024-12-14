package com.shopeeapp.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.shopeeapp.R;
import com.shopeeapp.dbhelper.UserDbHelper;
import com.shopeeapp.utilities.AppUtilities;
import com.shopeeapp.utilities.ImageConverter;

import org.jetbrains.annotations.NotNull;
//một activity trong ứng dụng Android để quản lý thông tin tài khoản người dùng,
// như tên, địa chỉ, số điện thoại, email và ảnh đại diện
public class AccountInfoActivity extends AppCompatActivity {
    private static final int IMAGE_PICK_CODE = 1000;

    ImageButton btnBack;
    ImageView imgAvt;
    Button btnTakePhoto;
    Button btnChoosePhoto;
    TextInputEditText txtFullName;
    TextInputEditText txtAddress;
    TextInputEditText txtEmail;
    TextInputEditText txtPhone;
    Button btnUpdate;

    Uri fileImage;
    String urlImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);

        if (Build.VERSION.SDK_INT >= 23) {
            if (!AppUtilities.checkPermission(this))
                AppUtilities.requestPermission(this);
        }

        addControl();
        addEvent();

        setInfo();
        setAvatar();
    }

    private void addControl() {
        btnBack = findViewById(R.id.btnBack);
        imgAvt = findViewById(R.id.imgAvt);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnChoosePhoto = findViewById(R.id.btnChoosePhoto);
        txtFullName = findViewById(R.id.txtFullName);
        txtAddress = findViewById(R.id.txtAddress);
        txtEmail = findViewById(R.id.txtEmail);
        txtPhone = findViewById(R.id.txtPhone);
        btnUpdate = findViewById(R.id.btnUpdate);
    }

    private void addEvent() {
        btnBack.setOnClickListener(view -> finish());
        btnTakePhoto.setOnClickListener(AppUtilities::setTakePhoto);
        btnChoosePhoto.setOnClickListener(AppUtilities::setChoosePhoto);
        btnUpdate.setOnClickListener(this::setUpdate);
    }

    private void setInfo() {
        // Replace with your logic to set user information from your session manager or database
        // For example:
        // if (user != null) {
        //     txtFullName.setText(user.getFullname());
        //     txtAddress.setText(user.getAddress());
        //     txtPhone.setText(user.getPhone());
        //     txtEmail.setText(user.getEmail());
        // }
    }

    private void setAvatar() {
        // Replace with your logic to load user avatar
        // For example:
        // if (user != null) {
        //     Glide.with(this).load(user.getAvatar()).into(imgAvt);
        // }
    }

    private void setUpdate(View view) {
        String fullName = txtFullName.getText().toString();
        String address = txtAddress.getText().toString();
        String phone = txtPhone.getText().toString();
        if (validate(fullName, phone, address)) {
            // Replace with your logic to update user information in the database or session manager
            // For example:
            // if (user != null) {
            //     user.setFullname(fullName);
            //     user.setAddress(address);
            //     user.setPhone(phone);
            //     user.setAvatar(urlImage);
            //     updateUserInfo();
            // }
        }
    }

    private boolean validate(@NotNull String fullName, String phone, String address) {
        if (fullName.isEmpty()) {
            txtFullName.setError("Không được bỏ trống");
            return false;
        }
        if (phone.isEmpty()) {
            txtPhone.setError("Không được bỏ trống");
            return false;
        }
        if (address.isEmpty()) {
            txtAddress.setError("Không được bỏ trống");
            return false;
        }
        if (fileImage == null) {
            // Provide default image URI if no image is selected
            fileImage = Uri.parse("android.resource://com.shopeeapp/drawable/account");
        }
        return true;
    }

    private void updateUserInfo() {
        // Replace with your logic to update user information in the database
        // For example:
        // UserDbHelper userDbHelper = new UserDbHelper(this);
        // int re = userDbHelper.update(user);
        // if (re > 0) {
        //     Log.i("===Update user info to database===", "Success");
        // }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {//Xử lý kết quả yêu cầu quyền truy cập.
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case AppUtilities.PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_CODE) {
                fileImage = data.getData();
                if (fileImage != null) {
                    imgAvt.setImageURI(data.getData());
                } else {
                    Log.e("AccountInfoActivity", "Lỗi khi chọn ảnh");
                }
            } else if (requestCode == AppUtilities.TAKE_PICTURE) {
                fileImage = AppUtilities.photoURI;
                imgAvt.setImageURI(fileImage);
            }
        }
    }
}
