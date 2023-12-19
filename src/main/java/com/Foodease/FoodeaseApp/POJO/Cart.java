package com.Foodease.FoodeaseApp.POJO;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Cart
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(mappedBy ="cart",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<CartItem>items=new ArrayList<>();



}
