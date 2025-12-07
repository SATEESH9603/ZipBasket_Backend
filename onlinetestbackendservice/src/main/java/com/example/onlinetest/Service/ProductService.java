package com.example.onlinetest.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.onlinetest.Domain.Dto.CreateProductRequestDto;
import com.example.onlinetest.Domain.Dto.CreateProductResponseDto;
import com.example.onlinetest.Domain.Dto.ProductDto;
import com.example.onlinetest.Domain.Dto.ProductsListResponseDto;
import com.example.onlinetest.Domain.Dto.ProductResponseDto;
import com.example.onlinetest.Domain.Exceptions.ProductException;
import com.example.onlinetest.Repo.Product;
import com.example.onlinetest.Repo.ProductRepo;
import com.example.onlinetest.Repo.User;
import com.example.onlinetest.Repo.UserRepo;
import com.example.onlinetest.Repo.Category;
import com.example.onlinetest.Domain.Dto.UpdateProductRequestDto;
import com.example.onlinetest.Domain.Dto.UpdateProductResponseDto;

@Service
public class ProductService implements IProductService {

    private final ProductRepo productRepo;
    private final UserRepo userRepo;
    private static final int PAGE_SIZE = 5;

    public ProductService(ProductRepo productRepo, UserRepo userRepo) {
        this.productRepo = productRepo;
        this.userRepo = userRepo;
    }

    @Override
    public CreateProductResponseDto createProduct(CreateProductRequestDto request) {
        try {
            Product p = new Product();
            if (request.getSellerId() != null && !request.getSellerId().isBlank()) {
                // try to resolve user by UUID and set as seller
                try {
                    UUID uid = UUID.fromString(request.getSellerId());
                    User user = userRepo.findById(uid).orElse(null);
                    if (user == null) {
                        throw new ProductException("Seller with id " + request.getSellerId() + " not found");
                    }
                    p.setSeller(user);
                } catch (IllegalArgumentException iae) {
                    throw new ProductException("Invalid sellerId format: " + request.getSellerId(), iae);
                }
            }
            p.setName(request.getName());
            p.setDescription(request.getDescription());
            if (request.getPrice() != null && !request.getPrice().isBlank()) {
                p.setPrice(new BigDecimal(request.getPrice()));
            }
            p.setCurrency(request.getCurrency());
            if (request.getQuantity() != null) p.setQuantity(request.getQuantity());
            p.setSku(request.getSku());
            if (request.getCategory() != null && !request.getCategory().isBlank()) {
                try {
                    p.setCategory(Category.valueOf(request.getCategory().toUpperCase().replace(' ', '_')));
                } catch (IllegalArgumentException iae) {
                    throw new ProductException("Invalid category: " + request.getCategory(), iae);
                }
            }
            p.setImages(request.getImages());
            p.setWeight(request.getWeight());
            p.setDimensions(request.getDimensions());
            if (request.getIsActive() != null) p.setActive(request.getIsActive());
            p.setMetadata(request.getMetadata());

            Product saved = productRepo.save(p);

            CreateProductResponseDto resp = new CreateProductResponseDto();
            resp.setSuccess(true);
            resp.setMessage("Product created successfully");
            resp.setProduct(new ProductDto(saved));
            return resp;
        } catch (Exception e) {
            throw new ProductException("Failed to create product: " + e.getMessage(), e);
        }
    }

    @Override
        public ProductsListResponseDto listProducts(Integer pageParam, String category) {
            int page = (pageParam == null || pageParam < 1) ? 1 : pageParam;

            try {
                PageRequest pageRequest = PageRequest.of(page - 1, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));
                Page<Product> pageResult;

                String raw = (category == null) ? null : category.trim();
                if (raw == null || raw.isEmpty()) {
                    pageResult = productRepo.findAll(pageRequest); // empty -> all
                } else {
                    Category cat = parseEnumOrThrow(Category.class, raw); // throws if invalid
                    pageResult = productRepo.findByCategory(cat, pageRequest);
                }

                List<ProductDto> products = com.example.onlinetest.Domain.Mapper.toProductDtoList(pageResult.getContent());

                ProductsListResponseDto response = new ProductsListResponseDto();
                response.setSuccess(true);
                response.setMessage(products.isEmpty() ? "No products found" : "Products retrieved successfully");
                response.setProducts(products);
                response.setPage(page);
                response.setTotalPages(pageResult.getTotalPages());
                response.setTotalItems(pageResult.getTotalElements());
                return response;

            } catch (IllegalArgumentException badEnum) {
                // invalid category string -> 400
                ProductsListResponseDto response = new ProductsListResponseDto();
                response.setSuccess(false);
                response.setMessage("Invalid category: " + category);
                response.setProducts(Collections.emptyList());
                response.setPage(0);
                response.setTotalPages(0);
                response.setTotalItems(0);
                return response;
            } catch (Exception e) {
                throw new ProductException("Failed to fetch products: " + e.getMessage(), e);
            }
        }

        /** Case-insensitive enum parse, throws IllegalArgumentException if not found. */
        private static <E extends Enum<E>> E parseEnumOrThrow(Class<E> enumType, String value) {
            for (E e : enumType.getEnumConstants()) {
                if (e.name().equalsIgnoreCase(value)) return e;
            }
            throw new IllegalArgumentException("No enum constant " + enumType.getSimpleName() + "." + value);
        }

        @Override
        public ProductResponseDto getProductById(String productId)
        {
            ProductResponseDto response = new ProductResponseDto();
            try {
                UUID pid = UUID.fromString(productId);
                Product product = productRepo.findById(pid).orElse(null);
                if (product == null) {
                    response.setSuccess(false);
                    response.setMessage("Product with id " + productId + " not found");
                } else {                    
                    response = com.example.onlinetest.Domain.Mapper.toProductResponseDto(product);
                }
            }
            catch (IllegalArgumentException iae) {
                response.setSuccess(false);
                response.setMessage("Invalid productId format: " + productId);
                response.setProduct(null);
            }
            catch (Exception e) {
                throw new ProductException("Failed to fetch product: " + e.getMessage(), e);
            }
            return response;
        }

        @Override
        public UpdateProductResponseDto updateProduct(String productId, UpdateProductRequestDto request) {
            UpdateProductResponseDto response = new UpdateProductResponseDto();
            try {
                UUID pid = UUID.fromString(productId);
                Product product = productRepo.findById(pid).orElse(null);
                if (product == null) {
                    response.setSuccess(false);
                    response.setMessage("Product with id " + productId + " not found");
                    return response;
                }
                // Map update fields
                product = com.example.onlinetest.Domain.Mapper.toProduct(product, request);
                Product updated = productRepo.save(product);
                response = com.example.onlinetest.Domain.Mapper.toUpdateProductResponseDto(updated);
            } catch (IllegalArgumentException iae) {
                response.setSuccess(false);
                response.setMessage("Invalid productId format: " + productId);
                response.setProduct(null);
            } catch (Exception e) {
                throw new ProductException("Failed to update product: " + e.getMessage(), e);
            }
                return response;
        }
}