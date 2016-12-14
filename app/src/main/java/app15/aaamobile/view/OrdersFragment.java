package app15.aaamobile.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import app15.aaamobile.R;
import app15.aaamobile.controller.DatabaseController;
import app15.aaamobile.controller.OrderAdapter;
import app15.aaamobile.model.Order;
import app15.aaamobile.model.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrdersFragment extends Fragment {
    DatabaseController dbController;
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
        //fillOrdersList();
        ListView ordersListView = (ListView) view.findViewById(R.id.list_orders);

        //DatabaseReference dbRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://aaamobile-5f640.firebaseio.com/users");

        /*FirebaseListAdapter adapter = new FirebaseListAdapter<User>(getActivity(), User.class, R.layout.item_order,  dbRef) {


            @Override
            protected void populateView(View v, User model, int position) {
                TextView tvTitle = (TextView)v.findViewById(R.id.tv_order_title);
                TextView tvDescription = (TextView) v.findViewById(R.id.tv_order_description);
                final Spinner statusSpinner = (Spinner) v.findViewById(R.id.spinner_order_status);
                Button btnSaveStatus = (Button) v.findViewById(R.id.btn_save_order_status);
                if(model.getOrderList().size() > 0) {
                    tvTitle.setText(model.getOrderList().get(0).getOrderTitle());
                    tvDescription.setText(model.getOrderList().get(0).getOrderDescription());
                    btnSaveStatus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String status = statusSpinner.getSelectedItem().toString();
                            Toast.makeText(getContext(), "Status is: " + status, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    v.setVisibility(View.GONE);
                }
            }

        };*/
        OrderAdapter adapter = new OrderAdapter(getContext(), android.R.layout.simple_list_item_1, dbController.readOrders()); //dbController.getUser().getOrderList()
        ordersListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        return view;
    }
    public void fillOrdersList(){   // TODO: 2016-12-06 get orders from database
        //orderList.clear();
        orderList.addAll(dbController.readOrders());
    }

}
