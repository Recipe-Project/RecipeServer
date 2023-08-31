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
@Table(name = "BlogScrap")
public class BlogScrapEntity {

    @Id
    @Column(name = "blogScrapId", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long blogScrapId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blogRecipeId")
    private BlogRecipeEntity blogRecipe;

    @CreatedDate
    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static BlogScrapEntity createBlogScrap(User user, BlogRecipe blogRecipe) {
        BlogScrapEntity blogScrapEntity = new BlogScrapEntity();
        blogScrapEntity.user = UserEntity.fromModel(user);
        blogScrapEntity.blogRecipe = BlogRecipeEntity.fromModel(blogRecipe);
        return blogScrapEntity;
    }
}
