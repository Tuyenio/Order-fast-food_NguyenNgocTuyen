package com.shopeeapp.dto;
// lớp DTO (Data Transfer Object) được sử dụng để chứa và truyền dữ liệu giữa các lớp hoặc tầng trong ứng dụng
public class StatisDTO {//Phương thức để lấy và thiết lập giá trị của thuộc tính id.
    private Integer id;
    private String name;
    private Integer quantity;

    public StatisDTO(Integer id, String name, Integer quantity) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
    }

    public StatisDTO(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
