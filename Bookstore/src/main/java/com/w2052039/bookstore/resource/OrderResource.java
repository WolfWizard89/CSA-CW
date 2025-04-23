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

    // Post method to create a new order
    // It takes a customer ID as a path parameter and returns a Response object
    @POST
    public Response createOrder(@PathParam("customerId") int customerId) {
        if (!DataStore.customers.containsKey(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " not found."); //Exception for customer not found
        }

        Cart cart = DataStore.carts.get(customerId); // Retrieve the cart for the customer using the provided ID
        if (cart == null || cart.getItems().isEmpty()) {
            throw new CartNotFoundException("Cart for customer with ID " + customerId + " not found or empty."); //Exception for cart not found or empty
        }

        List<CartItem> items = new ArrayList<>(cart.getItems().values()); // Get the items in the cart
        double totalPrice = items.stream()
                .mapToDouble(item -> item.getBook().getPrice() * item.getQuantity()) // Calculate the total price of the order
                .sum(); 
        int orderId = DataStore.nextOrderId++; // Increment the next order ID
        Date date = new Date(); // Get the current date and time
        Order order = new Order(orderId, customerId, items, totalPrice, date); // Create a new Order object with the provided details
        DataStore.orders.put(orderId, order); // Store the order in the DataStore orders map
        cart.getItems().clear(); // Clear the cart after creating the order
        return Response.status(Response.Status.CREATED).entity(order).build(); // Return a response with status 201 (Created) and the created order object
    
    }

    // Get method to retrieve all orders for a specific customer
    // It takes a customer ID as a path parameter and returns a list of Order objects
    @GET
    public List<Order> getAllOrders(@PathParam("customerId") int customerId) {
        if (!DataStore.customers.containsKey(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " not found."); //Exception for customer not found
        }
        List<Order> orders = new ArrayList<>(); // Create a new list to store the orders for the customer
        for (Order order : DataStore.orders.values()) { // Iterate through all orders in the DataStore orders map
            if (order.getCustomerId() == customerId) {
                orders.add(order); // Add the order to the list if it belongs to the customer
            }
        }
        return orders;
    }

    // Get method to retrieve a specific order by ID for a specific customer
    // It takes a customer ID and an order ID as path parameters and returns the Order object
    @GET
    @Path("/{orderId}")
    public Order getOrderById(@PathParam("customerId") int customerId, @PathParam("orderId") int orderId) {
        if (!DataStore.customers.containsKey(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " not found."); //Exception for customer not found
        }
        Order order = DataStore.orders.get(orderId); // Retrieve the order from the DataStore orders map using the provided ID
        if (order == null || order.getCustomerId() != customerId) {
            throw new InvalidInputException("Order with ID " + orderId + " not found for customer with ID " + customerId + "."); //Exception for order not found or does not belong to the customer
        }
        return order;
        
    }


}
