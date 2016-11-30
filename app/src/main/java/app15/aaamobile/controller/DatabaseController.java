package app15.aaamobile.controller;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import app15.aaamobile.model.User;

/**
 * Created by umyhafzaqa on 2016-11-30.
 */
public class DatabaseController {
    FirebaseDatabase db;
    DatabaseReference dbRef;
    public static User user = new User();   //for stability, initilizing User()

    public DatabaseController(){
        db = FirebaseDatabase.getInstance();
       // dbRef = db.getReference();
    }
    public void initDatabaseReference(String child){
        dbRef = db.getReference().child(child);
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
    public void writeUserData(String uid, String email, String name, String password, boolean ifAdmin){
        User createUser = new User(uid, email, name, password, ifAdmin);
        Log.i("In Database Controller", "User password: " + password);
        dbRef.child(uid).setValue(createUser);
    }
}
