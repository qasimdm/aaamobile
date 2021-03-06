package app15.aaamobile.view;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import app15.aaamobile.R;
import app15.aaamobile.controller.CartController;
import app15.aaamobile.controller.DatabaseController;
import app15.aaamobile.controller.ProductAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class CartViewFragment extends Fragment {
    private final String TABLE_USER = "users";

    private FragmentManager fm;
    private ListView lvCartItems;
    private CartController cartController;
    private TextView tvTotalPrice, tvEmptyCart;
    private Button btnClearCart, btnShop;
    private DatabaseController dbController;


    public CartViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart_view, container, false);
        fm = getActivity().getSupportFragmentManager();
        lvCartItems = (ListView)view.findViewById(R.id.lv_cart_items);
        tvTotalPrice = (TextView)view.findViewById(R.id.tvTotalPrice);
        tvEmptyCart = (TextView)view.findViewById(R.id.tv_empty_cart_msg);
        btnClearCart = (Button)view.findViewById(R.id.btn_clear_cart);
        btnShop = (Button)view.findViewById(R.id.btn_shop);
        cartController = new CartController();
        checkIfCartEmpty();

        ProductAdapter adapterLVCartItems = new ProductAdapter(getContext(), android.R.layout.simple_list_item_1, cartController.getOrdersList(), this);
        lvCartItems.setAdapter(adapterLVCartItems);
        tvTotalPrice.setText(""+ cartController.getTotalPrice());
        //Redirects to the payment page if there is atleast 1 item in the cart
        btnShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cartController.getOrdersCount()>0) {
                    String key = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    dbController = new DatabaseController();
                    dbController.setDatabaseReference(TABLE_USER);
                    dbController.readOnce(key);
                    /*PaymentDialogFragment payFragment = new PaymentDialogFragment();
                    payFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);    //// TODO: 2016-11-15 fix style, title not visible, problem after Api 23
                    payFragment.show(fm, "PF");*/
                    Checkout checkoutFrag = new Checkout();
                    checkoutFrag.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
                    checkoutFrag.show(fm, "PF");
                }
                else{
                    Toast.makeText(getContext(), "Your cart is empty.", Toast.LENGTH_SHORT).show();
                }     //Uncomment after paypal integration
            }
        });
        //Removes all items from the cart
        btnClearCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cartController.getOrdersCount() > 0){
                    cartController.clearCart();
                    checkIfCartEmpty();
                }
            }
        });

        return view;
    }
    //Checks if the cart is empty, if so, then sets empty msg visible and calls update total price method
    public void checkIfCartEmpty(){
        if (cartController.getOrdersCount() == 0){
            tvEmptyCart.setVisibility(View.VISIBLE);
            MainActivity.mNotificationCount = cartController.getOrdersCount();
            getActivity().invalidateOptionsMenu();  //update Actionbar
        }
        updateTotalPrice();
    }
    public void updateTotalPrice(){
        tvTotalPrice.setText(""+cartController.getTotalPrice());
    }

}
