package com.assignment.warehouse.repository;

import com.assignment.warehouse.model.entity.Articles;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticlesRepository extends CrudRepository<Articles, Integer> {


}
