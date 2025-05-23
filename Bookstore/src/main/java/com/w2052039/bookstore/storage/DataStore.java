/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.w2052039.bookstore.storage;

/**
 *
 * @author Wolf_Wizard
 */

import com.w2052039.bookstore.model.*;
import java.util.HashMap;
import java.util.Map;

public class DataStore {
    
    public static Map<Integer, Author> authors = new HashMap<>();      // Map to store authors	
    public static Map<Integer, Book> books = new HashMap<>();          // Map to store books
    public static Map<Integer, Customer> customers = new HashMap<>();  // Map to store customers	
    public static Map<Integer, Cart> carts = new HashMap<>();          // Map to store carts
    public static Map<Integer, Order> orders = new HashMap<>();        // Map to store orders


    public static int nextAuthorId = 1;   // Next available author ID
    public static int nextBookId = 1;     // Next available book ID
    public static int nextCustomerId = 1; // Next available customer ID
    public static int nextOrderId = 1;    // Next available order ID

    static{

        // Sample data for authors, books, and customers
        Author author1 = new Author(nextAuthorId++, "J.K. Rowling", "British author, best known for the Harry Potter series.");
        Author author2 = new Author(nextAuthorId++, "George R.R. Martin", "American novelist and short story writer, known for 'A Song of Ice and Fire'.");
        Author author3 = new Author(nextAuthorId++, "J.R.R. Tolkien", "English writer, best known for 'The Hobbit' and 'The Lord of the Rings'.");
        authors.put(author1.getId(), author1);
        authors.put(author2.getId(), author2);
        authors.put(author3.getId(), author3);
        
        Book book1 = new Book(nextBookId++, "Harry Potter and the Philosopher's Stone", author1.getId(), author1.getName(), "9780747532699", 1997, 20.99, 100);
        Book book2 = new Book(nextBookId++, "Harry Potter and the Chamber of Secrets", author1.getId(), author1.getName(), "9780747538493", 1998, 22.99, 80);
        Book book3 = new Book(nextBookId++, "A Game of Thrones", author2.getId(), author2.getName(), "9780553103540", 1996, 25.99, 50);
        Book book4 = new Book(nextBookId++, "A Clash of Kings", author2.getId(), author2.getName(), "9780553108033", 1998, 27.99, 40);
        books.put(book1.getId(), book1);
        books.put(book2.getId(), book2);
        books.put(book3.getId(), book3);
        books.put(book4.getId(), book4);

        Customer customer1 = new Customer(nextCustomerId++, "John Doe", "john.doe@example.com", "password123");
        Customer customer2 = new Customer(nextCustomerId++, "Jane Smith", "jane.smith@example.com", "password456");
        Customer customer3 = new Customer(nextCustomerId++, "Lara Argento", "lara.argento@example.com", "password789");
        customers.put(customer1.getId(), customer1);
        customers.put(customer2.getId(), customer2);
        customers.put(customer3.getId(), customer3);

    }

}
