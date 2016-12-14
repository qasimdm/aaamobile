package app15.aaamobile.controller;

/**
 * Created by umyhafzaqa on 2016-11-09.
 * A controller for Order and maintains the cart
 */
import java.util.ArrayList;
import android.app.Application;
import app15.aaamobile.model.Order;

public class CartController extends Application{

    private static ArrayList<Order> ordersList = new ArrayList<>();

    public Order getOrder(int pPosition) {

        return ordersList.get(pPosition);
    }

    public void addOrder(Order order) {
        ordersList.add(order);

    }

    public ArrayList getOrdersList(){
        return this.ordersList;
    }

    public int getOrdersCount() {

        return ordersList.size();
    }
    public void clearCart(){
        ordersList.clear();
    }
    public double getTotalPrice(){
        double totalPrice = 0;
        if (ordersList.size()>0) {
            for (Order order : ordersList) {
                totalPrice += order.getPrice();
            }
        }
        return totalPrice;
    }

}