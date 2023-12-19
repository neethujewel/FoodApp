package com.Foodease.FoodeaseApp.dao;

import com.Foodease.FoodeaseApp.POJO.UserCart;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartDao extends CrudRepository<UserCart,Integer> {
}
