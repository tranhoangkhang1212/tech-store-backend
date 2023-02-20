package TechStore.app.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.util.Date;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Audit {

    @CreatedBy
    @Column(name = "CREATED_BY")

    protected String createdBy;

    @LastModifiedBy
    @Column(name = "UPDATED_BY")
    protected String updatedBy;

    @CreatedDate
    @Column(name = "CREATED_AT")
    protected Date createdAt;

    @LastModifiedDate
    @Column(name = "UPDATED_AT")
    protected Date updatedAt;

    @Version
    private Long version;
}
