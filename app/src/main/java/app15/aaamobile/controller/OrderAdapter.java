package app15.aaamobile.controller;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import app15.aaamobile.R;
import app15.aaamobile.model.Order;
import app15.aaamobile.view.MainActivity;

/**
 * Created by umyhafzaqa on 2016-12-06.
 */
public class OrderAdapter extends ArrayAdapter<Order> {

    DatabaseController dbController;
    ArrayList<Order> ordersList;
    public OrderAdapter(Context context, int resource, ArrayList<Order> ordersList) {
        super(context, resource, ordersList);
        dbController = new DatabaseController();
        this.ordersList = ordersList;
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
        final Spinner statusSpinner = (Spinner) convertView.findViewById(R.id.spinner_order_status);
        Button btnSaveStatus = (Button) convertView.findViewById(R.id.btn_save_order_status);
        //setting values
        if (dbController.orderList.size() > 0) {
            Log.i("In orderAdapter", "size="+ordersList.size() + "pos="+position);
            tvTitle.setText(dbController.orderList.get(position).getOrderTitle());
            tvDescription.setText(dbController.orderList.get(position).getOrderDescription());
        }
        notifyDataSetChanged();

        btnSaveStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String status = statusSpinner.getSelectedItem().toString();
                Toast.makeText(getContext(), "Status is: " + status, Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }

    @Override
    public int getCount(){
        return dbController.getUser().getOrderList().size();
    }

}
