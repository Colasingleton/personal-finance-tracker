package com.financetracker.expense_tracker.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false , length = 50)
    private String name;

    @Column(name = "color_code", length = 7)
    private String colorCode = "#007bff";

    @Column(length = 30)
    private String icon = "fa-money-bill";

    public Category() {}

    public Category(String name) {
        this.name = name;
    }

    public Category(String name, String colorCode, String icon) {
        this.name = name;
        this.colorCode = colorCode;
        this.icon = icon;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColorCode() {
        return colorCode;
    }

    public void setColorCode(String colorCode) {
        this.colorCode = colorCode;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
