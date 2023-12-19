package com.Foodease.FoodeaseApp.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public interface CartService
{
    @Autowired
    private CartRepository cartRepository;
    public default void  updateQuantity(Long cartId, Long dishId, int newQuantity)
    {
    Cart cart=cartRepository.findById(cartId)
            .orElseThrow(()-> new EntityNotFoundException("cart not found"))
    CartItem cartItem=cart.getItems().stream()
            .filter(item->item.getDish().getId().equals(dishId))
            .findFirst()
            .orElseThrow(()->new EntityNotFoundException("Dish not found in the cart"));
    cartItem.setQuantity(newQuantity);
    cartRepository.save(cart);

    }

}
