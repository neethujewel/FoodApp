package com.Foodease.FoodeaseApp.rest;

import com.Foodease.FoodeaseApp.POJO.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepo extends JpaRepository<Cart,Long>
{

}
