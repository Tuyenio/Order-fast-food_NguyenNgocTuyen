package com.shopeeapp.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.shopeeapp.MainActivity;
import com.shopeeapp.R;
import com.shopeeapp.dbhelper.BillDbHelper;
import com.shopeeapp.dbhelper.CartDbHelper;
import com.shopeeapp.dbhelper.DiscountDbHelper;
import com.shopeeapp.dbhelper.NotificationDbHelper;
import com.shopeeapp.dbhelper.StoreDbHelper;
import com.shopeeapp.entity.Bill;
import com.shopeeapp.entity.Cart;
import com.shopeeapp.entity.Discount;
import com.shopeeapp.entity.Notification;
import com.shopeeapp.entity.Product;
import com.shopeeapp.entity.Store;
import com.shopeeapp.utilities.AccountSessionManager;

import java.util.Locale;

public class CartDetail extends AppCompatActivity {

    ImageView productImage;
    TextView productTitle;
    EditText txtQuantity;
    ImageButton btnSubtract;
    ImageButton btnPlus;
    TextView productPrice;
    TextView productDiscount;
    TextView txtProductStore;
    TextView txtProductStoreAddress;
    TextInputEditText txtDeliveryAddress;
    TextView cartPrice;
    Button btnCancelOrder;
    Button btnOrder;

