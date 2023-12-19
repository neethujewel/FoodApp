package com.Foodease.FoodeaseApp.rest;

import com.Foodease.FoodeaseApp.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/cart")
public interface CartEnd
{
    @Autowired
    CartService cartService = null;
    @PutMapping("/{cartId}/updateQuantity/{dishId}")
    public default ResponseEntity<String>updateQuantity(
            @PathVariable Long cartId,
            @PathVariable Long dishId,
            @RequestParam int newQuantity) {
        cartService.updateQuantity(cartId,dishId,newQuantity);
        return ResponseEntity.ok("Quantity updated successfully");

    }

}
