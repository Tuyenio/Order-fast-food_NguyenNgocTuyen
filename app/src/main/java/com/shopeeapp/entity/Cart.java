package com.shopeeapp.entity;

import com.shopeeapp.dbhelper.ProductDbHelper;

public class Cart {
    public static final String CART_UNPAID = "Unpaid";
    public static final String CART_PAID = "Paid";
    public static final String CART_ORDERED = "Ordered";

    private Integer id;
    private Integer userId;
    private Integer productId;
    private Integer quantity;
    private String status;
    private Product product;
    private String color;
    private String size;

    public Cart(Integer id, Integer userId, Integer productId, Integer quantity, String status,String color,String size) {
        this.setId(id);
        this.setUserId(userId);
        this.setProductId(productId);
        this.setQuantity(quantity);
        this.setStatus(status);
        this.setColor(color);
        this.setSize(size);

    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Cart(Integer userId, Integer productId, Integer quantity,String size,String color) {
        this(-1, userId, productId, quantity, CART_UNPAID,size,color);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        if (quantity >= 0)
            this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Double getTotalPrice() {
        if (this.getProduct() == null)
            return 0.0;
        return quantity * this.getProduct().getPrice();
    }

    public void setCartOrdered() {
        this.setStatus(CART_ORDERED);
    }

    public void setCartPaid() {
        this.setStatus(CART_PAID);
    }
}
