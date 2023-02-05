package com.recipe.app.src.buy.models;

import com.recipe.app.src.receipt.models.Receipt;
import lombok.*;
import javax.persistence.*;
import com.recipe.app.common.entity.BaseEntity;

@NoArgsConstructor(access = AccessLevel.PUBLIC) // Unit Test 를 위해 PUBLIC
@EqualsAndHashCode(callSuper = false)
@Data // from lombok
@Entity // 필수, Class 를 Database Table화 해주는 것이다
@Table(name = "Buy") // Table 이름을 명시해주지 않으면 class 이름을 Table 이름으로 대체한다.
public class Buy extends BaseEntity {
    @Id // PK를 의미하는 어노테이션
    @Column(name = "buyIdx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer buyIdx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiptIdx", nullable = false)
    private Receipt receipt;

    @Column(name = "buyName", nullable = false, length = 45)
    private String buyName;

    @Column(name = "status", nullable = false, length = 10)
    private String status = "ACTIVE";

    public Buy(Receipt receipt, String buyName){
        this.receipt = receipt;
        this.buyName = buyName;
    }
}
