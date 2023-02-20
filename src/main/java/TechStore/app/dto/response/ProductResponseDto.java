package TechStore.app.dto.response;

import lombok.Data;

@Data
public class ProductResponseDto {
    private Long id;
    private String name;
    private Integer price;
    private Integer amount;
    private String currency;
    private String imageUrl;
    private String description;
    private Integer brandId;
    private Integer star;
    private Integer promotion;
    private Boolean isSale;
    private Integer salePercent;
}
