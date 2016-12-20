package app15.aaamobile.controller;

import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import app15.aaamobile.model.Order;
import app15.aaamobile.model.User;
import app15.aaamobile.view.OnGetDataListener;

/**
 * Created by umyhafzaqa on 2016-11-30.
 */
public class DatabaseController {   // TODO: 2016-12-15 make it singelton
    private FirebaseDatabase db;
    private DatabaseReference dbRef;
    private static User user = new User();   //for stability, initilizing User()
    private ArrayList<Order> orderListForAdmin = new ArrayList<>();

    public DatabaseController(){
        db = FirebaseDatabase.getInstance();
       // dbRef = db.getReference();
    }
    public void setDatabaseReference(String child){
        dbRef = db.getReference().child(child);
    }
    public void setDatabaseReference(String child1, String child2){
        dbRef = db.getReference().child(child1).child(child2);
    }

    public User getUser(){
        return this.user;
    }

    public void readOnce(String key){
        dbRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void readOnce(String key, final MenuItem menuItem){
        dbRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                Log.i("DbController", "Value of ifAdmin = " + user.isAdmin());
                if (user.isAdmin()){
                    menuItem.setVisible(true);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void readUserOrder(String key, final OnGetDataListener listener){
        dbRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fetchUserOrder(dataSnapshot, listener);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    private void fetchUserOrder(DataSnapshot dataSnapshot, OnGetDataListener listener){
        ArrayList<Order> orderArrayList = new ArrayList<>();
        User user1 = dataSnapshot.getValue(User.class);
        if (user1.getOrderList().size()>0) {
            for (int i = 0; i < user1.getOrderList().size(); i++) { // for(Order order : user1.getOrderList()) {
                user1.getOrderList().get(i).setOrderId(user1.getUid()); //saving user id to re-use it later while updating order status
                orderArrayList.add(user1.getOrderList().get(i));
            }
            listener.onSuccess(orderArrayList);
        }
    }

    // Retrieves all users orders from the database and fill listview in orders fragment (only visible for admin)
    public void readOrders(final OnGetDataListener listener){
        //staticOrderList.clear();
        dbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot, listener);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot, listener);
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void  fetchData(DataSnapshot dataSnapshot, OnGetDataListener listener){

        User user1 = dataSnapshot.getValue(User.class);
        if (user1.getOrderList().size()>0) {
            for (int i=0; i<user1.getOrderList().size(); i++){ // for(Order order : user1.getOrderList()) {
                user1.getOrderList().get(i).setOrderId(user1.getUid()); //saving user id to re-use it later while updating order status
                user1.getOrderList().get(i).setOrderNumber(Integer.toString(i));    //saving the index of order to later update it in the arraylist
                orderListForAdmin.add(user1.getOrderList().get(i));
            }
            if (orderListForAdmin.size()>0) {
                listener.onSuccess(orderListForAdmin);
            }
            //adapter.notifyDataSetChanged();
        }
    }
    //Create new user account
    public void writeUserData(String child, String email, String name, String password, boolean ifAdmin){
        User createUser = new User(child, email, name, password, ifAdmin);
        Log.i("In Database Controller", "User password: " + password);
        dbRef.child(child).setValue(createUser);    //child = key
    }
    //Used by admin to update the status of order
    public void updateOrder(Order order){
        dbRef.child(order.getOrderId()).child("orderList").child(order.getOrderNumber()).child("status").setValue(order.getStatus());
    }
    //to update a specific field value
    public void writeSingleItem(String fieldName, String value){
        dbRef.child(fieldName).setValue(value);
    }
    // Write an order in the database
    public void writeOrder(String key, ArrayList<Order> ordersList){ // OnGetDataListener listener){
        getUser().getOrderList().addAll(ordersList);
        dbRef.child(key).child("orderList").setValue(getUser().getOrderList());

    }
}
