/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.w2052039.bookstore.model;


import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Wolf_Wizard
 */
public class Cart {
    
    private int customerId;
    private Map<Integer, CartItem> items = new HashMap<>();

    public Cart() {}

    public Cart(int customerId) {
        this.customerId = customerId;
    }
    public int getCustomerId() {
        return customerId;
    }
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    public Map<Integer, CartItem> getItems() {
        return items;
    }
    public void setItems(Map<Integer, CartItem> items) {
        this.items = items;
    }

}
