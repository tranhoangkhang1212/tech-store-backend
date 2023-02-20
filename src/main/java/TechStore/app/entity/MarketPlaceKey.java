package TechStore.app.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "MARKET_PLACE_KEY")
@Getter
@Setter
public class MarketPlaceKey extends Audit implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "PRIVATE_KEY")
    private String privateKey;
    @Column(name = "PUBLIC_KEY")
    private String publicKey;
    @Column(name = "CODE", unique = true)
    private String code;
}

