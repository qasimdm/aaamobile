package app15.aaamobile.view;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

import app15.aaamobile.model.Order;

/**
 * Created by umyhafzaqa on 2016-12-14.
 */
public interface OnGetDataListener {
    public void onSuccess(ArrayList<Order> orderArrayList);
}
