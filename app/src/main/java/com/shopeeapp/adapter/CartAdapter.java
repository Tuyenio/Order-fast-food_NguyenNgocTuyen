package com.shopeeapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.bumptech.glide.Glide;
import com.shopeeapp.R;
import com.shopeeapp.activity.CartDetail;
import com.shopeeapp.dbhelper.CartDbHelper;
import com.shopeeapp.entity.Cart;
import com.shopeeapp.fragment.CartFragment;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    public ArrayList<Cart> carts;
    private CartFragment context;
    public CartAdapter(ArrayList<Cart> carts, CartFragment context) {

        this.carts = carts;
        this.context = context;
    }

    public ArrayList<Cart> getCarts() {
        return carts;
    }

    public void setCarts(ArrayList<Cart> carts) {
        this.carts = carts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cart_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cart cart = carts.get(position);
        holder.cart = cart;
        Glide.with(context)
                .load(cart.getProduct().getImage())
                .into(holder.cartImage);
        holder.cartTitle.setText(cart.getProduct().getName());
        holder.cartQuantity.setText(String.valueOf(cart.getQuantity()));
        String status = cart.getStatus() == "Paid" ? "Đã thanh toán" : "Chưa thanh toán";
        holder.cartStatus.setText(status);
        holder.cartColor.setText(cart.getColor());
        holder.cartSize.setText(cart.getSize());

    }

    @Override
    public int getItemCount() {
        return carts == null ? 0 : carts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView cartImage;
        TextView cartTitle;
        TextView cartQuantity;
        TextView cartStatus;
        Cart cart;
        TextView cartSize;
        TextView cartColor;
        ImageView popup;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cartImage = itemView.findViewById(R.id.cartImage);
            cartTitle = itemView.findViewById(R.id.cartProductName);
            cartQuantity = itemView.findViewById(R.id.cartQuantity);
            cartStatus = itemView.findViewById(R.id.cartStatus);
            cartSize = itemView.findViewById(R.id.cartSize);
            cartColor = itemView.findViewById(R.id.cartColor);
            popup = itemView.findViewById(R.id.pp);
            itemView.setOnClickListener(v -> {
                CartDetail.cart = cart;
                Intent intent = new Intent(itemView.getContext(), CartDetail.class);
                itemView.getContext().startActivity(intent);
            });
            popup.setOnClickListener(view -> showPopupMenu(view, getAdapterPosition()));
        }
    }
    private void showPopupMenu(View view, int position) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.popupmenu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ppDelete:
                        removeItem(position);
                        return true;
                    case R.id.ppAccept:
                        AcceptItem(position);
                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }
    private void removeItem(int position) {
        Cart cart = carts.get(position);
        CartDbHelper cartDbHelper = new CartDbHelper(context.getContext());
        cartDbHelper.delete(cart.getId());
        carts.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, carts.size());
        Toast.makeText(context.getContext(), "Đã xóa sản phẩm khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
    }
    private void AcceptItem(int position) {
        Cart cart = carts.get(position);
        CartDbHelper cartDbHelper = new CartDbHelper(context.getContext());
        cart.setStatus("Ordered");
        cartDbHelper.updateStatus(cart.getId(),"Ordered");
        carts.remove(position);
        notifyDataSetChanged();
        Toast.makeText(context.getContext(), "Đã cập nhật thành công", Toast.LENGTH_SHORT).show();
    }
}
