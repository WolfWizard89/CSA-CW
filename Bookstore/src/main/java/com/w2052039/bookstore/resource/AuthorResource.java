/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.w2052039.bookstore.resource;

/**
 *
 * @author Wolf_Wizard
 */

import com.w2052039.bookstore.model.Author;
import com.w2052039.bookstore.model.Book;
import com.w2052039.bookstore.storage.DataStore;
import com.w2052039.bookstore.exception.AuthorNotFoundException;
import com.w2052039.bookstore.exception.InvalidInputException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;


@Path("/authors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthorResource {

    // Post method to create a new author
    // It takes an Author object as input and returns a Response object
    @POST
    public Response createAuthor(Author author) {
        if (author == null || author.getName() == null || author.getBiography() == null) {
            throw new InvalidInputException("Invalid author data provided or something missing."); //Exception for invalid input
        }

        int id = DataStore.nextAuthorId++; // Increment the next author ID
        author.setId(id); // Set the ID for the new author
        DataStore.authors.put(id, author); // Store the author in the DataStore authors map
        return Response.status(Response.Status.CREATED).entity(author).build(); // Return a response with status 201 (Created) and the created author object
    }

    // Get method to retrieve all authors
    // It returns a collection of Author objects
    @GET
    public Collection<Author> getAllAuthors() { 
        return DataStore.authors.values(); // Return all authors from the DataStore authors map
    }

    // Get method to retrieve a specific author by ID
    // It takes an author ID as a path parameter and returns the Author object
    @GET
    @Path("/{id}")
    public Author getAuthorById(@PathParam("id") int id) {
        Author author = DataStore.authors.get(id); // Retrieve the author from the DataStore authors map using the provided ID
        if (author == null) {
            throw new AuthorNotFoundException("Author with ID " + id + " not found."); //Exception for author not found
        }
        return author;
    }

    // Put method to update an existing author
    // It takes an author ID as a path parameter and an Author object as input
    @PUT
    @Path("/{id}")
    public Response updateAuthor(@PathParam("id") int id, Author author) {
        
        Author existingAuthor = DataStore.authors.get(id); // Retrieve the existing author from the DataStore authors map using the provided ID

        if (existingAuthor == null) {
            throw new AuthorNotFoundException("Author with ID " + id + " not found."); //Exception for author not found
        }
        if (author.getName() != null){
            existingAuthor.setName(author.getName()); // Update the author's name if provided
        }
        if (author.getBiography() != null){
            existingAuthor.setBiography(author.getBiography()); // Update the author's biography if provided
        }
        DataStore.authors.put(id, existingAuthor); // Store the updated author in the DataStore authors map
        return Response.status(Response.Status.OK).entity(existingAuthor).build(); // Return a response with status 200 (OK) and the updated author object
        
    }

    // Delete method to remove an author by ID
    // It takes an author ID as a path parameter and returns a Response object
    @DELETE
    @Path("/{id}")
    public Response deleteAuthor(@PathParam("id") int id) {
        Author author = DataStore.authors.remove(id); // Remove the author from the DataStore authors map using the provided ID
        if (author == null) {
            throw new AuthorNotFoundException("Author with ID " + id + " not found."); //Exception for author not found
        }
        return Response.status(Response.Status.NO_CONTENT).build(); // Return a response with status 204 (No Content) indicating successful deletion
    }
    
    // Get method to retrieve all books by a specific author
    // It takes an author ID as a path parameter and returns a list of Book objects
    @GET
    @Path("/{id}/books")
    public List<Book> getBooksByAuthor(@PathParam("id") int id) {
        if (!DataStore.authors.containsKey(id)) {
            throw new AuthorNotFoundException("Author with ID " + id + " not found."); //Exception for author not found
        }
        List<Book> booksByAuthor = new ArrayList<>(); // Create a list to store books by the specified author
        for (Book book : DataStore.books.values()) {
            if (book.getAuthorId() == id) {
                booksByAuthor.add(book); // Add the book to the list if its author ID matches the specified ID
            }
        }
        return booksByAuthor;
    
    }
    
}
