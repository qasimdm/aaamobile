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

import java.math.BigDecimal;

import app15.aaamobile.R;
import app15.aaamobile.controller.CartController;
import app15.aaamobile.controller.DatabaseController;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PaymentActivity extends AppCompatActivity {

    private static final String CLIENT_ID = "ATNyXyN9V-miHvSyOBNR6wuDKpPczEAtDoh4u8_fOAOXC9OEv_T1bcpU0IlTLoD7h6QRH12EjWPKbYG8";
    private final int nothingSelected = 0;
    private final int paypalSelected = 80;
    private final int cardSelected = 67;

    private final String TABLE_USER = "users";
    private AutoCompleteTextView tvCardNumber, tvCardExpMonth, tvCardExpYear, tvCardCVC;
    LinearLayout layoutCardPayment;
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
        Log.i("Payment act", "value: "+paymentType);
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
        else if (paymentType == cardSelected){
            layoutCardPayment = (LinearLayout)findViewById(R.id.ll_card_payment);
            layoutCardPayment.setVisibility(View.VISIBLE);
            tvCardNumber = (AutoCompleteTextView)findViewById(R.id.et_card_number);
            tvCardExpMonth = (AutoCompleteTextView)findViewById(R.id.et_card_exp_month);
            tvCardExpYear = (AutoCompleteTextView)findViewById(R.id.et_card_exp_year);
            tvCardCVC = (AutoCompleteTextView)findViewById(R.id.et_card_cvc);
            
        }


    }

    private void paypalPayment(){
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
        if (resultCode == Activity.RESULT_OK) {
            PaymentConfirmation confirm = data.getParcelableExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {

                //Write order to the databae
                databaseController.setDatabaseReference(TABLE_USER);
                databaseController.writeOrder(auth.getCurrentUser().getUid(), cartController.getOrdersList()); //, listener);
                //Clear cart, so item counter badge on toolbar updates as well
                cartController.clearCart();
                //finish whole application and start a new Main Activity
                finishAffinity();   // TODO: 2016-12-28 close the previous activity and start new MainActivity 
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_LONG).show();

            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i("paymentExample", "The user canceled.");
        } else if (resultCode == com.paypal.android.sdk.payments.PaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
        }
    }

    private void sendAuthorizationToServer(PayPalAuthorization authorization) {
        //as name suggests, send auth to server
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
