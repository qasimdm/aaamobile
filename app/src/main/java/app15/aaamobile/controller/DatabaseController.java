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
    public static ArrayList<Order> staticOrderList = new ArrayList<>();

    public DatabaseController(){
        db = FirebaseDatabase.getInstance();
       // dbRef = db.getReference();
    }
    public void setDatabaseReference(){
        dbRef = db.getReference();
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
    public void readData(String key){
        Log.i("in DatabaseController", dbRef.toString());
        dbRef.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //This method is called once with initial value and when data at this location is updated
                user = dataSnapshot.getValue(User.class);
                Log.i("in onDataChange", dbRef.toString());
                if (user != null){
                    Log.i("in DatabaseController", "email fo the user is: " + user.getEmail());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Failed to read the value
            }
        });
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
                    Log.i("DbController, ifisAdmin", "Value of ifAdmin= " + user.isAdmin());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    // Retrieves all orders from the database and fill listview in orders fragment, adapter is used to notifyDataSetChanged
    public ArrayList<Order> readOrders(final OrderAdapter adapter){
        //staticOrderList.clear();
        dbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot, adapter);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                fetchData(dataSnapshot, adapter);
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
        Log.i("db before retrun", "size = "+ staticOrderList.size());

        return staticOrderList;
    }
    private void  fetchData(DataSnapshot dataSnapshot, OrderAdapter adapter){
        User user1 = dataSnapshot.getValue(User.class);
        if (user1.getOrderList().size()>0) {
            for (int i=0; i<user1.getOrderList().size(); i++){ // for(Order order : user1.getOrderList()) {
                user1.getOrderList().get(i).setOrderId(user1.getUid()); //saving user id to re-use it later while updating order status
                user1.getOrderList().get(i).setOrderNumber(Integer.toString(i));    //saving the index of order to later update it in the arraylist
                staticOrderList.add(user1.getOrderList().get(i));
            }
            adapter.notifyDataSetChanged();
            //Log.i("DB fetchData", order.getOrderId()+ "," + order.getOrderTitle() + ", array size " + staticOrderList.size());
        }
    }

    public void writeUserData(String child, String email, String name, String password, boolean ifAdmin){
        User createUser = new User(child, email, name, password, ifAdmin);
        Log.i("In Database Controller", "User password: " + password);
        dbRef.child(child).setValue(createUser);    //child = key
    }
    public void updateOrder(Order order){
        dbRef.child(order.getOrderId()).child("orderList").child(order.getOrderNumber()).child("status").setValue(order.getStatus());
    }
    public void writeSingleItem(String fieldName, String value){
        dbRef.child(fieldName).setValue(value);
    }
    // Write an order in the database
    public void writeOrder(String key, ArrayList<Order> ordersList){ // OnGetDataListener listener){
        //String orderKey = dbRef.child(key).push().getKey();
        //Map<String, Order> orderMap = new HashMap<>();
        //orderMap.put(orderKey, order);
        getUser().getOrderList().addAll(ordersList);
        dbRef.child(key).child("orderList").setValue(user.getOrderList());

    }
}
