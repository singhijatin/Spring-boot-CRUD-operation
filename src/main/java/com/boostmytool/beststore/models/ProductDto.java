package com.boostmytool.beststore.models;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;
public class ProductDto {

    @NotEmpty(message = "name cannot be empty")
    private String name;
    @NotEmpty(message = "brand cannot be empty")
    private String brand;
    @NotEmpty(message = "category cannot be empty")
    private String category;
    @Min(0)
    private double price;
    @Size(min=10, message ="description must have atleast 10 characters")

    @Size(max=2000, message ="description must not exceed 2000 characters")
    private String  description;
    private MultipartFile imageFileName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MultipartFile getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(MultipartFile imageFileName) {
        this.imageFileName = imageFileName;
    }
}
