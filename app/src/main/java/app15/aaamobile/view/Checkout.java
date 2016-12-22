package app15.aaamobile.view;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import app15.aaamobile.R;

public class Checkout extends DialogFragment {
    private int selection = 0;
    private int nothingSelected = 0;
    private int paypalSelected = 1;
    private int cardSelected = 2;

    FragmentManager fm;
    //UI refs
    private ImageView imgPaypal, imgCard;

    public Checkout() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_checkout, container, false);
        getDialog().setTitle("CHECKOUT");

        imgPaypal = (ImageView)view.findViewById(R.id.btn_select_paypal_method);
        imgCard = (ImageView)view.findViewById(R.id.btn_select_card_method);
        Button btnCheckout = (Button)view.findViewById(R.id.btn_checkout);
        imgPaypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selection == nothingSelected) {
                    imgPaypal.setBackground(getResources().getDrawable(R.color.light_grey));
                    selection = paypalSelected;
                }
                else if(selection == cardSelected){
                    imgCard.setBackground(getResources().getDrawable(R.color.white));
                    imgPaypal.setBackground(getResources().getDrawable(R.color.light_grey));
                    selection = paypalSelected;
                }
                else if(selection == paypalSelected){
                    imgPaypal.setBackground(getResources().getDrawable(R.color.white));
                    selection = nothingSelected;
                }
            }
        });

        imgCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selection == nothingSelected) {
                    imgCard.setBackground(getResources().getDrawable(R.color.light_grey));
                    selection = cardSelected;
                }
                else if(selection == paypalSelected){
                    imgPaypal.setBackground(getResources().getDrawable(R.color.white));
                    imgCard.setBackground(getResources().getDrawable(R.color.light_grey));
                    selection = cardSelected;
                }
                else if(selection == cardSelected){
                    imgCard.setBackground(getResources().getDrawable(R.color.white));
                    selection = nothingSelected;
                }
            }
        });
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PaymentActivity.class);
                intent.putExtra("paymentType", selection);
                startActivity(intent);
            }
        });
        return view;
    }

}
