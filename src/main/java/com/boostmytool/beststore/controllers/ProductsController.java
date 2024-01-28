package com.boostmytool.beststore.controllers;

import com.boostmytool.beststore.models.Product;
import com.boostmytool.beststore.models.ProductDto;
import com.boostmytool.beststore.services.ProductsRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductsController {
    @Autowired
    private ProductsRepository repo;

    @GetMapping({"","/"})
    public String showProductList(Model model){
        List<Product> products = repo.findAll(Sort.by(Sort.Direction.DESC,"id"));
        model.addAttribute("products",products);
        return "products/index";
    }

    @GetMapping({"","/"})
    public String showCreatePage(Model model){
        ProductDto productDto = new ProductDto();
        model.addAttribute("productDto",productDto);
        return "products/CreateProduct";
    }

    @PostMapping({"/create"})
    public String createProduct(
        @Valid @ModelAttribute ProductDto productDto,
                BindingResult result){
        if(productDto.getImageFileName().isEmpty()){
            result.addError(new FieldError("productDto","imageFile", "The image File is required"));
        }

        if(result.hasErrors()){
            return "products/CreateProduct";
        }

        MultipartFile image = productDto.getImageFileName();
        Date createdAt = new Date();
        String storageFileName = createdAt.getTime() + " " + image.getOriginalFilename();
        try{
            String uploadDir = "public/image/";
            Path uploadPath = Paths.get(uploadDir);
            if(!Files.exists(uploadPath)){
                Files.createDirectories(uploadPath);
            }
            try(InputStream inputStream = image.getInputStream()){
                Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        }catch (Exception e){
            System.out.println("Exception:" + e.getMessage());
        }

        Product product = new Product();
        product.setName(productDto.getName());
        product.setBrand(productDto.getBrand());
        product.setCategory(productDto.getCategory());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setCreatedAt(createdAt);
        product.setImageFileName(storageFileName);

        repo.save(product);

        return "redirect:/products";
    }

    @GetMapping("/edit")
    public String showEditPage(
            Model model, @RequestParam int id
    ){

        try{
            Product product = repo.findById(id).get();
            model.addAttribute("product", product);

            ProductDto productDto = new ProductDto();
            productDto.setName(product.getName());
            productDto.setBrand(product.getBrand());
            productDto.setCategory(product.getCategory());
            productDto.setPrice(product.getPrice());
            productDto.setDescription(product.getDescription());

            model.addAttribute("productDto",productDto);

        }catch (Exception e){
            System.out.println("Exception:" + e.getMessage());
            return "redirect:/products";
        }
        return "products/EditProduct";
    }

    @PostMapping("/edit")
    public String updateProduct(
            Model model,
            @RequestParam int id,
            @Valid @ModelAttribute ProductDto productDto,
            BindingResult result
    ){
        try{
            Product product = repo.findById(id).get();
            model.addAttribute("product",product);

            if(result.hasErrors()){
                return "products/EditProduct";
            }
            if(!productDto.getImageFileName().isEmpty()){
                //delete old image
                String uploadDir = "public/image/";
                Path oldImagePath = Paths.get(uploadDir + product.getImageFileName());
                try{
                    Files.delete(oldImagePath);
                }catch (Exception e) {
                    System.out.println("Exception:" + e.getMessage());
                }

                //save the new image file
                MultipartFile image = productDto.getImageFileName();
                Date createdAt = new Date();
                String storageFileName = createdAt.getTime() + " " + image.getOriginalFilename();
                try(InputStream inputStream = image.getInputStream()){
                        Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                                StandardCopyOption.REPLACE_EXISTING);
                    }
                product.setImageFileName(storageFileName);
            }
            product.setName(productDto.getName());
            product.setBrand(productDto.getBrand());
            product.setCategory(productDto.getCategory());
            product.setPrice(productDto.getPrice());
            product.setDescription(productDto.getDescription());

            repo.save(product);

        }catch (Exception e){
            System.out.println("Exception:" + e.getMessage());
        }
        return "redirect:/products";

    }

    @GetMapping("/delete")
    public String deleteProduct(
            @RequestParam int id
    ){
        try{
            Product product = repo.findById(id).get();

            //deleteOldImage
            String uploadDir = "public/image/";
            Path oldImagePath = Paths.get(uploadDir + product.getImageFileName());
            try{
                Files.delete(oldImagePath);
            }catch (Exception e) {
                System.out.println("Exception:" + e.getMessage());
            }

            repo.delete(product);
        }catch (Exception e){
            System.out.println("Exception:" + e.getMessage());
        }
        return "redirect:/products";
    }
}
