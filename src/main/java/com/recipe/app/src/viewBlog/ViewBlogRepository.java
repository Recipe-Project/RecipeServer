package com.recipe.app.src.viewBlog;

import com.recipe.app.src.viewBlog.models.ViewBlog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViewBlogRepository extends CrudRepository<ViewBlog, Integer>{
}
