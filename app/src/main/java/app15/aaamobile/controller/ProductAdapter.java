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
import java.util.List;

import app15.aaamobile.R;
import app15.aaamobile.model.Product;

/**
 * Created by umyhafzaqa on 2016-11-10.
 */
public class ProductAdapter extends ArrayAdapter<Product> {

    ImageButton btnDeleteItem;
    CartController cartController;

    public ProductAdapter(Context context, int resource, ArrayList<Product> objects) {
        super(context, resource, objects);
        cartController = new CartController();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        Product product = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_product, parent, false);
        }
        btnDeleteItem = (ImageButton)convertView.findViewById(R.id.btn_delete_item);
        TextView tvProductName = (TextView)convertView.findViewById(R.id.tv_product_name);
        TextView tvProductType = (TextView)convertView.findViewById(R.id.tv_product_type);
        TextView tvProductPrice = (TextView)convertView.findViewById(R.id.tv_product_price);
        tvProductName.setText(product.getProductName());
        tvProductType.setText(product.getProductDescription());
        tvProductPrice.setText(""+product.getProductPrice());

        btnDeleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Deleted.", Toast.LENGTH_SHORT).show();
                cartController.myProducts.remove(position);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }
    @Override
    public int getCount(){
        return cartController.myProducts.size();
    }
}

