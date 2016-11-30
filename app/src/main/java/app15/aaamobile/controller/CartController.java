package app15.aaamobile.controller;

/**
 * Created by umyhafzaqa on 2016-11-09.
 */
import java.util.ArrayList;
import android.app.Application;

import app15.aaamobile.model.Cart;
import app15.aaamobile.model.Product;
import app15.aaamobile.view.CartViewFragment;

public class CartController extends Application{

    public static ArrayList<Product> myProducts = new ArrayList<Product>();
    private Cart myCart = new Cart();

    public Product getProducts(int pPosition) {

        return myProducts.get(pPosition);
    }

    public void setProducts(Product Products) {
        myProducts.add(Products);

    }

    public Cart getCart() {

        return myCart;

    }

    public int getProductsCount() {

        return myProducts.size();
    }
    public void clearCart(){
        myProducts.clear();
    }
    public double getTotalPrice(){
        double totalPrice = 0;
        if (myProducts.size()>0) {
            for (Product product : myProducts) {
                totalPrice += product.getProductPrice();
            }
        }
        return totalPrice;
    }

}