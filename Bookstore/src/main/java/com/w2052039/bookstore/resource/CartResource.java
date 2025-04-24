/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.w2052039.bookstore.resource;

/**
 *
 * @author Wolf_Wizard
 */

import com.w2052039.bookstore.model.Book;
import com.w2052039.bookstore.model.Cart;
import com.w2052039.bookstore.model.CartItem;
import com.w2052039.bookstore.storage.DataStore;
import com.w2052039.bookstore.exception.BookNotFoundException;
import com.w2052039.bookstore.exception.CartNotFoundException;
import com.w2052039.bookstore.exception.InvalidInputException;
import com.w2052039.bookstore.exception.OutOfStockException;
import com.w2052039.bookstore.exception.CustomerNotFoundException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;


@Path("/customers/{customerId}/cart")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CartResource {

    private void  validateCustomer(int customerId) {
        if (!DataStore.customers.containsKey(customerId)) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " not found.");
        }
    }

    // Post method to add an item to the cart
    // It takes a customer ID and a CartItem object as input and returns a Response object
    @POST
    @Path("/items")
    public Response addItemToCart(@PathParam("customerId") int customerId, java.util.List<CartItem> itemsToAdd) {
        validateCustomer(customerId); // Validate customer ID

        if (itemsToAdd == null || itemsToAdd.isEmpty()) {
            throw new InvalidInputException("No cart items provided."); // Exception for invalid input
        }

        Cart cart = DataStore.carts.computeIfAbsent(customerId, k -> new Cart(customerId)); // Create a new cart if it doesn't exist
        Map<Integer, CartItem> items = cart.getItems(); // Get the items in the cart

        for (CartItem item : itemsToAdd) {
            if (item == null || item.getBook() == null) {
                throw new InvalidInputException("Invalid cart item provided."); // Exception for invalid input
            }

            int bookId = item.getBook().getId(); // Get the book ID from the CartItem object
            Book book = DataStore.books.get(bookId); // Retrieve the book from the DataStore books map using the provided ID
            if (book == null) {
                throw new BookNotFoundException("Book with ID " + bookId + " not found."); // Exception for book not found
            }
            if (book.getStock() < item.getQuantity()) {
                throw new OutOfStockException("Not enough stock for book with ID " + bookId + "."); // Exception for out of stock
            }
            if (item.getQuantity() <= 0) {
                throw new InvalidInputException("Quantity must be greater than zero."); // Exception for invalid quantity
            }
            if (items.containsKey(bookId)) {
                CartItem existingItem = items.get(bookId); // Get the existing item in the cart
                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity()); // Update the quantity of the existing item
            } else {
                items.put(bookId, new CartItem(book, item.getQuantity())); // Add the new item to the cart
            }
            book.setStock(book.getStock() - item.getQuantity()); // Decrease the stock of the book
        }
        return Response.status(Response.Status.CREATED).entity(cart).build(); // Return a response with status 201 (Created) and the updated cart object
    }
    
    // Get method to retrieve the cart for a specific customer
    // It takes a customer ID as a path parameter and returns the Cart object
    @GET
    public Cart getCart(@PathParam("customerId") int customerId) {
        validateCustomer(customerId); // Validate customer ID
        Cart cart = DataStore.carts.get(customerId); // Retrieve the cart from the DataStore carts map using the provided customer ID
        if (cart == null) {
            throw new CartNotFoundException("Cart for customer with ID " + customerId + " not found."); //Exception for cart not found
        }
        return cart;
    }

    // Get method to retrieve a specific item from the cart
    // It takes a customer ID and a book ID as path parameters and returns the CartItem object
    @PUT
    @Path("/items/{bookId}")
    public Response updateCartItem(@PathParam("customerId") int customerId, @PathParam("bookId") int bookId, CartItem item) {
        validateCustomer(customerId); // Validate customer ID
        Cart cart = DataStore.carts.get(customerId); // Retrieve the cart from the DataStore carts map using the provided customer ID
        Book book = DataStore.books.get(bookId); // Retrieve the book from the DataStore books map using the provided book ID
        if (cart == null) {
            throw new CartNotFoundException("Cart for customer with ID " + customerId + " not found."); //Exception for cart not found
        }
        if (book == null) { 
            throw new BookNotFoundException("Book with ID " + bookId + " not found."); //Exception for book not found
        }
        if (!cart.getItems().containsKey(bookId)) {
            throw new BookNotFoundException("Book with ID " + bookId + " not found in cart."); //Exception for book not found in cart
        }
        if (item.getQuantity() <= 0) {
            throw new InvalidInputException("Quantity must be greater than zero."); //Exception for invalid quantity
        }
        if (book.getStock() + cart.getItems().get(bookId).getQuantity() < item.getQuantity()) {
            throw new OutOfStockException("Not enough stock for book with ID " + bookId + "."); //Exception for out of stock
        }
        
        if (item.getQuantity() > cart.getItems().get(bookId).getQuantity()) {
            book.setStock(book.getStock() - (item.getQuantity() - cart.getItems().get(bookId).getQuantity())); // Decrease the stock of the book
        } else {
            book.setStock(book.getStock() + (cart.getItems().get(bookId).getQuantity() - item.getQuantity())); // Increase the stock of the book
        }
        cart.getItems().get(bookId).setQuantity(item.getQuantity()); // Update the quantity of the item in the cart
        return Response.status(Response.Status.OK).entity(cart).build(); // Return a response with status 200 (OK) and the updated cart object
    }

    // Delete method to remove an item from the cart
    // It takes a customer ID and a book ID as path parameters and returns a Response object
    @DELETE
    @Path("/items/{bookId}")
    public Response removeItemFromCart(@PathParam("customerId") int customerId, @PathParam("bookId") int bookId) {
        validateCustomer(customerId); // Validate customer ID
        Cart cart = DataStore.carts.get(customerId); // Retrieve the cart from the DataStore carts map using the provided customer ID
        Book book = DataStore.books.get(bookId); // Retrieve the book from the DataStore books map using the provided book ID
        if (cart == null) {
            throw new InvalidInputException("Cart for customer with ID " + customerId + " not found."); //Exception for cart not found
        }
        
        if (book == null) {
            throw new BookNotFoundException("Book with ID " + bookId + " not found."); //Exception for book not found
        }
        CartItem cartItem = cart.getItems().get(bookId);
        if (cartItem == null) {
            throw new BookNotFoundException("Book with ID " + bookId + " not found in cart."); //Exception for book not found in cart
        }
        int quantity = cartItem.getQuantity(); // Get the quantity of the item in the cart
        cart.getItems().remove(bookId);     // Remove the item from the cart
        book.setStock(book.getStock() + quantity); // Increase the stock of the book
        return Response.status(Response.Status.NO_CONTENT).build(); // Return a response with status 204 (No Content) indicating successful deletion
    }


}
