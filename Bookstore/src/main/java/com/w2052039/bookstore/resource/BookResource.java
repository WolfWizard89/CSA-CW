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

    

    @POST
    public Response createBook (Book book) {
    
        if (book == null || book.getTitle() == null || book.getIsbn() == null|| book.getPrice() == 0 || book.getPublicationYear() == 0 ) {
            throw new InvalidInputException("Invalid book data provided.");
        }
        
        if (!DataStore.authors.containsKey(book.getAuthorId())) {
            throw new AuthorNotFoundException("Author not found.");
        }

        int id = DataStore.nextBookId++;
        book.setId(id);
        book.setAuthorName(book.getAuthorId() != 0 ? DataStore.authors.get(book.getAuthorId()).getName() : null);
        DataStore.books.put(id, book);
        return Response.status(Response.Status.CREATED).entity(book).build();
    
    }

    @GET
    public Collection<Book> getAllBooks() {
        return DataStore.books.values();
    }

    @GET
    @Path("/{id}")
    public Book getBookById(@PathParam("id") int id) {
        Book book = DataStore.books.get(id);
        if (book == null) {
            throw new BookNotFoundException("Book with ID " + id + " not found.");
        }
        return book;
    }

    @PUT
    @Path("/{id}")
    public Response updateBook(@PathParam("id") int id, Book updatedBook) {
        
        Book existingBook = DataStore.books.get(id);
        if (existingBook == null) {
            throw new BookNotFoundException("Book with ID " + id + " not found.");
        }

        if (updatedBook.getTitle() != null) {
            existingBook.setTitle(updatedBook.getTitle());
        }
        if (updatedBook.getIsbn() != null) {
            existingBook.setIsbn(updatedBook.getIsbn());
        }
        if (updatedBook.getAuthorId() != 0) {
            if (!DataStore.authors.containsKey(updatedBook.getAuthorId())) {
                throw new AuthorNotFoundException("Author not found.");
            }
            existingBook.setAuthorId(updatedBook.getAuthorId());
            existingBook.setAuthorName(DataStore.authors.get(updatedBook.getAuthorId()).getName());
        }
        if (updatedBook.getPublicationYear() != 0) {
            existingBook.setPublicationYear(updatedBook.getPublicationYear());
        }
        if (updatedBook.getPrice() != 0) {
            existingBook.setPrice(updatedBook.getPrice());
        }
        if (updatedBook.getStock() != 0) {
            existingBook.setStock(updatedBook.getStock());
        }
        DataStore.books.put(id, existingBook);
        return Response.status(Response.Status.OK).entity(existingBook).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteBook(@PathParam("id") int id) {
        Book book = DataStore.books.remove(id);
        if (book == null) {
            throw new BookNotFoundException("Book with ID " + id + " not found.");
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }

    
}
