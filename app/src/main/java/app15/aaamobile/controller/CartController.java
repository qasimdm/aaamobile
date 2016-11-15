package app15.aaamobile.controller;

/**
 * Created by umyhafzaqa on 2016-11-09.
 */
import java.util.ArrayList;
import android.app.Application;

import app15.aaamobile.model.Cart;
import app15.aaamobile.model.Product;

public class CartController extends Application{

    public static ArrayList<Product> myProducts = new ArrayList<Product>(); // TODO: 2016-11-10 CartController communicates to the cart, make static cart object, add/remove/update/delete funtions for the cart
    private Cart myCart = new Cart();   // TODO: 2016-11-10 make myProducts private later after test 

    public Product getProducts(int pPosition) {

        return myProducts.get(pPosition);
    }

    public void setProducts(Product Products) {
        myProducts.add(Products);

    }

    public Cart getCart() {

        return myCart;

    }

    public int getProductsArraylistSize() {

        return myProducts.size();
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