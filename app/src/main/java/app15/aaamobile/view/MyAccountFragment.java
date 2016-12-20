package app15.aaamobile.view;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app15.aaamobile.R;
import app15.aaamobile.adapter.UserOrderAdapter;
import app15.aaamobile.controller.DatabaseController;
import app15.aaamobile.model.Order;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyAccountFragment extends Fragment {

    private final String TABLE_USER = "users";
    private FragmentManager fm;
    private FirebaseAuth mAuth;
    private DatabaseController dbController;
    private ExpandableListView userOrderExpListView;
    private TextView tvUsername, tvEmail;

    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    private String username;
    public MyAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);
        //fragment manager init
        fm = getActivity().getSupportFragmentManager();
        //UI refs
        userOrderExpListView = (ExpandableListView) view.findViewById(R.id.list_view_user_order);
        ImageButton btnChangePassword = (ImageButton)view.findViewById(R.id.user_profile_change_password);
        tvUsername = (TextView)view.findViewById(R.id.user_profile_name);
        tvEmail = (TextView)view.findViewById(R.id.user_profile_email);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth != null) {
            getUserInfo();
        }
        //prepareListData();



        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditProfileFragment editProfileFragment = new EditProfileFragment();
                //Setting a listener on EditProfile fragment that when dismiss() is called, it shall update the Display name text field in MyAccountFragment
                editProfileFragment.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        if (dbController != null) {
                            if (dbController.getUser() != null) {
                                username = dbController.getUser().getName();     //After setting username first time, set username also, as it's a tag for dialog fragment
                                tvUsername.setText(username);
                            }
                        }
                    }
                });
                editProfileFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);    //// TODO: 2016-11-15 fix style, title not visible, problem after Api 23
                editProfileFragment.show(fm, username);
            }
        });

        return view;
    }
    //Firebase database controller reference
    private void getUserInfo() {
        dbController = new DatabaseController();
        dbController.setDatabaseReference(TABLE_USER); //to retrieve orders, to show in the order listview
        username = dbController.getUser().getName();
        String userEmail = dbController.getUser().getEmail();
        if ( username != null ) {
            if (!username.equals("")) {
                tvUsername.setText(username);
            }
        }
        if (userEmail != null) {
            tvEmail.setText(userEmail);
        }

        dbController.readUserOrder(mAuth.getCurrentUser().getUid(), new OnGetDataListener() {
            @Override
            public void onSuccess(ArrayList<Order> orderArrayList) {
                if (orderArrayList.size()>0) {
                    prepareListData(orderArrayList);    //Arra
                    UserOrderAdapter adapter = new UserOrderAdapter(getContext(), listDataHeader, listDataChild);
                    userOrderExpListView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                else {
                    //show "No order history" message
                }
            }
        });
    }

    private void prepareListData(ArrayList<Order> orderArrayList) {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<String, List<String>>();
        String title, status, description, price;

        for (int i=0; i<orderArrayList.size(); i++) {
            title = orderArrayList.get(i).getOrderTitle();
            status = orderArrayList.get(i).getStatus();
            description = orderArrayList.get(i).getOrderDescription();
            price = Double.toString(orderArrayList.get(i).getPrice());
            if (status.equals("")){
                status = "No updates yet";
            }
            //Adding header data
            listDataHeader.add(title);  //header data

            List<String> list = new ArrayList<>();

            list.add("Problem: " + description);
            list.add("Cost: " + price);
            list.add("Status: " + status);

            listDataChild.put(listDataHeader.get(i), list); //header, child data
        }
    }

}
