package com.example.gsb_mobile_app;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

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
    ProgressDialog progressDialog;
    RequestQueue requestQueue;
    TextView visitorPortalTitle;
    private static final String TAG = "VisitorPortal";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_visitor_portal, container, false);
        String userId = null;
        Bundle bundle = getArguments();

        if (bundle != null) {
            userId = bundle.getString("userId");
            String kilometerCostsId = bundle.getString("kilometerCostsId");
            String roleId = bundle.getString("roleId");
            String firstName = bundle.getString("firstName");
            String lastName = bundle.getString("lastName");
            String email = bundle.getString("email");
            String status = bundle.getString("status");

            User user = new User(userId, kilometerCostsId, roleId, firstName, lastName, email, status);
            firstName = user.getFirstName();
            String formattedData = "Bienvenue " + firstName + " ! ";

            visitorPortalTitle = rootView.findViewById(R.id.visitorPortalTitle);
            visitorPortalTitle.setText(formattedData);

            progressDialog = new ProgressDialog(getActivity());
            requestQueue = Volley.newRequestQueue(getActivity());

            progressDialog.setMessage("Récupération des données en cours...");
            progressDialog.setCancelable(false);

            gettingExpenseSheetsRequest(userId);
        } else {
            Log.d(TAG, "Bundle is null.");
        }

        return rootView;
    }

    private void gettingExpenseSheetsRequest(String user_id) {
        String gettingExpenseSheetsURL = "https://jeremiebayon.fr/api/controllers/portals/visitor.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, gettingExpenseSheetsURL, response -> {
            progressDialog.dismiss();
            LinearLayout expenseSheetsList = getView().findViewById(R.id.expenseSheetsList);
            LayoutInflater inflater = LayoutInflater.from(getActivity());

            try {
                JSONObject jsonObject = new JSONObject(response);
                int status = jsonObject.getInt("status");

                if (status == 200) {
                    Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    JSONArray result = jsonObject.getJSONArray("data");

                    for (int i = 0; i < result.length(); i++) {
                        JSONObject data = result.getJSONObject(i);
                        View expenseSheet = inflater.inflate(R.layout.expense_sheet, expenseSheetsList, false);
                        TextView textViewRequestDate = expenseSheet.findViewById(R.id.textViewRequestDate);
                        TextView textViewNightsNumber = expenseSheet.findViewById(R.id.textViewNightsNumber);
                        TextView textViewTotalAmount = expenseSheet.findViewById(R.id.textViewTotalAmount);
                        TextView textViewTreatmentStatus = expenseSheet.findViewById(R.id.textViewTreatmentStatus);

                        textViewRequestDate.setText(data.optString("request_date"));
                        textViewNightsNumber.setText(data.optString("nights_number"));
                        textViewTotalAmount.setText(data.optString("total_amount"));
                        textViewTreatmentStatus.setText(data.optString("treatment_status"));

                        expenseSheetsList.addView(expenseSheet);
                    }
                } else {
                    Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "Un problème est survenu. Veuillez recommencer.", Toast.LENGTH_LONG).show();
            }
        },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Échec de la connexion au serveur. Veuillez recommencer.", Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", user_id);
                return params;
            }
        };

        requestQueue.add(stringRequest);
        progressDialog.show();
    }

    /*public void logout(View view) {
        getActivity().finish();
    }*/
}


