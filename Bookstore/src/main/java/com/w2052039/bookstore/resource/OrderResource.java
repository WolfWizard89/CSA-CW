/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.w2052039.bookstore.resource;

/**
 *
 * @author Wolf_Wizard
 */
import com.w2052039.bookstore.model.Order;
import com.w2052039.bookstore.model.Cart;
import com.w2052039.bookstore.model.CartItem;
import com.w2052039.bookstore.storage.DataStore;
import com.w2052039.bookstore.exception.CustomerNotFoundException;
import com.w2052039.bookstore.exception.InvalidInputException;
import com.w2052039.bookstore.exception.CartNotFoundException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



@Path("/customers/{customerId}/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    @POST
    public Response createOrder(@PathParam("customerId") int customerId) {
        if (!DataStore.customers.containsKey(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " not found.");
        }

        Cart cart = DataStore.carts.get(customerId);
        if (cart == null || cart.getItems().isEmpty()) {
            throw new CartNotFoundException("Cart for customer with ID " + customerId + " not found or empty.");
        }

        List<CartItem> items = new ArrayList<>(cart.getItems().values());
        double totalPrice = items.stream()
                .mapToDouble(item -> item.getBook().getPrice() * item.getQuantity())
                .sum();
        int orderId = DataStore.nextOrderId++;
        Date date = new Date();
        Order order = new Order(orderId, customerId, items, totalPrice, date);
        DataStore.orders.put(orderId, order);
        cart.getItems().clear();
        return Response.status(Response.Status.CREATED).entity(order).build();
    
    }

    @GET
    public List<Order> getAllOrders(@PathParam("customerId") int customerId) {
        if (!DataStore.customers.containsKey(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " not found.");
        }
        List<Order> orders = new ArrayList<>();
        for (Order order : DataStore.orders.values()) {
            if (order.getCustomerId() == customerId) {
                orders.add(order);
            }
        }
        return orders;
    }

    @GET
    @Path("/{orderId}")
    public Order getOrderById(@PathParam("customerId") int customerId, @PathParam("orderId") int orderId) {
        if (!DataStore.customers.containsKey(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " not found.");
        }
        Order order = DataStore.orders.get(orderId);
        if (order == null || order.getCustomerId() != customerId) {
            throw new InvalidInputException("Order with ID " + orderId + " not found for customer with ID " + customerId + ".");
        }
        return order;
        
    }


}
