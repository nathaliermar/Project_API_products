package com.api.products.repositories;

import com.api.products.model.ProductModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductModel, UUID> {

}
