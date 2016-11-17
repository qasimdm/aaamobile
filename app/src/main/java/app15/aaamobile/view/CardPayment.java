package app15.aaamobile.view;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.stripe.android.*;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.exception.AuthenticationException;

import app15.aaamobile.R;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

/**
 * A simple {@link Fragment} subclass.
 */
public class CardPayment extends Fragment {
    private final int MY_SCAN_REQUEST_CODE = 0;

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

        Button btnCardScan = (Button)view.findViewById(R.id.btn_io_read_card);
        Button btnCardPay = (Button)view.findViewById(R.id.btn_make_card_payment);
        btnCardScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onScanPress(getView());
            }
        });
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
    public void onScanPress(View v) {
        Intent scanIntent = new Intent(getContext(), CardIOActivity.class);

        // customize these values to suit your needs.
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false

        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
        startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_SCAN_REQUEST_CODE) {
            String resultDisplayStr;
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                // Never log a raw card number. Avoid displaying it, but if necessary use getFormattedCardNumber()
                resultDisplayStr = "Card Number: " + scanResult.getRedactedCardNumber() + "\n";
                Log.i("CardPayment", resultDisplayStr);

                // Do something with the raw number, e.g.:
                // myService.setCardNumber( scanResult.cardNumber );

                if (scanResult.isExpiryValid()) {
                    resultDisplayStr += "Expiration Date: " + scanResult.expiryMonth + "/" + scanResult.expiryYear + "\n";
                    Log.i("CardPayment", resultDisplayStr);
                }

                if (scanResult.cvv != null) {
                    // Never log or display a CVV
                    resultDisplayStr += "CVV has " + scanResult.cvv.length() + " digits.\n";
                    Log.i("CardPayment", resultDisplayStr);
                }

                if (scanResult.postalCode != null) {
                    resultDisplayStr += "Postal Code: " + scanResult.postalCode + "\n";
                    Log.i("CardPayment", resultDisplayStr);
                }
            }
            else {
                resultDisplayStr = "Scan was canceled.";
                Log.i("CardPayment", resultDisplayStr);
            }
            // do something with resultDisplayStr, maybe display it in a textView
            // resultTextView.setText(resultDisplayStr);
        }
        // else handle other activity results
    }

}
