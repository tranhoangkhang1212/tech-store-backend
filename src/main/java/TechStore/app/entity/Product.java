package TechStore.app.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "PRODUCT")
@Data
public class Product extends Audit implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "PRICE")
    private Integer price;

    @Column(name = "AMOUNT")
    private Integer amount;

    @Column(name = "CURRENCY")
    private String currency;

    @Column(name = "IMAGE_URL")
    private String imageUrl;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "BRAND_ID")
    private Integer brandId;

    @Column(name = "STAR")
    private Integer star;

    @Column(name = "PROMOTION")
    private Integer promotion;

    @Column(name = "IS_SALE")
    private Boolean isSale;

    @Column(name = "SALE_PERCENT")
    private Integer salePercent;

}
