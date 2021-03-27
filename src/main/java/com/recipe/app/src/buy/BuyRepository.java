package com.recipe.app.src.buy;

import com.recipe.app.src.buy.models.Buy;
import com.recipe.app.src.user.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository // => JPA => Hibernate => ORM => Database 객체지향으로 접근하게 해주는 도구이다
public interface BuyRepository extends CrudRepository<Buy, Integer> {
}
