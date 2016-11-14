package app15.aaamobile.model;

/**
 * Created by umyhafzaqa on 2016-11-09.
 */
import java.util.ArrayList;

public class Cart {

    private  ArrayList<Product> cartProducts = new ArrayList<>();


    public Product getProducts(int pPosition) {
        return cartProducts.get(pPosition);
    }

    public void setProducts(Product Products) {
        cartProducts.add(Products);
    }

    public int getCartSize() {
        return cartProducts.size();
    }

    public boolean checkProductInCart(Product aProduct) {
        return cartProducts.contains(aProduct);
    }

}
