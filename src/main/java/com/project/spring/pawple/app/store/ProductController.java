package com.project.spring.pawple.app.store;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/store/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<ProductDto> getAllProducts() {
        return productService.findAll()
                .stream()
                .map(ProductDto::fromEntity)
                .collect(Collectors.toList());
    }

    @GetMapping("/category/{category}")
    public List<ProductDto> getProductsByCategory(@PathVariable String category) {
        return productService.findByCategory(category)
                .stream()
                .map(ProductDto::fromEntity)
                .collect(Collectors.toList());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductDto createProduct(
        @RequestPart("data") ProductDto dto,
        @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = productService.storeImage(image); // 저장 경로 리턴
            dto.setImage(imageUrl);
        }

        ProductEntity saved = productService.save(dto.toEntity());
        return ProductDto.fromEntity(saved);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProductDto updateProduct(
        @PathVariable Long id,
        @RequestPart("data") ProductDto dto,
        @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        if (image != null && !image.isEmpty()) {
            String imageUrl = productService.storeImage(image);
            dto.setImage(imageUrl);
        }

        ProductEntity updated = productService.update(id, dto.toEntity());
        return ProductDto.fromEntity(updated);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.delete(id);
    }

    @GetMapping("/{id}")
    public ProductDto getProductById(@PathVariable Long id) {
        ProductEntity product = productService.findById(id);
        return ProductDto.fromEntity(product);
    }

    // ✅ 1. 장바구니 담기
    @PostMapping("/cart/add")
    public String addToCart(@RequestBody ProductDto dto, HttpSession session) {
        List<ProductDto> cart = (List<ProductDto>) session.getAttribute("cart");

        if (cart == null) {
            cart = new ArrayList<>();
        }

        // 같은 상품 있으면 수량만 추가
        boolean found = false;
        for (ProductDto item : cart) {
            if (item.getId().equals(dto.getId())) {
                item.setQuantity(item.getQuantity() + dto.getQuantity());
                found = true;
                break;
            }
        }

        if (!found) {
            cart.add(dto);
        }

        session.setAttribute("cart", cart);
        return "장바구니에 담겼습니다.";
    }

    // ✅ 2. 장바구니 조회
    @GetMapping("/cart")
    public List<ProductDto> getCart(HttpSession session) {
        List<ProductDto> cart = (List<ProductDto>) session.getAttribute("cart");
        return cart == null ? new ArrayList<>() : cart;
    }

    // ✅ 3. 장바구니에서 상품 제거
    @DeleteMapping("/cart/{productId}")
    public String removeFromCart(@PathVariable Long productId, HttpSession session) {
        List<ProductDto> cart = (List<ProductDto>) session.getAttribute("cart");

        if (cart != null) {
            cart.removeIf(item -> item.getId().equals(productId));
            session.setAttribute("cart", cart);
        }

        return "장바구니에서 제거되었습니다.";
    }

    // ✅ (선택) 장바구니 비우기
    @DeleteMapping("/cart/clear")
    public String clearCart(HttpSession session) {
        session.removeAttribute("cart");
        return "장바구니가 비워졌습니다.";
    }

}
