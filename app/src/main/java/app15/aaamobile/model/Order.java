package app15.aaamobile.model;

/**
 * Created by umyhafzaqa on 2016-11-23.
 */
public class Order {
    private String orderId;
    private String orderTitle;
    private String orderDescription;
    private boolean done;

    public Order(){
    }
    public Order(String orderId, String orderDescription, boolean done){
        this.orderId = orderId;
        this.orderDescription = orderDescription;
        this.done = done;
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
    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }


}
