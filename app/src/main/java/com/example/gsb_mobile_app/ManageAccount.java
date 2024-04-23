package com.example.gsb_mobile_app;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.VolleyError;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class ManageAccount extends Fragment {
    private static final String TAG = "ManageAccount";
    private String status;
    private ProgressDialog progressDialog;
    private RequestQueue requestQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_account, container, false);
        EditText firstNameEditText = view.findViewById(R.id.firstNameEditText);
        EditText lastNameEditText = view.findViewById(R.id.lastNameEditText);
        EditText emailEditText = view.findViewById(R.id.emailEditText);
        Button manageAccountButton1 = view.findViewById(R.id.manageAccountButton1);
        Button manageAccountButton2 = view.findViewById(R.id.manageAccountButton2);

        Bundle bundle = getArguments();
        String userId = bundle.getString("userId");
        String firstName = bundle.getString("firstName");
        String lastName = bundle.getString("lastName");
        String email = bundle.getString("email");

        firstNameEditText.setText(firstName);
        lastNameEditText.setText(lastName);
        emailEditText.setText(email);

        progressDialog = new ProgressDialog(getContext());
        requestQueue = Volley.newRequestQueue(getActivity());

        status = "deactivated";
        updateFields(status, firstNameEditText, lastNameEditText, emailEditText);

        manageAccountButton1.setVisibility(View.VISIBLE);
        manageAccountButton2.setVisibility(View.GONE);

        manageAccountButton1.setOnClickListener(v ->  {
            status = "activated";
            updateFields(status, firstNameEditText, lastNameEditText, emailEditText);
            manageAccountButton1.setVisibility(View.GONE);
            manageAccountButton2.setVisibility(View.VISIBLE);
        });

        manageAccountButton2.setOnClickListener(v -> {
            String firstNameVar = firstNameEditText.getText().toString();
            String lastNameVar = lastNameEditText.getText().toString();
            String emailVar = emailEditText.getText().toString();

            if (firstNameVar.isEmpty()) {
                Toast.makeText(getContext(), "Le champ Prénom est vide", Toast.LENGTH_SHORT).show();
            } else if (lastNameVar.isEmpty()) {
                Toast.makeText(getContext(), "Le champ Nom est vide", Toast.LENGTH_SHORT).show();
            } else if (emailVar.isEmpty()) {
                Toast.makeText(getContext(), "Le champ E-mail est vide", Toast.LENGTH_SHORT).show();
            } else {
                manageAccountRequest(userId, firstNameVar, lastNameVar, emailVar);
            }
        });

        return view;
    }
    private void updateFields(String status, EditText firstNameEditText, EditText lastNameEditText, EditText emailEditText) {
        switch (status) {
            case "activated":
                firstNameEditText.setEnabled(true);
                lastNameEditText.setEnabled(true);
                emailEditText.setEnabled(true);
                break;
            case "deactivated":
                firstNameEditText.setEnabled(false);
                lastNameEditText.setEnabled(false);
                emailEditText.setEnabled(false);
                break;
        }
    }
    private void manageAccountRequest(String userId, String firstNameVar, String lastNameVar, String emailVar) {
        String manageAccountURL = "https://jeremiebayon.fr/api/controllers/functionalities/account/UpdateAccount.php";

        progressDialog.setMessage("Envoi...");
        progressDialog.setCancelable(false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, manageAccountURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int status = jsonObject.getInt("status");

                            if (status == 200) {
                                Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Un problème est survenu.", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Échec de la connexion au serveur.", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("updateid", userId);
                params.put("first_name", firstNameVar);
                params.put("last_name", lastNameVar);
                params.put("email", emailVar);

                return params;
            }
        };

        requestQueue.add(stringRequest);
        progressDialog.show();
    }
}
