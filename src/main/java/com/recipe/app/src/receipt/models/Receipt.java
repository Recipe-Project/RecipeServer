package com.recipe.app.src.receipt.models;

import com.recipe.app.src.buy.models.Buy;
import com.recipe.app.src.user.models.User;
import lombok.*;
import javax.persistence.*;
import com.recipe.app.config.BaseEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PUBLIC) // Unit Test 를 위해 PUBLIC
@EqualsAndHashCode(callSuper = false)
@Data // from lombok
@Entity // 필수, Class 를 Database Table화 해주는 것이다
@Table(name = "Receipt") // Table 이름을 명시해주지 않으면 class 이름을 Table 이름으로 대체한다.
public class Receipt extends BaseEntity {
    @Id // PK를 의미하는 어노테이션
    @Column(name = "receiptIdx", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer receiptIdx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userIdx", nullable = false)
    private User user;

    @Column(name = "title", nullable = false, length = 45)
    private String title;

    @Column(name = "receiptDate", nullable = false, length = 45)
    private Date receiptDate;

    @Column(name = "status", nullable = false, length = 10)
    private String status = "ACTIVE";

    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL)
    private List<Buy> buys = new ArrayList<>();

    public Receipt(User user, String title, Date receiptDate){
        this.user = user;
        this.title = title;
        this.receiptDate = receiptDate;
    }
}
