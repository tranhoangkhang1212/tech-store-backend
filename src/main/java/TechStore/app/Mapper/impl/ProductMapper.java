package TechStore.app.Mapper.impl;

import TechStore.app.Mapper.DtoMapper;
import TechStore.app.dto.response.ProductResponseDto;
import TechStore.app.entity.Product;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductMapper implements DtoMapper<Product, ProductResponseDto> {
    @Override
    public ProductResponseDto toDto(Product entity) {
        ProductResponseDto dto = new ProductResponseDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public List<ProductResponseDto> toListDto(List<Product> entities) {
        List<ProductResponseDto> dtos = new ArrayList<>();
        if(ObjectUtils.isEmpty(entities)) {
            return dtos;
        }
        for (Product entity : entities) {
            dtos.add(toDto(entity));
        }
        return dtos;
    }
}
