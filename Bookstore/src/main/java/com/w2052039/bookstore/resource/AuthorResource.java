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

    @POST
    public Response createAuthor(Author author) {
        if (author == null || author.getName() == null || author.getBiography() == null) {
            throw new InvalidInputException("Invalid author data provided or something missing.");
        }

        int id = DataStore.nextAuthorId++;
        author.setId(id);
        DataStore.authors.put(id, author);
        return Response.status(Response.Status.CREATED).entity(author).build();
    }

    @GET
    public Collection<Author> getAllAuthors() {
        return DataStore.authors.values();
    }

    @GET
    @Path("/{id}")
    public Author getAuthorById(@PathParam("id") int id) {
        Author author = DataStore.authors.get(id);
        if (author == null) {
            throw new AuthorNotFoundException("Author with ID " + id + " not found.");
        }
        return author;
    }

    @PUT
    @Path("/{id}")
    public Response updateAuthor(@PathParam("id") int id, Author author) {
        
        Author existingAuthor = DataStore.authors.get(id);

        if (existingAuthor == null) {
            throw new AuthorNotFoundException("Author with ID " + id + " not found.");
        }
        if (author.getName() != null){
            existingAuthor.setName(author.getName());
        }
        if (author.getBiography() != null){
            existingAuthor.setBiography(author.getBiography());
        }
        DataStore.authors.put(id, existingAuthor);
        return Response.status(Response.Status.OK).entity(existingAuthor).build();
        
    }

    @DELETE
    @Path("/{id}")
    public Response deleteAuthor(@PathParam("id") int id) {
        Author author = DataStore.authors.remove(id);
        if (author == null) {
            throw new AuthorNotFoundException("Author with ID " + id + " not found.");
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }
    
    @GET
    @Path("/{id}/books")
    public List<Book> getBooksByAuthor(@PathParam("id") int id) {
        if (!DataStore.authors.containsKey(id)) {
            throw new AuthorNotFoundException("Author with ID " + id + " not found.");
        }
        List<Book> booksByAuthor = new ArrayList<>();
        for (Book book : DataStore.books.values()) {
            if (book.getAuthorId() == id) {
                booksByAuthor.add(book);
            }
        }
        return booksByAuthor;
    
    }
    
}
