package app15.aaamobile.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.exception.AuthenticationException;

import java.math.BigDecimal;

import app15.aaamobile.R;
import app15.aaamobile.controller.CartController;
import app15.aaamobile.controller.DatabaseController;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PaymentActivity extends AppCompatActivity {

    private final int MY_SCAN_REQUEST_CODE = 8;
    private static final String CLIENT_ID = "ATNyXyN9V-miHvSyOBNR6wuDKpPczEAtDoh4u8_fOAOXC9OEv_T1bcpU0IlTLoD7h6QRH12EjWPKbYG8";
    private final int nothingSelected = 0;
    private final int paypalSelected = 80;
    private final int cardSelected = 67;

    private AutoCompleteTextView tvCardNumber, tvCardExpMonth, tvCardExpYear, tvCardCVC;
    private LinearLayout layoutCardPayment;
    Button btnCardPay, btnCardScan;

    private final String TABLE_USER = "users";
    private CartController cartController = new CartController();
    private DatabaseController databaseController;
    private FirebaseAuth auth;

    private static PayPalConfiguration config = new PayPalConfiguration().acceptCreditCards(false)
            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK)
            .clientId(CLIENT_ID);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int paymentType = getIntent().getIntExtra("paymentType", nothingSelected);
        Log.i("Payment act", "value: " + paymentType);
        setContentView(R.layout.activity_payment);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //if Paypal was selected as payment mathod
        if (paymentType == paypalSelected) {
            paypalPayment();
        }
        // if Card payment was selected
        else if (paymentType == cardSelected) {
            initUiReferences();


        }


    }

    private void paypalPayment() {
        databaseController = new DatabaseController();
        auth = FirebaseAuth.getInstance();
        //Button btnPaypal = (Button) findViewById(R.id.btn_pp_make_payment);
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

        double price = cartController.getTotalPrice();
        String orderTitle = cartController.getOrder(0).getOrderTitle();
        PayPalPayment payment = new PayPalPayment(new BigDecimal(price), "SEK", "Repair of " + orderTitle,
                PayPalPayment.PAYMENT_INTENT_SALE);

        Intent pp = new Intent(this, com.paypal.android.sdk.payments.PaymentActivity.class);

        // send the same configuration for restart resiliency
        pp.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        pp.putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_PAYMENT, payment);

        startActivityForResult(pp, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Card scan activity result
        if (requestCode == MY_SCAN_REQUEST_CODE) {
            String resultDisplayStr;
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

                // Never log a raw card number. Avoid displaying it, but if necessary use getFormattedCardNumber()
                resultDisplayStr = "Card Number: " + scanResult.getRedactedCardNumber() + "\n";
                Log.i("CardPayment", resultDisplayStr);

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
            } else {
                resultDisplayStr = "Scan was canceled.";
                Log.i("CardPayment", resultDisplayStr);
            }

        }   //END if MY_SCAN_REQUEST_CODE
        // else if paypal method was selected
        else if (resultCode == Activity.RESULT_OK) {
            PaymentConfirmation confirm = data.getParcelableExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {

                //Write order to the databae
                databaseController.setDatabaseReference(TABLE_USER);
                databaseController.writeOrder(auth.getCurrentUser().getUid(), cartController.getOrdersList()); //, listener);
                //Clear cart, so item counter badge on toolbar updates as well
                cartController.clearCart();
                //finish whole application and start a new Main Activity
                //finishAffinity();   // TODO: 2016-12-28 close the previous activity and start new MainActivity
                Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                //finish();
                Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_LONG).show();

            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i("paymentExample", "The user canceled.");
            finish();
        } else if (resultCode == com.paypal.android.sdk.payments.PaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
        }
    }

    private void sendAuthorizationToServer(PayPalAuthorization authorization) {
        //as name suggests, send auth to server
    }

    public void onScanPress(View v) {
        Intent scanIntent = new Intent(PaymentActivity.this, CardIOActivity.class);

        // customize these values to suit your needs.
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false

        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
        startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
    }

    public void initUiReferences() {
        layoutCardPayment = (LinearLayout) findViewById(R.id.ll_card_payment);
        layoutCardPayment.setVisibility(View.VISIBLE);
        tvCardNumber = (AutoCompleteTextView) findViewById(R.id.et_card_number);
        tvCardExpMonth = (AutoCompleteTextView) findViewById(R.id.et_card_exp_month);
        tvCardExpYear = (AutoCompleteTextView) findViewById(R.id.et_card_exp_year);
        tvCardCVC = (AutoCompleteTextView) findViewById(R.id.et_card_cvc);
        btnCardScan = (Button) findViewById(R.id.btn_io_read_card);
        btnCardPay = (Button) findViewById(R.id.btn_make_card_payment);

        btnCardScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onScanPress(view);
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

                if (!(strCardMonth).equals("") && !(strCardYear).equals("")) {    //checking if user didn't enter month or year
                    cardExpMonth = Integer.parseInt(tvCardExpMonth.getText().toString());
                    cardExpYear = Integer.parseInt(tvCardExpYear.getText().toString());
                }

                Card card = new Card(cardNumber, cardExpMonth, cardExpYear, cardCVC);    //4242 4242 4242 4242, 12, 2017, 123
                if (!card.validateCard()) {  //validates if card number, date and cvc are entered correctly
                    Toast.makeText(PaymentActivity.this, "Card Details are not correct", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        Stripe stripe = new Stripe("pk_test_TA7H4JwpV3t8MTOfbRyeeBzd");
                        stripe.createToken(card, new TokenCallback() {
                            @Override
                            public void onError(Exception error) {
                                Toast.makeText(PaymentActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSuccess(Token token) {
                                Toast.makeText(PaymentActivity.this, "Payment succeeded", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (AuthenticationException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button.
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
