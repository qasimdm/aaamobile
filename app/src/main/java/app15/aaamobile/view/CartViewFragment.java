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
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import app15.aaamobile.R;
import app15.aaamobile.controller.CartController;
import app15.aaamobile.controller.ProductAdapter;
import com.stripe.android.*;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.exception.AuthenticationException;


/**
 * A simple {@link Fragment} subclass.
 */
public class CartViewFragment extends Fragment {

    FragmentManager fm;
    ListView lvCartItems;
    CartController cartController;
    TextView tvTotalPrice;
    Button btnShop;
    TabHost tabHost;


    public CartViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart_view, container, false);
        fm = getActivity().getSupportFragmentManager();
        lvCartItems = (ListView)view.findViewById(R.id.lv_cart_items);
        tvTotalPrice = (TextView)view.findViewById(R.id.tvTotalPrice);
        btnShop = (Button)view.findViewById(R.id.btnShop);
        cartController = new CartController();

        ProductAdapter adapterLVCartItems = new ProductAdapter(getContext(), android.R.layout.simple_list_item_1, cartController.myProducts);
        lvCartItems.setAdapter(adapterLVCartItems);
        tvTotalPrice.setText(""+ cartController.getTotalPrice());

        btnShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PaymentDialogFragment payFragment = new PaymentDialogFragment();
                payFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);    //// TODO: 2016-11-15 fix style, title not visible, problem after Api 23 
                payFragment.show(fm, "PF");

                /*Card card = new Card("4242424242424242", 12, 2017, "123");
                if (!card.validateCard()){
                    Toast.makeText(getContext(), "Card Details are not correct", Toast.LENGTH_SHORT).show();
                }
                else{
                    try {
                        Stripe stripe = new Stripe("pk_test_TA7H4JwpV3t8MTOfbRyeeBzd");
                        stripe.createToken(card, new TokenCallback() {
                            @Override
                            public void onError(Exception error) {
                                Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSuccess(Token token) {
                                Toast.makeText(getContext(), "Payment succeeded", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (AuthenticationException e) {
                        e.printStackTrace();
                    }
                }*/
            }
        });

        return view;
    }

}
