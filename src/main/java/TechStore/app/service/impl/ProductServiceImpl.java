package TechStore.app.service.impl;

import TechStore.app.mapper.impl.ProductMapper;
import TechStore.app.dto.response.ProductResponseDto;
import TechStore.app.entity.Product;
import TechStore.app.repository.ProductRepository;
import TechStore.app.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    @Transactional(readOnly = true)
    @Override
    public List<ProductResponseDto> getAllProduct() {
        List<Product> productData = productRepository.findAll();
        return productMapper.toListDto((productData));
    }
}
