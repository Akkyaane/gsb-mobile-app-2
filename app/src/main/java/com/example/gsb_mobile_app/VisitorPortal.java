package com.example.gsb_mobile_app;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class VisitorPortal extends Fragment {
    private static final String TAG = "VisitorPortal";
    ProgressDialog progressDialog;
    RequestQueue requestQueue;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visitor_portal, container, false);
        TextView visitorPortalTitle = view.findViewById(R.id.visitorPortalTitle);
        progressDialog = new ProgressDialog(getActivity());
        requestQueue = Volley.newRequestQueue(getActivity());

        Bundle bundle = getArguments();
        String userId = bundle.getString("userId");
        String firstName = bundle.getString("firstName");
        String lastName = bundle.getString("lastName");
        String formattedData = "Bonjour - " + firstName + " " + lastName;

        visitorPortalTitle.setText(formattedData);

        progressDialog.setMessage("Récupération des données...");
        progressDialog.setCancelable(false);

        gettingExpenseSheetsRequest(userId);

        return view;
    }
    private void gettingExpenseSheetsRequest(String userId) {
        String gettingExpenseSheetsURL = "https://jeremiebayon.fr/api/controllers/portals/visitor.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, gettingExpenseSheetsURL, response -> {
            progressDialog.dismiss();
            LinearLayout expenseSheetsList = getView().findViewById(R.id.expenseSheetsList);
            LayoutInflater inflater = LayoutInflater.from(getActivity());

            try {
                JSONObject jsonObject = new JSONObject(response);
                int status = jsonObject.getInt("status");

                if (status == 200) {
                    JSONArray result = jsonObject.getJSONArray("data");

                    for (int i = 0; i < result.length(); i++) {
                        JSONObject data = result.getJSONObject(i);
                        View expenseSheetItem = inflater.inflate(R.layout.expense_sheet, expenseSheetsList, false);
                        LinearLayout expenseSheet = expenseSheetItem.findViewById(R.id.expenseSheet);
                        TextView RequestDateTextView = expenseSheetItem.findViewById(R.id.requestDateTextView);
                        TextView nightsNumberTextView = expenseSheetItem.findViewById(R.id.nightsNumberTextView);
                        TextView totalAmountTextView = expenseSheetItem.findViewById(R.id.totalAmountTextView);
                        TextView treatmentStatusTextView = expenseSheetItem.findViewById(R.id.treatmentStatusTextView);

                        RequestDateTextView.setText(data.optString("request_date"));
                        nightsNumberTextView.setText(data.optString("nights_number"));
                        totalAmountTextView.setText(data.optString("total_amount"));
                        treatmentStatusTextView.setText(data.optString("treatment_status"));

                        final String expenseSheetId = data.optString("expense_sheet_id");
                        expenseSheet.setOnClickListener(v -> manageExpenseSheetRequest(expenseSheetId));

                        expenseSheetsList.addView(expenseSheetItem);
                    }
                } else {
                    Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Un problème est survenu.", Toast.LENGTH_LONG).show();
            }
        },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Échec de la connexion au serveur.", Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", userId);
                return params;
            }
        };

        requestQueue.add(stringRequest);
        progressDialog.show();
    }
    private void manageExpenseSheetRequest(String expenseSheetId) {
        String gettingExpenseSheetsURL = "https://jeremiebayon.fr/api/controllers/functionalities/ExpenseSheet/ManageExpenseSheet.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, gettingExpenseSheetsURL, response -> {
            progressDialog.dismiss();

            try {
                JSONObject jsonObject = new JSONObject(response);
                int status = jsonObject.getInt("status");

                if (status == 200) {
                    Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    ManageExpenseSheet fragment = ManageExpenseSheet.newInstance(jsonObject.getString("data"));
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frameLayout, fragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                } else {
                    Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Un problème est survenu.", Toast.LENGTH_LONG).show();
            }
        },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Échec de la connexion au serveur.", Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", expenseSheetId);
                return params;
            }
        };

        requestQueue.add(stringRequest);
        progressDialog.show();
    }
}

