package app15.aaamobile.view;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import org.json.JSONException;
import java.math.BigDecimal;
import app15.aaamobile.R;
import app15.aaamobile.controller.CartController;
import app15.aaamobile.controller.DatabaseController;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaypalFragment extends Fragment {
    private static final String CLIENT_ID = "ATNyXyN9V-miHvSyOBNR6wuDKpPczEAtDoh4u8_fOAOXC9OEv_T1bcpU0IlTLoD7h6QRH12EjWPKbYG8";

    private CartController cartController = new CartController();
    private DatabaseController databaseController;
    private FirebaseAuth auth;

    private static PayPalConfiguration config = new PayPalConfiguration().acceptCreditCards(false)
            // Start with mock environment.  When ready, switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK)
            .clientId(CLIENT_ID);
    public PaypalFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_paypal_payment, container, false);
        databaseController = new DatabaseController();
        auth = FirebaseAuth.getInstance();
        Button btnPaypal = (Button)view.findViewById(R.id.btn_pp_make_payment);
        Intent intent = new Intent(getContext(), PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        getActivity().startService(intent);

        btnPaypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double price = cartController.getTotalPrice();
                String orderTitle = cartController.getOrder(0).getOrderTitle();
                PayPalPayment payment = new PayPalPayment(new BigDecimal(price), "SEK", "Repair of " + orderTitle,
                        PayPalPayment.PAYMENT_INTENT_SALE);

                Intent intent = new Intent(getContext(), PaymentActivity.class);

                // send the same configuration for restart resiliency
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

                intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

                startActivityForResult(intent, 0);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                try {
                    Log.i("paymentExample", confirm.toJSONObject().toString(4));
                    Toast.makeText(getContext(), "Payment: " + confirm.toJSONObject().toString(4), Toast.LENGTH_SHORT).show();
                    databaseController.setDatabaseReference("users");

                    databaseController.writeOrder(auth.getCurrentUser().getUid(), cartController.getOrdersList());

                    // TODO: send 'confirm' to your server for verification.
                    // see https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                    // for more details.

                } catch (JSONException e) {
                    Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                    Toast.makeText(getContext(), "Payment failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i("paymentExample", "The user canceled.");
        }
        else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
        }
    }
    private void sendAuthorizationToServer(PayPalAuthorization authorization) {
    //as name suggests, send auth to server
    }

    @Override
    public void onDestroy(){
        getActivity().stopService(new Intent(getContext(), PayPalService.class));
        super.onDestroy();
    }

}
