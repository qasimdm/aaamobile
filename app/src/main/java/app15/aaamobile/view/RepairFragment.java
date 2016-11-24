package app15.aaamobile.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import app15.aaamobile.R;
import app15.aaamobile.controller.CartController;
import app15.aaamobile.model.Product;

/**
 * A simple {@link Fragment} subclass.
 */
public class RepairFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private final String SELECT_MODEL = "Select Model";
    private final String SELECT_PROBLEM = "Select Problem";

    Spinner spinnerMake, spinnerModel; //, spinnerProblem;
    MultiSelectionSpinner spinnerProblem;
    Button btnAddToCart;
    ArrayAdapter<String> adapterMake, adapterModel, adapterProblem;
    ArrayList<String> model, problem;
    CartController cartController;

    public RepairFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_repair, container, false);
        spinnerMake = (Spinner)view.findViewById(R.id.spinnerMake);
        spinnerModel = (Spinner)view.findViewById(R.id.spinnerModel);
        spinnerProblem = (MultiSelectionSpinner) view.findViewById(R.id.spinnerProblem);
        btnAddToCart = (Button)view.findViewById(R.id.btnAddToCart);

        cartController = new CartController();
        // Spinner Drop down elements
        String[] companiesList = getResources().getStringArray(R.array.array_companies);
        String[] problemGeneric = getResources().getStringArray(R.array.array_problem);
        model = new ArrayList<>();
        problem = new ArrayList<>();
        model.add(SELECT_MODEL);
        spinnerProblem.setPrompt("Select a problem from list");
        problem.add("Select Problem");

        spinnerProblem.setItems(problem);

        // Adapter for Make, Model and Problem spinners
        adapterMake = new ArrayAdapter<>(getContext(), R.layout.spinner_font, companiesList);
        adapterModel = new ArrayAdapter<String>(getContext(), R.layout.spinner_font, model){
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent)
            {
                View v = null;

                // If this is the initial dummy entry, make it hidden
                if (position == 0) {
                    TextView tv = new TextView(getContext());
                    tv.setHeight(0);
                    //tv.setVisibility(View.GONE);
                    v = tv;
                }
                else {
                    // Pass convertView as null to prevent reuse of special case views
                    v = super.getDropDownView(position, null, parent);
                }

                // Hide scroll bar because it appears sometimes unnecessarily, this does not prevent scrolling
                parent.setVerticalScrollBarEnabled(false);
                return v;
            }
        };
        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String make = spinnerMake.getSelectedItem().toString();
                String model = spinnerModel.getSelectedItem().toString();
                String problem = spinnerProblem.getSelectedItemsAsString();
                Log.i("TAG", "value of problem" + problem);
                if ( (model.equals(SELECT_MODEL)) || ((problem.equals(SELECT_PROBLEM)) || (problem.equals("")) || (problem.equals(null)) )) {
                    Toast.makeText(getContext(), "Order not added to the cart. Select the required fields first.", Toast.LENGTH_SHORT ).show();
                }
                else{
                    Product order = new Product(make + model, problem, 100);
                    cartController.myProducts.add(order);
                    MainActivity.mNotificationCount = cartController.getProductsCount();
                    getActivity().invalidateOptionsMenu();
                    Toast.makeText(getContext(), "Order added successfully to the cart", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //adapterProblem = new ArrayAdapter<>(getContext(), R.layout.spinner_font, problem);

        // Drop down layout style - list view with radio button // TODO: 2016-11-09 add cart funtions, show cart, add/remove button
        adapterMake.setDropDownViewResource(R.layout.spinner_font);
        adapterModel.setDropDownViewResource(R.layout.spinner_font);
        //adapterProblem.setDropDownViewResource(R.layout.spinner_font);

        // attaching data adapter to spinners
        spinnerMake.setAdapter(adapterMake);
        spinnerModel.setAdapter(adapterModel);
        //spinnerProblem.setAdapter(adapterProblem);

        spinnerMake.setOnItemSelectedListener(this);
        spinnerModel.setOnItemSelectedListener(this);
        spinnerProblem.setOnItemSelectedListener(this);
        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        String item;
        String[] problemGeneric = getResources().getStringArray(R.array.array_problem);

        switch (adapterView.getId()){
            case R.id.spinnerMake:
                item = adapterView.getItemAtPosition(position).toString();
                if (item.equals("Select Make")){
                    Toast.makeText(adapterView.getContext(), item + " selected", Toast.LENGTH_SHORT).show();
                    model.clear();
                    problem.clear();
                    model.add(SELECT_MODEL);
                    problem.add("Select Problem");
                    spinnerModel.setEnabled(false);
                    spinnerProblem.setEnabled(false);
                }
                else if (item.equals("Iphone")){ // TODO: 2016-11-08 fix spinners and update accordingly
                    String [] modelIphone = getResources().getStringArray(R.array.array_model_iphone);
                    model.clear();
                    problem.clear();
                    model.addAll(Arrays.asList(modelIphone));
                    problem.addAll(Arrays.asList(problemGeneric));
                    spinnerModel.setEnabled(true);
                    spinnerProblem.setEnabled(true);
                }
                else if(item.equals("Samsung")){
                    String [] modelSamsung = getResources().getStringArray(R.array.array_model_samsung);
                    model.clear();
                    problem.clear();
                    model.addAll(Arrays.asList(modelSamsung));
                    problem.addAll(Arrays.asList(problemGeneric));
                    spinnerModel.setEnabled(true);
                    spinnerProblem.setEnabled(true);
                }
                adapterModel.notifyDataSetChanged();
                //adapterProblem.notifyDataSetChanged();
                spinnerProblem.setItems(problem);
                break;
            case R.id.spinnerModel:
                //item = adapterView.getItemAtPosition(position).toString();
                break;
            case R.id.spinnerProblem:
                //item = adapterView.getItemAtPosition(position).toString();

                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
