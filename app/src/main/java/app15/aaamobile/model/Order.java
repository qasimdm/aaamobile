package app15.aaamobile.model;

import java.util.Date;

/**
 * Created by umyhafzaqa on 2016-11-23.
 * Order model, contains the details of an order.
 * Attribute status is used to set the status on the reparation, e.g. in process, done etc
 */
public class Order {
    private String orderId;
    private String orderNumber;
    private String orderTitle;
    private String orderDescription;
    private double price;
    private String status;
    private Date date;

    public Order(){ //Empty constructor required for firebase to deserialize
    }
    public Order(String title, String description, String status, double price){
        //this.orderId = orderId;
        this.orderTitle = title;
        this.orderDescription = description;
        this.status = status;
        this.price = price;
    }
    public String getOrderTitle() {
        return orderTitle;
    }

    public void setOrderTitle(String orderTitle) {
        this.orderTitle = orderTitle;
    }
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
    public String getOrderDescription() {
        return orderDescription;
    }

    public void setOrderDescription(String orderDescription) {
        this.orderDescription = orderDescription;
    }
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
