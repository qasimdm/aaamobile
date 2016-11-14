package app15.aaamobile.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import app15.aaamobile.R;
import app15.aaamobile.controller.CartController;
import app15.aaamobile.controller.ProductAdapter;
import app15.aaamobile.model.Product;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartViewFragment extends Fragment {

    ListView lvCartItems;
    CartController cartController;

    public CartViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart_view, container, false);
        lvCartItems = (ListView)view.findViewById(R.id.lv_cart_items);
        cartController = new CartController();

        ProductAdapter adapterLVCartItems = new ProductAdapter(getContext(), android.R.layout.simple_list_item_1, cartController.myProducts);
        lvCartItems.setAdapter(adapterLVCartItems);

        return view;
    }

}
