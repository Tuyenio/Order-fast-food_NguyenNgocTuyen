package com.shopeeapp.activity;

import static com.shopeeapp.utilities.AppUtilities.PERMISSION_REQUEST_CODE;
import static com.shopeeapp.utilities.AppUtilities.SELECT_PICTURE;
import static com.shopeeapp.utilities.AppUtilities.TAKE_PICTURE;
import static com.shopeeapp.utilities.AppUtilities.encode;
import static com.shopeeapp.utilities.AppUtilities.requestPermission;
import static com.shopeeapp.utilities.AppUtilities.setPic;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.shopeeapp.R;
import com.shopeeapp.dbhelper.AccountDbHelper;
import com.shopeeapp.dbhelper.UserDbHelper;
import com.shopeeapp.entity.Account;
import com.shopeeapp.entity.User;
import com.shopeeapp.utilities.AppUtilities;

import org.jetbrains.annotations.NotNull;

public class SignUp extends AppCompatActivity {
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    TextInputEditText txtFullName,
            txtEmail,
            txtPhone,
            txtAddress,
            txtUsername,
            txtPassword,
            txtConfirmPassword;
    TextInputLayout layoutConfirmPassword;
    ImageView imgAvt;
    ChipGroup chipGroupSex;
    Uri fileImage;
    String urlImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        addControl();
        addEvent();
        setConfirmPassword();
    }

    private void addControl() {
        txtFullName = findViewById(R.id.txtFullName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPhone = findViewById(R.id.txtPhone);
        txtAddress = findViewById(R.id.txtAddress);
        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);
        txtConfirmPassword = findViewById(R.id.txtConfirmPassword);
        layoutConfirmPassword = findViewById(R.id.layoutConfirmPassword);
        imgAvt = findViewById(R.id.imgAvt);
        chipGroupSex = findViewById(R.id.chipGroupSex);
    }

    private void addEvent() {
        findViewById(R.id.btnBack).setOnClickListener(view -> finish());
        findViewById(R.id.txtSignIn).setOnClickListener(this::setLogIn);
        findViewById(R.id.btnSignUp).setOnClickListener(this::setSignUp);
        findViewById(R.id.btnTakePhoto).setOnClickListener(this::setTakePhoto);
        findViewById(R.id.btnChoosePhoto).setOnClickListener(AppUtilities::setChoosePhoto);
    }

    private void setTakePhoto(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            AppUtilities.setTakePhoto(view);
        }
    }

    private void setConfirmPassword() {
        txtConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setConfirmPasswordErrorHelper();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                setConfirmPasswordErrorHelper();
            }
        });
    }

    private void setConfirmPasswordErrorHelper() {
        String password = txtPassword.getText().toString();
        String confirmPassword = txtConfirmPassword.getText().toString();

        if (password.equals(confirmPassword))
            layoutConfirmPassword.setErrorEnabled(false);
        else {
            layoutConfirmPassword.setErrorEnabled(true);
            layoutConfirmPassword.setError("Phải trùng với mật khẩu đã nhập.");
        }
    }

    private boolean validate(@NotNull String fullName, String email, String phone, String address,
                             String username, String password, String confirmPassword) {
        if (fullName.equals("")) {
            txtFullName.setError("Không được bỏ trống");
            return false;
        }
        if (email.equals("")) {
            txtEmail.setError("Không được bỏ trống");
            return false;
        }
        if (phone.equals("")) {
            txtPhone.setError("Không được bỏ trống");
            return false;
        }
        if (address.equals("")) {
            txtAddress.setError("Không được bỏ trống");
            return false;
        }
        if (username.equals("")) {
            txtUsername.setError("Không được bỏ trống");
            return false;
        }
        if (password.equals("")) {
            txtPassword.setError("Không được bỏ trống");
            return false;
        }
        if (confirmPassword.equals("")) {
            txtConfirmPassword.setError("Không được bỏ trống");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            txtConfirmPassword.setError("Mật khẩu không trùng khớp");
            return false;
        }
        if (password.length() < 6) {
            txtPassword.setError("Mật khẩu phải dài hơn hoặc bằng 6 ký tự");
            return false;
        }
        if (fileImage == null) {
            fileImage = Uri.parse("android.resource://com.shopeeapp/drawable/account");
        }
        return true;
    }

    private void setSignUp(View view) {
        String fullName = txtFullName.getText().toString();
        String email = txtEmail.getText().toString();
        String phone = txtPhone.getText().toString();
        String address = txtAddress.getText().toString();
        String username = txtUsername.getText().toString();
        String password = txtPassword.getText().toString();
        String confirmPassword = txtConfirmPassword.getText().toString();

        if (!validate(fullName, email, phone, address, username, password, confirmPassword)) {
            Toast.makeText(this, "Nhập thông tin sai", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save image URI to urlImage
        urlImage = fileImage.toString();

        signUp();
    }

    private void signUp() {
        String fullName = txtFullName.getText().toString();
        String email = txtEmail.getText().toString();
        String phone = txtPhone.getText().toString();
        String address = txtAddress.getText().toString();
        String username = txtUsername.getText().toString();
        String password = txtPassword.getText().toString();

        Account account = new Account(username, email, password);
        AccountDbHelper accountDbHelper = new AccountDbHelper(this);
        long rowID = accountDbHelper.insert(account);
        if (rowID < 0) {
            Toast.makeText(this, "Vui lòng nhập lại thông tin!", Toast.LENGTH_SHORT).show();
        } else {
            Integer accountId = accountDbHelper.getAccountByRowId(rowID).getId();
            User user = new User(accountId, fullName, getSex(), phone, address, urlImage);
            UserDbHelper userDbHelper = new UserDbHelper(this);
            long re = userDbHelper.insert(user);
            if (re < 0) {
                Toast.makeText(this, "Đã xảy ra lỗi trong quá trình tạo tài khoản. Vui lòng tạo lại!",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Đã đăng ký thành công!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    @NotNull
    private String getSex() {
        int selected = chipGroupSex.getCheckedChipId();
        switch (selected) {
            case R.id.chipMale:
                return "M";
            case R.id.chipFemale:
                return "F";
            default:
                return "O";
        }
    }

    void setLogIn(View view) {
        Intent intent = new Intent(this, LogIn.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, IMAGE_PICK_CODE);
                } else {
                    Toast.makeText(this, "Permission denied...!", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_CODE) {
                fileImage = data.getData();
                if (fileImage != null) {
                    imgAvt.setImageURI(data.getData());
                } else {
                    System.out.println("Lỗi");
                }
            } else if (requestCode == TAKE_PICTURE) {
                fileImage = AppUtilities.photoURI;
                imgAvt.setImageURI(fileImage);
            }
        }
    }
}
