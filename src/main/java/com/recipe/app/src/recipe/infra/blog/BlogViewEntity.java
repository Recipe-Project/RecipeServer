package com.recipe.app.src.recipe.infra.blog;

import com.recipe.app.src.recipe.domain.BlogRecipe;
import com.recipe.app.src.user.domain.User;
import com.recipe.app.src.user.infra.UserEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "BlogView")
public class BlogViewEntity {

    @Id
    @Column(name = "blogViewId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long blogViewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    private BlogRecipeEntity blogRecipe;

    @CreatedDate
    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static BlogViewEntity create(User user, BlogRecipe blogRecipe) {
        BlogViewEntity blogViewEntity = new BlogViewEntity();
        blogViewEntity.user = UserEntity.fromModel(user);
        blogViewEntity.blogRecipe = BlogRecipeEntity.fromModel(blogRecipe);
        return blogViewEntity;
    }
}
