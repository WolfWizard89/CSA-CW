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

    @POST
    @Path("/items")
    public Response addItemToCart(@PathParam("customerId") int customerId, CartItem item) {
        validateCustomer(customerId);

        if (item == null || item.getBook() == null) {
            throw new InvalidInputException("Invalid cart item provided.");
        }
        
        int bookId = item.getBook().getId();
        Book book = DataStore.books.get(bookId);
        if (book == null) {
            throw new BookNotFoundException("Book with ID " + bookId + " not found.");
        }
        if (book.getStock() < item.getQuantity()) {
            throw new OutOfStockException("Not enough stock for book with ID " + bookId + ".");
        }
        if (item.getQuantity() <= 0) {
            throw new InvalidInputException("Quantity must be greater than zero.");
        }
        Cart cart = DataStore.carts.computeIfAbsent(customerId, k -> new Cart(customerId));
        Map<Integer, CartItem> items = cart.getItems();
        if (items.containsKey(bookId)) {
            CartItem existingItem = items.get(bookId);
            existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
        } else {
            items.put(bookId, new CartItem(book, item.getQuantity()));
        }
        book.setStock(book.getStock() - item.getQuantity());
        return Response.status(Response.Status.CREATED).entity(cart).build();
    }
    /*
     *
     * Example JSON for adding an item to the cart:
     {
    "book": {
            "id": 3
            },
    "quantity": 3
    }
     */

    @GET
    public Cart getCart(@PathParam("customerId") int customerId) {
        validateCustomer(customerId);
        Cart cart = DataStore.carts.get(customerId);
        if (cart == null) {
            throw new CartNotFoundException("Cart for customer with ID " + customerId + " not found.");
        }
        return cart;
    }

    @PUT
    @Path("/items/{bookId}")
    public Response updateCartItem(@PathParam("customerId") int customerId, @PathParam("bookId") int bookId, CartItem item) {
        validateCustomer(customerId);
        Cart cart = DataStore.carts.get(customerId);
        if (cart == null) {
            throw new CartNotFoundException("Cart for customer with ID " + customerId + " not found.");
        }
        if (!cart.getItems().containsKey(bookId)) {
            throw new BookNotFoundException("Book with ID " + bookId + " not found in cart.");
        }
        Book book = DataStore.books.get(bookId);
        if (book == null) {
            throw new BookNotFoundException("Book with ID " + bookId + " not found.");
        }
        if (item.getQuantity() <= 0) {
            throw new InvalidInputException("Quantity must be greater than zero.");
        }
        if (book.getStock() + cart.getItems().get(bookId).getQuantity() < item.getQuantity()) {
            throw new OutOfStockException("Not enough stock for book with ID " + bookId + ".");
        }
        
        if (item.getQuantity() > cart.getItems().get(bookId).getQuantity()) {
            book.setStock(book.getStock() - (item.getQuantity() - cart.getItems().get(bookId).getQuantity()));
        } else {
            book.setStock(book.getStock() + (cart.getItems().get(bookId).getQuantity() - item.getQuantity()));
        }
        cart.getItems().get(bookId).setQuantity(item.getQuantity());
        return Response.status(Response.Status.OK).entity(cart).build();
    }

    @DELETE
    @Path("/items/{bookId}")
    public Response removeItemFromCart(@PathParam("customerId") int customerId, @PathParam("bookId") int bookId) {
        validateCustomer(customerId);
        Cart cart = DataStore.carts.get(customerId);
        if (cart == null || !cart.getItems().containsKey(bookId)) {
            throw new InvalidInputException("Cart for customer with ID " + customerId + " not found.");
        }
        Book book = DataStore.books.get(bookId);
        if (book == null) {
            throw new BookNotFoundException("Book with ID " + bookId + " not found.");
        }
        int quantity = cart.getItems().get(bookId).getQuantity();
        cart.getItems().remove(bookId);
        book.setStock(book.getStock() + quantity);
        return Response.status(Response.Status.NO_CONTENT).build();
    }


}