    public static Cart cart;
    private Product product;
    private int quantity = 0;
    private Float discount = (float) 0;
    private Float price;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_detail);

        addControl();
        addEvent();
        if (cart == null) return;
        setCartInfo();
    }

    private void addControl() {
        productImage = findViewById(R.id.productImage);
        productTitle = findViewById(R.id.productTitle);
        txtQuantity = findViewById(R.id.txtQuantity);
        btnSubtract = findViewById(R.id.subtract);
        btnPlus = findViewById(R.id.plus);
        productPrice = findViewById(R.id.productPrice);
        productDiscount = findViewById(R.id.productDiscount);
        txtProductStore = findViewById(R.id.productStore);
        txtProductStoreAddress = findViewById(R.id.productStoreAddress);
        txtDeliveryAddress = findViewById(R.id.txtDeliveryAddress);
        cartPrice = findViewById(R.id.cartPrice);
        btnCancelOrder = findViewById(R.id.btnCancelOrder);
        btnOrder = findViewById(R.id.btnOrder);
    }

    private void addEvent() {
        findViewById(R.id.btnBack).setOnClickListener(view -> finish());
        btnCancelOrder.setOnClickListener(this::setCancelOrder);
        btnOrder.setOnClickListener(this::setOrder);
        btnPlus.setOnClickListener(this::setPlus);
        btnSubtract.setOnClickListener(this::setSubtract);
        txtQuantity.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txtQuantity.getText().toString().isEmpty()) {
                    txtQuantity.setText("0");
                }
                quantity = Integer.parseInt(txtQuantity.getText().toString());
                calcPrice();
            }
        });
    }

    private void setCartInfo() {
        if (cart == null)
            return;
        this.product = cart.getProduct();
        productTitle.setText(product.getName());
        productPrice.setText(formatCurrency(product.getPrice()));

        DiscountDbHelper dbHelper = new DiscountDbHelper(this);
        Discount discountEntity =  dbHelper.getDiscountByProduct(product.getId());
        discount = discountEntity == null ? 0 : discountEntity.getValue();
        productDiscount.setText(String.valueOf(discount));
        price = (float) Math.round(cart.getTotalPrice() * (100 - discount) / 100);
        cartPrice.setText(String.valueOf(price));
        setAddress();
        setQuantity();
        setBackgroundImage();

        StoreDbHelper storeDbHelper = new StoreDbHelper(this);
        Store store = storeDbHelper.getStoreById(product.getStoreId());

        if (store == null)
            return;
        txtProductStore.setText(store.getName());
        txtProductStoreAddress.setText(store.getAddress());
    }

    private String formatCurrency(Double amount) {
        return String.format(Locale.getDefault(), "%,.0f", amount);
    }

    private void setOrder(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.payment_resource);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);
        dialog.setCancelable(true);

        Button btnNext = dialog.findViewById(R.id.btnNext);
        RadioGroup rdgPayment = dialog.findViewById(R.id.rdgPayment);
        RadioButton rdTrucTiep = dialog.findViewById(R.id.rdTrucTiep);

        btnNext.setOnClickListener(view1 -> {
            BillDbHelper billDbHelper = new BillDbHelper(view.getContext());
            String deliveryAddress = txtDeliveryAddress.getText().toString();
            Bill bill = new Bill(AccountSessionManager.user.getId(), cart.getId(), deliveryAddress, discount, price);
            long result = billDbHelper.insert(bill);
            if (result > 0) {
                createCart();
                Integer id = Integer.valueOf(createNotification(product.getName()).toString());
                showNotification(id);
                Toast.makeText(view.getContext(), "Đã đặt hàng thành công.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        dialog.show();
    }

    private void setCancelOrder(View view) {
        CartDbHelper cartDbHelper = new CartDbHelper(view.getContext());
        int re = cartDbHelper.delete(cart.getId());
        if (re > 0) {
            Toast.makeText(view.getContext(), "Đã xóa giỏ hàng.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void createCart() {
        CartDbHelper cartDbHelper = new CartDbHelper(this);
        cart.setCartOrdered();
        cart.setQuantity(quantity);
        cart.setStatus(Cart.CART_ORDERED);
        cartDbHelper.update(cart);
    }

    private Long createNotification(String productName) {
        NotificationDbHelper notificationDbHelper = new NotificationDbHelper(this);
        Notification notification = new Notification(
                AccountSessionManager.user.getId(),
                Notification.NOTIFY_CART,
                Notification.NOTIFY_ORDER_PRODUCT + productName);
        return notificationDbHelper.insert(notification);
    }

    private void setBackgroundImage() {
        Glide.with(this)
                .load(product.getImage())
                .into(productImage);
    }

    private void setAddress() {
        String address = AccountSessionManager.user.getAddress();
        if (address != null && !address.isEmpty()) {
            txtDeliveryAddress.setText(address);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setQuantity() {
        this.quantity = cart.getQuantity();
        txtQuantity.setText(String.valueOf(cart.getQuantity()));
    }

    private void calcPrice() {
        quantity = Integer.parseInt(txtQuantity.getText().toString());
        price = (float) Math.round(cart.getTotalPrice() * (100 - discount) / 100);
        cartPrice.setText(String.valueOf(price));
    }

    private void setSubtract(View view) {
        if (quantity <= 1) return;
        quantity--;
        txtQuantity.setText(String.valueOf(quantity));
        cart.setQuantity(quantity);
        CartDbHelper cartDbHelper = new CartDbHelper(this);
        cartDbHelper.update(cart);
    }

    private void setPlus(View view) {
        quantity++;
        txtQuantity.setText(String.valueOf(quantity));
        cart.setQuantity(quantity);
        CartDbHelper cartDbHelper = new CartDbHelper(this);
        cartDbHelper.update(cart);
    }

    private void showNotification(Integer id) {
        NotificationManager nm = (NotificationManager) getSystemService(NotificationManager.class);
        if (nm == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("1", "Đặt hàng thành công", NotificationManager.IMPORTANCE_DEFAULT);
            nm.createNotificationChannel(channel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
                .setContentTitle("Đặt hàng thành công ")
                .setContentText("Cảm ơn bạn đã đặt thành công " + product.getName() + " tại ShopeeApp. Chúc bạn một ngày vui vẻ!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("action", MainActivity.NOTIFICATION_ACTION);


        nm.notify(id, mBuilder.build());
    }

    abstract class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }
}
