/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.w2052039.bookstore.resource;

/**
 *
 * @author Wolf_Wizard
 */

import com.w2052039.bookstore.model.Customer;
import com.w2052039.bookstore.storage.DataStore;
import com.w2052039.bookstore.exception.CustomerNotFoundException;
import com.w2052039.bookstore.exception.InvalidInputException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerResource {

    // Post method to create a new customer
    // It takes a Customer object as input and returns a Response object
    @POST
    public Response createCustomer(Customer customer) {
        if (customer == null || customer.getName() == null || customer.getEmail() == null || customer.getPassword() == null) {
            throw new InvalidInputException("Name, email, and password are required."); //Exception for invalid input
        }
        int id = DataStore.nextCustomerId++; // Increment the next customer ID
        customer.setId(id); // Set the ID for the new customer
        DataStore.customers.put(id, customer); // Store the customer in the DataStore customers map
        return Response.status(Response.Status.CREATED).entity(customer).build(); // Return a response with status 201 (Created) and the created customer object
    }

    // Get method to retrieve all customers
    // It returns a collection of Customer objects
    @GET
    public Collection<Customer> getAllCustomers() {
        return DataStore.customers.values(); // Return all customers from the DataStore customers map
    }

    // Get method to retrieve a specific customer by ID
    // It takes a customer ID as a path parameter and returns the Customer object
    @GET
    @Path("/{id}")
    public Customer getCustomerById(@PathParam("id") int id) {
        Customer customer = DataStore.customers.get(id); // Retrieve the customer from the DataStore customers map using the provided ID
        if (customer == null) {
            throw new CustomerNotFoundException("Customer with ID " + id + " not found."); //Exception for customer not found
        }
        return customer;
    }

    // Put method to update an existing customer
    // It takes a customer ID as a path parameter and a Customer object as input, and returns a Response object
    @PUT
    @Path("/{id}")
    public Response updateCustomer(@PathParam("id") int id, Customer customer) {

        Customer existingCustomer = DataStore.customers.get(id); // Retrieve the customer from the DataStore customers map using the provided ID
        if (existingCustomer == null) {
            throw new CustomerNotFoundException("Customer with ID " + id + " not found."); //Exception for customer not found
        }
        if (customer.getName() != null) {
            existingCustomer.setName(customer.getName()); // Update the name of the existing customer
        }
        if (customer.getEmail() != null) {
            existingCustomer.setEmail(customer.getEmail()); // Update the email of the existing customer
        }
        if (customer.getPassword() != null) {
            existingCustomer.setPassword(customer.getPassword());   // Update the password of the existing customer
        }
        DataStore.customers.put(id, existingCustomer); // Store the updated customer in the DataStore customers map
        return Response.status(Response.Status.OK).entity(existingCustomer).build(); // Return a response with status 200 (OK) and the updated customer object
        
    }

    // Delete method to remove a customer by ID
    // It takes a customer ID as a path parameter and returns a Response object
    @DELETE
    @Path("/{id}")
    public Response deleteCustomer(@PathParam("id") int id) {
        Customer customer = DataStore.customers.remove(id); // Remove the customer from the DataStore customers map using the provided ID
        if (customer == null) {
            throw new CustomerNotFoundException("Customer with ID " + id + " not found."); //Exception for customer not found
        }
        return Response.status(Response.Status.NO_CONTENT).build(); // Return a response with status 204 (No Content) indicating successful deletion
    }


}
