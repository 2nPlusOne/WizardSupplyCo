package com.estore.api.estoreapi.persistence;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.estore.api.estoreapi.model.Order;
import com.estore.api.estoreapi.model.Product;
import com.estore.api.estoreapi.model.InsufficientStockException;

/**
 * Implements the functionality for JSON file-based persistence for Order
 * 
 * {@literal @}Component Spring annotation instantiates a single instance of this
 * class and injects the instance into other classes as needed
 * 
 * @author Kanisha Agrawal
 */

public class OrdersFileDAO implements OrdersDAO{

    private static final Logger LOG = Logger.getLogger(OrdersFileDAO.class.getName());


    private static final Order InsufficientStockException = null;

    
    private Map<Integer,Order> orders;   // Provides a local cache of the cart objects
                                    // so that we don't need to read from the file
                                   // each time
    private ObjectMapper objectMapper;  // Provides conversion between Cart
                                       // objects and JSON text format written
                                      // to the file
    private String filename;    // Filename to read from and write to

    private static int nextorderNumber;  // The next order number to assign to a new order
    private InventoryDAO inventoryDao;
    private CartsDAO cartsDAO;

    /**
     * Creates an User File Data Access Object
     * 
     * @param filename Filename to read from and write to
     * @param objectMapper Provides JSON Object to/from Java Object serialization and deserialization
     * 
     * @throws IOException when file cannot be accessed or read from
     */
    public OrdersFileDAO(@Value("${orders.file}") String filename, ObjectMapper objectMapper, InventoryDAO inventoryDao, CartsDAO cartsDAO) throws IOException {
        LOG.info("OrderFileDAO created");
        this.filename = filename;
        this.objectMapper = objectMapper;
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.inventoryDao = inventoryDao;
        this.cartsDAO = cartsDAO;
        load(); // load the orders from the file
    }

   /**
     * Generates the next number for a new {@linkplain Order order}
     * 
     * @return The next number
     */
    private synchronized static int nextorderNumber() {
        int orderNumber = nextorderNumber;
        ++nextorderNumber;
        return orderNumber;
    }

    /**
     * Saves the orders to the file
     * 
     * @return true if the orders were written successfully
     * 
     * @throws IOException when file cannot be accessed or written to
     */
    private boolean save() throws IOException {
        LOG.info("Saving orders to file: " + filename);
        Order[] ordersArray = getOrders();

        // Serializes the Java Objects to JSON objects into the file
        // writeValue will thrown an IOException if there is an issue
        // with the file or reading from the file
        objectMapper.writeValue(new File(filename), ordersArray);

        return true;
    }
    
    /**
     * Loads the orders from the file
     * 
     * @return true if the orders were read successfully
     * 
     * @throws IOException when file cannot be accessed or read from
     */
    private boolean load() throws IOException {
        LOG.info("Loading orders from file: " + filename);
        orders = new TreeMap<>();
        nextorderNumber = 0;

        // readValue will thrown an IOException if there is an issue
        // with the file or reading from the file
        Order[] ordersArray = objectMapper.readValue(new File(filename), Order[].class);

        // Load the order into the local cache
        for (Order order : ordersArray) {
            LOG.info("Loaded order for user: " + order.getOrderNumber());
            orders.put(order.getOrderNumber(), order);
            if (order.getOrderNumber() > nextorderNumber)
            nextorderNumber = order.getOrderNumber();
    }
        //increments the next order number
        ++nextorderNumber;
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Order[] getOrders() {
        synchronized (orders) {
            return orders.values().toArray(new Order[0]);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Order getOrder(int orderNumber) {
        synchronized (orders) {
            return orders.get(orderNumber);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Order createOrder(Order order) throws IOException{
        
        if(order.getProductSkus().length == 0){
            return null;
        }

        int userId = order.getUserId();
        for(int sku:  order.getProductSkus()){
            if(!(inventoryDao.getProduct(sku).hasEnoughStockFor(order.getCart().getProductCount(sku)))){
                return InsufficientStockException;
            }
            
        }
        synchronized (orders) {
            Order newOrder = new Order(nextorderNumber(), order.getFirstName(), order.getLastName(),order.getPhoneNumber(), order.getEmailAddress(), order.getShippingAddress(), order.getCart());
            int[] skus = order.getProductSkus();
            for(int sku: skus){
              Product product = inventoryDao.getProduct(sku);
              product.getStock().removeStock(order.getCart().getProductCount(sku));
              inventoryDao.updateProduct(product); 
             
            }
            
        //TODO make sure to update purchased productMap for users to update 

            cartsDAO.clearCart(userId);
            orders.put(newOrder.getOrderNumber(), newOrder);
            try {
                save();
            } catch (IOException e) {
                LOG.warning("Failed to save cart for user " + order.getOrderNumber() + ". " + e.getMessage());
            }

            return order;
        }
    }
    
}
