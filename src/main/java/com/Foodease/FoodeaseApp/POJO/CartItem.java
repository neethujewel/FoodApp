package com.Foodease.FoodeaseApp.POJO;

import jakarta.persistence.*;

@Entity
public class CartItem
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "dish_id")
    private  Dish dish;
    private  int quantity;
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;


}
