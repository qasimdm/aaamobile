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
import app15.aaamobile.view.CartViewFragment;
import app15.aaamobile.view.MainActivity;

/**
 * Created by umyhafzaqa on 2016-11-10.
 */
public class ProductAdapter extends ArrayAdapter<Product> {

    ImageButton btnDeleteItem;
    CartController cartController;
    CartViewFragment cartObject;
    TextView tvEmptyCart;

    public ProductAdapter(Context context, int resource, ArrayList<Product> objects, CartViewFragment cartObject) {
        super(context, resource, objects);
        cartController = new CartController();
        this.cartObject = cartObject;
        //this.tvEmptyCart = tvEmptyCart;
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
                if (cartController.getProductsCount() > 0) {
                    cartController.myProducts.remove(position);
                    MainActivity.mNotificationCount = cartController.getProductsCount();
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
        return cartController.myProducts.size();
    }
}

