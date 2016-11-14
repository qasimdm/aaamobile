package app15.aaamobile.model;

import android.graphics.PorterDuff;

/**
 * Created by umyhafzaqa on 2016-11-09.
 */

public class Product {

    private String productName;
    private String productDescription;
    private int productPrice;

    public Product(){
    }
    public Product(String productName, String productDescription, int productPrice) {
        this.productName = productName;
        this.productDescription = productDescription;
        this.productPrice = productPrice;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

}
