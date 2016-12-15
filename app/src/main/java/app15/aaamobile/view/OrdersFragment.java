package app15.aaamobile.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import app15.aaamobile.R;
import app15.aaamobile.controller.DatabaseController;
import app15.aaamobile.controller.OrderAdapter;
import app15.aaamobile.model.Order;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrdersFragment extends Fragment {
    DatabaseController dbController;
    OrderAdapter adapter;
    private ArrayList<Order> orderList = new ArrayList<>();

    public OrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        dbController = new DatabaseController();
        dbController.setDatabaseReference("users");
        dbController.staticOrderList.clear();
        ListView ordersListView = (ListView) view.findViewById(R.id.list_orders);

        adapter = new OrderAdapter(getContext(), android.R.layout.simple_list_item_1, dbController.staticOrderList); //dbController.getUser().getOrderList()
        dbController.readOrders(adapter);
        ordersListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        return view;
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.i("in OnStop", "OrdersFragment");
    }

}
