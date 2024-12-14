package com.shopeeapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.shopeeapp.R;
import com.shopeeapp.admin.ShowPaymentActivity;
import com.shopeeapp.dbhelper.BillDbHelper;
import com.shopeeapp.dbhelper.ProductDbHelper;
import com.shopeeapp.dbhelper.UserDbHelper;
import com.shopeeapp.entity.Bill;
import com.shopeeapp.entity.Cart;
import com.shopeeapp.entity.Product;
import com.shopeeapp.entity.User;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
//adapter tùy chỉnh cho một danh sách các đối tượng Bill,
// giúp hiển thị các hóa đơn trong giao diện quản trị của ứng dụng Android
public class AdminBillAdapter extends ArrayAdapter<Bill> {
    private final Activity context;
    private final ArrayList<Bill> bills;
    private final int layoutResource;

    public AdminBillAdapter(Activity context, int resource, ArrayList<Bill> objects) {
        super(context, resource, objects);
        this.context = context;
        this.bills = objects;
        this.layoutResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater layoutInflater = this.context.getLayoutInflater();
            row = layoutInflater.inflate(this.layoutResource, parent, false);
        }

        Bill bill = bills.get(position);
        String billId = bill.getId().toString();
        BillDbHelper billDbHelper = new BillDbHelper(context);
        UserDbHelper userDbHelper = new UserDbHelper(context);
        ProductDbHelper productDbHelper = new ProductDbHelper(context);
        Cart cartInformation = billDbHelper.getCartById(bill.getCartId());
        TextView txtusername = row.findViewById(R.id.txt_row_show_nameUser);
        TextView txtPhone = row.findViewById(R.id.txt_row_show_phoneUser);
        TextView title = row.findViewById(R.id.billProductName);
        TextView txtAddress = row.findViewById(R.id.billDeliveryAddress);
        TextView price = row.findViewById(R.id.billTotalPrice);
        TextView date = row.findViewById(R.id.billTime);
        TextView productPrice = row.findViewById(R.id.productPrice);
        TextView quantity = row.findViewById(R.id.billQuantity);
        TextView billDiscount = row.findViewById(R.id.billDiscount);
        TextView billStatus = row.findViewById(R.id.billStatus);
        TextView size = row.findViewById(R.id.size);
        TextView color = row.findViewById(R.id.cl);
        size.setText(cartInformation.getSize());
        color.setText(cartInformation.getColor());
        // Fetch user details once and reuse
        int userId = bill.getUserId();
        User user = userDbHelper.getUser(userId);
        if (user != null) {
            txtusername.setText(user.getFullname());
            txtPhone.setText(user.getPhone());
            txtAddress.setText(user.getAddress());
        }

        String dateget = bill.getDate();
        date.setText(dateget);
        billDiscount.setText(String.valueOf(bill.getDiscount()));
        billStatus.setText(bill.getStatus());
        if (cartInformation != null) {
            int id = cartInformation.getProductId();
            Product product = productDbHelper.getvProductById(id);
            if (product != null) {
                title.setText("Tên sản phẩm: " + product.getName());
                NumberFormat numberFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
                String formattedProductPrice = numberFormat.format(product.getPrice());
                String formattedTotalPrice = numberFormat.format(cartInformation.getQuantity() * product.getPrice());
                productPrice.setText(formattedProductPrice + " VND");
                price.setText(formattedTotalPrice+ " VND");
            }
        }

        Button btnConfirm = row.findViewById(R.id.buttonConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bill bill = billDbHelper.getBillById(Integer.parseInt(billId));
                if (bill != null) {
                    bill.setStatus("Paid");
                    billDbHelper.update(bill);
                    Intent intent = new Intent(context, ShowPaymentActivity.class);
                    context.startActivity(intent);
                }
            }
        });

        return row;
    }
}
