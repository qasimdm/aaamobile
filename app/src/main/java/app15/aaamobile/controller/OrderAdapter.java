package app15.aaamobile.controller;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import app15.aaamobile.R;
import app15.aaamobile.model.Order;
import app15.aaamobile.model.User;

/**
 * Created by umyhafzaqa on 2016-12-06.
 */
public class OrderAdapter extends ArrayAdapter<Order> {

    DatabaseController dbController;
    ArrayList<Order> ordersList;
    ArrayList<String> orderStatus;
    //UI refs
    Spinner statusSpinner;

    public OrderAdapter(Context context, int resource, ArrayList<Order> ordersList) {
        super(context, resource, ordersList);
        dbController = new DatabaseController();
        dbController.setDatabaseReference("users");
        this.ordersList = ordersList;
        orderStatus = new ArrayList<>();
        orderStatus.addAll(Arrays.asList( getContext().getResources().getStringArray(R.array.array_status) ));
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        //Order order = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_order, parent, false);
        }
        //UI references
        TextView tvTitle = (TextView)convertView.findViewById(R.id.tv_order_title);
        TextView tvDescription = (TextView) convertView.findViewById(R.id.tv_order_description);
        statusSpinner = (Spinner) convertView.findViewById(R.id.spinner_order_status);
        final Button btnSaveStatus = (Button) convertView.findViewById(R.id.btn_save_order_status);
        btnSaveStatus.setTag(position);

        //setting values
        if (ordersList.size() > 0) {
            Order order = ordersList.get(position);
            Log.i("In orderAdapter", "size="+ordersList.size() + ", pos="+position);
            tvTitle.setText(order.getOrderTitle());
            tvDescription.setText(order.getOrderDescription());
            statusSpinner.setSelection(orderStatus.indexOf(order.getStatus()));
        }

        btnSaveStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentPosition = (Integer)view.getTag();
                String status = statusSpinner.getSelectedItem().toString();

                Order order = ordersList.get(currentPosition);
                order.setStatus(status);

                dbController.updateOrder(order);
                Toast.makeText(getContext(), "Status is: " + status, Toast.LENGTH_SHORT).show();
            }
        });
        //notifyDataSetChanged();
        return convertView;
    }

    @Override
    public int getCount(){
        return ordersList.size();
    }

}
