package app15.aaamobile.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.stripe.android.*;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.exception.AuthenticationException;

import app15.aaamobile.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CardPayment extends Fragment {

    private AutoCompleteTextView tvCardNumber, tvCardExpMonth, tvCardExpYear, tvCardCVC;
    public CardPayment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_card_payment, container, false);
        tvCardNumber = (AutoCompleteTextView)view.findViewById(R.id.et_card_number);
        tvCardExpMonth = (AutoCompleteTextView)view.findViewById(R.id.et_card_exp_month);
        tvCardExpYear = (AutoCompleteTextView)view.findViewById(R.id.et_card_exp_year);
        tvCardCVC = (AutoCompleteTextView)view.findViewById(R.id.et_card_cvc);

        Button btnCardPay = (Button)view.findViewById(R.id.btn_make_card_payment);
        btnCardPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cardNumber = tvCardNumber.getText().toString();
                String cardCVC = tvCardCVC.getText().toString();
                String strCardMonth = tvCardExpMonth.getText().toString();  //string format of card expiry month
                String strCardYear = tvCardExpYear.getText().toString();    //string format of card expiry year
                int cardExpMonth = 0, cardExpYear = 0;

                if ( !(strCardMonth).equals("") && !(strCardYear).equals("") ) {    //checking if user didn't enter month or year
                    cardExpMonth = Integer.parseInt(tvCardExpMonth.getText().toString());
                    cardExpYear = Integer.parseInt(tvCardExpYear.getText().toString());
                }

                Card card = new Card(cardNumber, cardExpMonth, cardExpYear, cardCVC);    //4242 4242 4242 4242, 12, 2017, 123
                if (!card.validateCard()){  //validates if card number, date and cvc are entered correctly
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
                }
            }
        });
        return view;
    }

}
