package app15.aaamobile.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by umyhafzaqa on 2016-11-07.
 * User model, also contains an attribute of isAdmin to check if the user has the Administrator rights
 */
public class User {

    private String uid;
    private String email;
    private String name;
    private String password;
    private boolean isAdmin;
    private ArrayList<Order> orderList = new ArrayList<>();

    public User(){//Empty constructor required for firebase to deserialize
    }

    public User(String uid, String email, String name, String password, boolean isAdmin){
        this.uid = uid;

        this.email = email;
        this.name = name;
        this.isAdmin = isAdmin;
        this.password = password;
    }
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getPassword(){
        return this.password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public boolean isAdmin() {
        return isAdmin;
    }
    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public ArrayList<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(ArrayList<Order> orderList) {
        this.orderList = orderList;
    }
}
