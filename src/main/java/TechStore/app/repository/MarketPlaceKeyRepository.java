package TechStore.app.repository;

import TechStore.app.entity.MarketPlaceKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MarketPlaceKeyRepository extends JpaRepository<MarketPlaceKey, Integer> {
    MarketPlaceKey findByCode(String code);

    @Modifying
    @Query(value = "TRUNCATE TABLE MARKET_PLACE_KEY", nativeQuery = true)
    void truncate();
}
