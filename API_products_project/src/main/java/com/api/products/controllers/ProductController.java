package com.api.products.controllers;

import com.api.products.dtos.ProductRecordDto;
import com.api.products.model.ProductModel;
import com.api.products.repositories.ProductRepository;
import jakarta.validation.Valid;
import org.hibernate.internal.log.SubSystemLogging;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api-store")
public class ProductController {

    @Autowired
    ProductRepository productRepository;

    @PostMapping("/products")
    public ResponseEntity<ProductModel> saveProduct(@RequestBody @Valid ProductRecordDto productRecordDto) {
        var productModel = new ProductModel();
        BeanUtils.copyProperties(productRecordDto, productModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(productModel));
    }
    @GetMapping("/products")
    public ResponseEntity<List<ProductModel>> getAllProducts(){ List<ProductModel> productsList = productRepository.findAll();
        if(!productsList.isEmpty()) {
            for(ProductModel product : productsList) {
                UUID id = product.getIdProduct();
                product.add(linkTo(methodOn(ProductController.class).getOneProduct(id)).withSelfRel());
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(productsList);
    }
    @GetMapping("/products/{id}")
    public ResponseEntity<Object> getOneProduct(@PathVariable(value = "id")UUID id){
        Optional<ProductModel> productModelOptional = productRepository.findById(id);
        if (productModelOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product Not Found");
        }
        productModelOptional.get().add(linkTo(methodOn(ProductController.class).getAllProducts()).withRel("Products List"));
        return ResponseEntity.status(HttpStatus.OK).body(productModelOptional.get());
    }
    @PutMapping("/products/{id}")
    public ResponseEntity<Object> updateProduct(@PathVariable(value = "id") UUID id,
                                                @RequestBody @Valid ProductRecordDto productRecordDto) {
        Optional<ProductModel> productModelOptional = productRepository.findById(id);
        if (productModelOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product Not Found");
        }
        ProductModel productModel = productModelOptional.get();
        BeanUtils.copyProperties(productRecordDto, productModel);
        return  ResponseEntity.status(HttpStatus.OK).body(productRepository.save(productModel));
    }
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Object> deleteProduct(@PathVariable(value="id") UUID id) {
        Optional<ProductModel> productO = productRepository.findById(id);
        if(productO.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found.");
        }
        productRepository.delete(productO.get());
        return ResponseEntity.status(HttpStatus.OK).body("Product deleted successfully.");
    }
}
