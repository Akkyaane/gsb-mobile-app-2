package com.example.gsb_mobile_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class ManageAccount extends Fragment {
    private Button updateButton;
    private Button submitButton;
    private EditText firstNameEditText, lastNameEditText, emailEditText;
    private ProgressDialog progressDialog;
    private RequestQueue requestQueue;
    private String state;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_account, container, false);

        updateButton = view.findViewById(R.id.updateButton);
        submitButton = view.findViewById(R.id.submitButton);
        Button logoutButton = view.findViewById(R.id.logoutButton);

        firstNameEditText = view.findViewById(R.id.firstNameEditText);
        lastNameEditText = view.findViewById(R.id.lastNameEditText);
        emailEditText = view.findViewById(R.id.emailEditText);

        Bundle bundle = getArguments();
        assert bundle != null;
        String userId = bundle.getString("userId");
        String firstName = bundle.getString("firstName");
        String lastName = bundle.getString("lastName");
        String email = bundle.getString("email");

        firstNameEditText.setText(firstName);
        lastNameEditText.setText(lastName);
        emailEditText.setText(email);

        progressDialog = new ProgressDialog(getContext());
        requestQueue = Volley.newRequestQueue(requireActivity());

        state = "deactivated";
        updateFields(state, firstNameEditText, lastNameEditText, emailEditText);

        updateButton.setVisibility(View.VISIBLE);
        submitButton.setVisibility(View.GONE);

        updateButton.setOnClickListener(v ->  {
            state = "activated";
            updateFields(state, firstNameEditText, lastNameEditText, emailEditText);
            updateButton.setVisibility(View.GONE);
            submitButton.setVisibility(View.VISIBLE);
        });

        submitButton.setOnClickListener(v -> {
            String firstNameVar = firstNameEditText.getText().toString();
            String lastNameVar = lastNameEditText.getText().toString();
            String emailVar = emailEditText.getText().toString();

            if (firstNameVar.isEmpty()) {
                Toast.makeText(getContext(), "Le champ Prénom est vide", Toast.LENGTH_LONG).show();
            } else if (lastNameVar.isEmpty()) {
                Toast.makeText(getContext(), "Le champ Nom est vide", Toast.LENGTH_LONG).show();
            } else if (emailVar.isEmpty()) {
                Toast.makeText(getContext(), "Le champ E-mail est vide", Toast.LENGTH_LONG).show();
            } else {
                manageAccountRequest(userId, firstNameVar, lastNameVar, emailVar);
            }
        });

        logoutButton.setOnClickListener(v -> {
            getActivity().finish();
        });

        return view;
    }
    private void updateFields(String state, EditText firstNameEditText, EditText lastNameEditText, EditText emailEditText) {
        switch (state) {
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
        String manageAccountURL = "https://jeremiebayon.fr/gsb-mobile-api/controllers/functionalities/account/UpdateAccount.php";

        progressDialog.setMessage("Envoi...");
        progressDialog.setCancelable(false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, manageAccountURL,
                response -> {
                    progressDialog.dismiss();

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int status = jsonObject.getInt("status");

                        Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        if (status == 200) {
                            state = "deactivated";
                            updateFields(state, firstNameEditText, lastNameEditText, emailEditText);
                            updateButton.setVisibility(View.VISIBLE);
                            submitButton.setVisibility(View.GONE);
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
