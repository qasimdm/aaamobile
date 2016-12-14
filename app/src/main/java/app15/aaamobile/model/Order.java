package app15.aaamobile.model;

/**
 * Created by umyhafzaqa on 2016-11-23.
 * Order model, contains the details of an order.
 * Attribute status is used to set the status on the reparation, e.g. in process, done etc
 */
public class Order {
    private String orderId;
    private String orderTitle;
    private String orderDescription;
    private double price;
    private String status;

    public Order(){
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

}
