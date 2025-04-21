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

    @POST
    public Response createCustomer(Customer customer) {
        if (customer == null || customer.getName() == null || customer.getEmail() == null || customer.getPassword() == null) {
            throw new InvalidInputException("Name, email, and password are required.");
        }
        int id = DataStore.nextCustomerId++;
        customer.setId(id);
        DataStore.customers.put(id, customer);
        return Response.status(Response.Status.CREATED).entity(customer).build();
    }

    @GET
    public Collection<Customer> getAllCustomers() {
        return DataStore.customers.values();
    }

    @GET
    @Path("/{id}")
    public Customer getCustomerById(@PathParam("id") int id) {
        Customer customer = DataStore.customers.get(id);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer with ID " + id + " not found.");
        }
        return customer;
    }

    @PUT
    @Path("/{id}")
    public Response updateCustomer(@PathParam("id") int id, Customer customer) {

        Customer existingCustomer = DataStore.customers.get(id);
        if (existingCustomer == null) {
            throw new CustomerNotFoundException("Customer with ID " + id + " not found.");
        }
        if (customer.getName() != null) {
            existingCustomer.setName(customer.getName());
        }
        if (customer.getEmail() != null) {
            existingCustomer.setEmail(customer.getEmail());
        }
        if (customer.getPassword() != null) {
            existingCustomer.setPassword(customer.getPassword());
        }
        DataStore.customers.put(id, existingCustomer);
        return Response.status(Response.Status.OK).entity(existingCustomer).build();
        
    }

    @DELETE
    @Path("/{id}")
    public Response deleteCustomer(@PathParam("id") int id) {
        Customer customer = DataStore.customers.remove(id);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer with ID " + id + " not found.");
        }
        return Response.status(Response.Status.NO_CONTENT).build();
    }


}
