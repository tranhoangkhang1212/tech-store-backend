package TechStore.app.service;

import TechStore.app.dto.response.ProductResponseDto;

import java.util.List;

public interface ProductService {
    List<ProductResponseDto> getAllProduct();
}
