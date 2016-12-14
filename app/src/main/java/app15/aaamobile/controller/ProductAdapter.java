package app15.aaamobile.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import app15.aaamobile.R;
import app15.aaamobile.model.Order;
import app15.aaamobile.view.CartViewFragment;
import app15.aaamobile.view.MainActivity;

/**
 * Created by umyhafzaqa on 2016-11-10.
 */
public class ProductAdapter extends ArrayAdapter<Order> {

    ImageButton btnDeleteItem;
    CartController cartController;
    CartViewFragment cartObject;
    TextView tvEmptyCart;

    public ProductAdapter(Context context, int resource, ArrayList<Order> objects, CartViewFragment cartObject) {
        super(context, resource, objects);
        cartController = new CartController();
        this.cartObject = cartObject;
        //this.tvEmptyCart = tvEmptyCart;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        Order order = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_product, parent, false);
        }
        //UI references
        btnDeleteItem = (ImageButton)convertView.findViewById(R.id.btn_delete_item);
        TextView tvProductName = (TextView)convertView.findViewById(R.id.tv_product_name);
        TextView tvProductType = (TextView)convertView.findViewById(R.id.tv_product_type);
        TextView tvProductPrice = (TextView)convertView.findViewById(R.id.tv_product_price);
        tvProductName.setText(order.getOrderTitle());
        tvProductType.setText(order.getOrderDescription());
        tvProductPrice.setText(""+order.getPrice());

        btnDeleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Deleted.", Toast.LENGTH_SHORT).show();
                if (cartController.getOrdersCount() > 0) {
                    cartController.getOrdersList().remove(position);
                    MainActivity.mNotificationCount = cartController.getOrdersCount();
                }
                notifyDataSetChanged();
                ((MainActivity)getContext()).invalidateOptionsMenu();
                cartObject.checkIfCartEmpty();
            }
        });

        return convertView;
    }
    @Override
    public int getCount(){
        return cartController.getOrdersList().size();
    }
}

