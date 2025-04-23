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
import com.w2052039.bookstore.storage.DataStore;
import com.w2052039.bookstore.exception.AuthorNotFoundException;
import com.w2052039.bookstore.exception.BookNotFoundException;
import com.w2052039.bookstore.exception.InvalidInputException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookResource {

    
    // Post method to create a new book
    // It takes a Book object as input and returns a Response object
    @POST
    public Response createBook (Book book) {
    
        if (book == null || book.getTitle() == null || book.getIsbn() == null|| book.getPrice() == 0 || book.getPublicationYear() == 0 ) {
            throw new InvalidInputException("Invalid book data provided or something missing."); //Exception for invalid input
        }
        
        if (!DataStore.authors.containsKey(book.getAuthorId())) {
            throw new AuthorNotFoundException("Author not found."); //Exception for author not found
        }

        int id = DataStore.nextBookId++; // Increment the next book ID
        book.setId(id); // Set the ID for the new book
        book.setAuthorName(book.getAuthorId() != 0 ? DataStore.authors.get(book.getAuthorId()).getName() : null); // Set the author name for the new book
        DataStore.books.put(id, book); // Store the book in the DataStore books map
        return Response.status(Response.Status.CREATED).entity(book).build(); // Return a response with status 201 (Created) and the created book object
    
    }

    // Get method to retrieve all books
    // It returns a collection of Book objects
    @GET
    public Collection<Book> getAllBooks() {
        return DataStore.books.values(); // Return all books from the DataStore books map
    }

    // Get method to retrieve a specific book by ID
    // It takes a book ID as a path parameter and returns the Book object
    @GET
    @Path("/{id}")
    public Book getBookById(@PathParam("id") int id) {
        Book book = DataStore.books.get(id); // Retrieve the book from the DataStore books map using the provided ID
        if (book == null) {
            throw new BookNotFoundException("Book with ID " + id + " not found."); //Exception for book not found
        }
        return book;
    }

    // Put method to update an existing book
    // It takes a book ID as a path parameter and a Book object as input
    @PUT
    @Path("/{id}")
    public Response updateBook(@PathParam("id") int id, Book updatedBook) {
        
        Book existingBook = DataStore.books.get(id); // Retrieve the existing book from the DataStore books map using the provided ID
        if (existingBook == null) {
            throw new BookNotFoundException("Book with ID " + id + " not found."); //Exception for book not found
        }

        if (updatedBook.getTitle() != null) {
            existingBook.setTitle(updatedBook.getTitle()); // Update the book's title if provided
        }
        if (updatedBook.getIsbn() != null) {
            existingBook.setIsbn(updatedBook.getIsbn()); // Update the book's ISBN if provided
        }
        if (updatedBook.getAuthorId() != 0) {
            if (!DataStore.authors.containsKey(updatedBook.getAuthorId())) {
                throw new AuthorNotFoundException("Author not found."); //Exception for author not found
            }
            existingBook.setAuthorId(updatedBook.getAuthorId()); // Update the book's author ID if provided
            existingBook.setAuthorName(DataStore.authors.get(updatedBook.getAuthorId()).getName()); // Update the book's author name if provided
        }
        if (updatedBook.getPublicationYear() != 0) {
            existingBook.setPublicationYear(updatedBook.getPublicationYear()); // Update the book's publication year if provided
        }
        if (updatedBook.getPrice() != 0) {
            existingBook.setPrice(updatedBook.getPrice()); // Update the book's price if provided
        }
        if (updatedBook.getStock() != 0) {
            existingBook.setStock(updatedBook.getStock()); // Update the book's stock if provided
        }
        DataStore.books.put(id, existingBook); // Store the updated book in the DataStore books map
        return Response.status(Response.Status.OK).entity(existingBook).build(); // Return a response with status 200 (OK) and the updated book object
    }

    // Delete method to delete a book by ID
    // It takes a book ID as a path parameter and returns a Response object
    @DELETE
    @Path("/{id}")
    public Response deleteBook(@PathParam("id") int id) {
        Book book = DataStore.books.remove(id); // Remove the book from the DataStore books map using the provided ID
        if (book == null) {
            throw new BookNotFoundException("Book with ID " + id + " not found."); //Exception for book not found
        }
        return Response.status(Response.Status.NO_CONTENT).build(); // Return a response with status 204 (No Content) indicating successful deletion
    }

    
}
