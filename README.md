# Online Test Backend Service - E-commerce Features Overview

This Spring Boot service exposes REST APIs for an e-commerce-like application. It follows these conventions:
- Controllers under `Controller` package
- DTOs under `Domain/Dto`
- Services use interfaces (prefixed with `I`) + implementations
- Persistence via Spring Data JPA entities under `Repo`
- Centralized mapping in `Domain/Mapper.java`
- Centralized exception handling in `Domain/Exceptions/GlobalExceptionHandler.java`
- JWT-based access control via filters in `Configuration`

## Authentication & Security
- Endpoints requiring authentication are configured in `Configuration/SecurityConfig.java`.
- Single JWT filter: `JwtAccessFilter` validates Bearer tokens and enforces roles for sensitive endpoints.
- Filter registration in `Configuration/FilterConfig.java` protects:
  - POST /api/products (ADMIN or SELLER)
  - /api/admin/** (ADMIN)
  - /api/cart/**, /api/user/address/**, /api/user/wishlist/**, /api/checkout, /api/orders/** (any valid token)
- Login attempts and last login tracking in `Service/AuthenticationService.java`:
  - Increments `loginAttempts` on failed login for existing user
  - Resets `loginAttempts` and sets `lastLogin` on successful login

## Product Catalog
- Controller: `Controller/ProductController.java`
  - GET /api/products?page=1&category=...
  - POST /api/products (ADMIN/SELLER via JWT)
  - GET /api/products/getProduct?productId=...
  - PATCH /api/products/updateProduct?productId=...
- Service: `Service/IProductService.java`, `Service/ProductService.java`
- Entities: `Repo/Product.java`, `Repo/Category.java`
- DTOs: `CreateProductRequestDto`, `CreateProductResponseDto`, `ProductDto`, `ProductResponseDto`, `ProductsListResponseDto`, `UpdateProductRequestDto`, `UpdateProductResponseDto`
- Mapping: `Domain/Mapper.java`
- Exceptions: `Domain/Exceptions/ProductException.java`

## User Profiles & Auth
- Auth Controller: `Controller/AuthController.java` (login/register)
- Profile Controller: `Controller/UserProfileController.java`
- Services: `Service/IUserAuthenticationService.java`, `Service/AuthenticationService.java`, `Service/IUserProfileService.java`
- Entity: `Repo/User.java` (cardinality to Address, Cart, Wishlist)
- Repo: `Repo/UserRepo.java`

## Cart
- Controller: `Controller/CartController.java`
  - GET /api/cart/view/{userName}
  - PATCH /api/cart/update/{userName}
- Service: `Service/ICartService.java`, `Service/CartService.java`
- Repos/Entities: `Repo/Cart.java`, `Repo/CartItem.java`, `Repo/CartRepo.java`
- DTOs: `CartItemDto`, `CartViewResponseDto`, `CartUpdateRequestDto`, `CartUpdateItemDto`, `CartUpdateResponseDto`
- Mapping: `Domain/Mapper.java` (toCartItemDto, toCartItemDtoList, toCartViewResponseDto)
- Exceptions: `Domain/Exceptions/CartException.java`

## Wishlist
- Controller: `Controller/WishlistController.java`
  - GET /api/user/wishlist/view/{userName}
  - POST /api/user/wishlist/add/{userName}
  - DELETE /api/user/wishlist/remove/{userName}
  - POST /api/user/wishlist/move-to-cart/{userName}
- Service: `Service/IWishlistService.java`, `Service/WishlistService.java`
- Repos/Entities: `Repo/Wishlist.java`, `Repo/WishlistItem.java`, `Repo/WishlistRepo.java`
- DTOs: `WishlistItemDto`, `WishlistViewResponseDto`, `WishlistModifyRequestDto`, `WishlistModifyResponseDto`
- Mapping: `Domain/Mapper.java` (wishlist mappings)

## Addresses
- Controller: `Controller/AddressController.java`
  - GET /api/user/address/{username}
  - POST /api/user/address/{username}
  - PATCH /api/user/address/{username}/{addressId}
  - DELETE /api/user/address/{username}/{addressId}
- Service: `Service/IAddressService.java`, `Service/AddressService.java`
- Repos/Entities: `Repo/Address.java`, `Repo/AddressRepo.java`
- DTOs: `AddressDto`
- Mapping: `Domain/Mapper.java` (address mappings)

## Checkout
- Controller: `Controller/CheckoutController.java`
  - POST /api/checkout
- Service: `Service/ICheckoutService.java`, `Service/CheckoutService.java`
- Repos/Entities: `Repo/Order.java`, `Repo/OrderItem.java`, `Repo/OrderRepo.java`, `Repo/ShippingMethod.java`
- DTOs: `CheckoutRequestDto`, `CheckoutResponseDto`
- Behavior: Converts cart to order, calculates subtotal/shipping/total, saves order, clears cart

## Orders
- Controller: `Controller/OrderController.java`
  - GET /api/orders/{username}
  - GET /api/orders/{username}/{orderId}
  - PATCH /api/orders/{username}/{orderId}/cancel
  - PATCH /api/orders/{username}/{orderId}/return
- Service: `Service/IOrderService.java`, `Service/OrderService.java`
- Repo: `Repo/OrderRepo.java` with `findByUserUsername`
- Status enum: `Repo/OrderStatus.java` (PLACED, CANCELLED, RETURN_REQUESTED, RETURNED)
- DTOs: `OrderSummaryDto`, `OrderDetailDto`, `OrderItemDto`
- Mapping: `Domain/Mapper.java` (order mappings)

## Exception Handling
- Centralized in `Domain/Exceptions/GlobalExceptionHandler.java`
  - Handles domain-specific exceptions (`ProductException`, `CartException`, `OrderException`, etc.)
  - Returns structured DTO responses with `success` and `message` fields where applicable

## Model Getters/Setters
- Entities use Lombok `@Getter/@Setter` where applicable (Cart, CartItem, Wishlist, WishlistItem, Order, OrderItem, Product, User, Address).

## OpenAPI / Swagger
- Swagger UI available at `/swagger-ui/index.html`
- OpenAPI JSON at `/v3/api-docs`

## Build & Run
- Build: `mvnw.cmd clean package`
- Run: `mvnw.cmd spring-boot:run` or run the built jar

## Notes
- JWT secret and expiration configured in `application.properties`.
- DB configured for Microsoft SQL Server.
- CORS restricted to `http://localhost:3000` in `Configuration/CorsConfig.java`.

---
If you need client-side integration examples (React), specify which pages/components to update and I’ll outline the fetch calls and state handling.
