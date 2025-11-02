package com.example.onlinetest.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.onlinetest.Domain.Dto.ProductDto;
import com.example.onlinetest.Domain.Dto.ProductsListResponseDto;
import com.example.onlinetest.Domain.Exceptions.ProductException;
import com.example.onlinetest.Repo.Product;
import com.example.onlinetest.Repo.ProductRepo;

@Service
public class ProductService implements IProductService {

    private final ProductRepo productRepo;
    private static final int PAGE_SIZE = 5;

    @Autowired
    public ProductService(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }

    @Override
    public ProductsListResponseDto listProducts(Integer pageParam) {
        int page = (pageParam == null || pageParam < 1) ? 1 : pageParam;

        try {
            PageRequest pageRequest = PageRequest.of(page - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<Product> pageResult = productRepo.findAll(pageRequest);

            List<ProductDto> products = pageResult.getContent().stream()
                .map(ProductDto::new)
                .collect(Collectors.toList());

            ProductsListResponseDto response = new ProductsListResponseDto();
            response.setSuccess(true);
            response.setMessage(products.isEmpty() ? "No products found" : "Products retrieved successfully");
            response.setProducts(products);
            response.setPage(page);
            response.setTotalPages(pageResult.getTotalPages());
            response.setTotalItems(pageResult.getTotalElements());

            return response;
        } catch (Exception e) {
            throw new ProductException("Failed to fetch products: " + e.getMessage(), e);
        }
    }
}