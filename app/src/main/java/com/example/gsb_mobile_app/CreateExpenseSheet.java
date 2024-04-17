package com.example.gsb_mobile_app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.net.Uri;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CreateExpenseSheet extends Fragment {
    private static final String TAG = "CreateExpenseSheet";
    private final int PICK_FILE_REQUEST_CODE = 123;
    private String transportCategory, transportExpenseFilePath, accommodationExpenseFilePath, foodExpenseFilePath, otherExpenseFilePath;
    private String lastClickedButtonId;
    private ProgressDialog progressDialog;
    private RequestQueue requestQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_create_expense_sheet, container, false);

        EditText requestDate = view.findViewById(R.id.requestDate);
        EditText startDate = view.findViewById(R.id.startDate);
        EditText endDate = view.findViewById(R.id.endDate);
        EditText kilometersNumber = view.findViewById(R.id.kilometersNumber);
        EditText transportExpense = view.findViewById(R.id.transportExpense);
        EditText nightsNumber = view.findViewById(R.id.nightsNumber);
        EditText accommodationExpense = view.findViewById(R.id.accommodationExpense);
        EditText foodExpense = view.findViewById(R.id.foodExpense);
        EditText otherExpense = view.findViewById(R.id.otherExpense);
        EditText message = view.findViewById(R.id.message);
        Button createExpenseSheetButton = view.findViewById(R.id.createExpenseSheetButton);

        Button transportExpenseFile = view.findViewById(R.id.transportExpenseFile);
        Button accommodationExpenseFile = view.findViewById(R.id.accommodationExpenseFile);
        Button foodExpenseFile = view.findViewById(R.id.foodExpenseFile);
        Button otherExpenseFile = view.findViewById(R.id.otherExpenseFile);

        Spinner createExpenseSheetSpinner = view.findViewById(R.id.createExpenseSheetSpinner);
        createExpenseSheetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedTransport = createExpenseSheetSpinner.getSelectedItem().toString();
                updateFields(selectedTransport, transportExpense, kilometersNumber);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        transportExpenseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastClickedButtonId = "transportExpenseFile";
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
            }
        });

        accommodationExpenseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastClickedButtonId = "accommodationExpenseFile";
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
            }
        });

        foodExpenseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastClickedButtonId = "foodExpenseFile";
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
            }
        });

        otherExpenseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastClickedButtonId = "otherExpenseFile";
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
            }
        });

        Bundle bundle = getArguments();

        String userId = bundle.getString("userId");
        String kilometerCostsId = bundle.getString("kilometerCostsId");

        progressDialog = new ProgressDialog(getContext());
        requestQueue = Volley.newRequestQueue(getActivity());

        createExpenseSheetButton.setOnClickListener(v -> {
            String requestDateVar = requestDate.getText().toString();
            String startDateVar = startDate.getText().toString();
            String endDateVar = endDate.getText().toString();
            String kilometersNumberVar = kilometersNumber.getText().toString();
            String transportExpenseVar = transportExpense.getText().toString();
            String nightsNumberVar = nightsNumber.getText().toString();
            String accommodationExpenseVar = accommodationExpense.getText().toString();
            String foodExpenseVar = foodExpense.getText().toString();
            String otherExpenseVar = otherExpense.getText().toString();
            String messageVar = message.getText().toString();

            if (requestDateVar.isEmpty()) {
                Toast.makeText(getContext(), "Le champ 'Date de création' est vide", Toast.LENGTH_SHORT).show();
            } else if (startDateVar.isEmpty()) {
                Toast.makeText(getContext(), "Le champ 'Date de départ' est vide", Toast.LENGTH_SHORT).show();
            } else if (endDateVar.isEmpty()) {
                Toast.makeText(getContext(), "Le champ 'Date de retour' est vide", Toast.LENGTH_SHORT).show();
            } else if (requestDateVar.isEmpty()) {
                requestDateVar =a;
            }

                createExpenseSheetRequest(kilometerCostsId, userId, requestDateVar, startDateVar, endDateVar, transportCategory, kilometersNumberVar, transportExpenseVar, transportExpenseFilePath, nightsNumberVar, accommodationExpenseVar, accommodationExpenseFilePath, foodExpenseVar, foodExpenseFilePath, otherExpenseVar, messageVar, otherExpenseFilePath);
            }
        });

        return view;
    }

    private void updateFields(String selectedTransport, EditText transportExpense, EditText kilometersNumber) {
        switch (selectedTransport) {
            case "Avion":
            case "Train":
            case "Bus/Car/Taxi":
                transportCategory = "1";
                transportExpense.setEnabled(true);
                kilometersNumber.setEnabled(false);
                break;
            case "Voiture":
                transportCategory = "4";
                transportExpense.setEnabled(false);
                kilometersNumber.setEnabled(true);
                break;
            default:
                transportCategory = "null";
                transportExpense.setEnabled(true);
                kilometersNumber.setEnabled(true);
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            switch (lastClickedButtonId) {
                case "transportExpenseFile":
                    transportExpenseFilePath = uri.getPath();
                    break;
                case "accommodationExpenseFile":
                    accommodationExpenseFilePath = uri.getPath();
                    break;
                case "foodExpenseFile":
                    foodExpenseFilePath = uri.getPath();
                    break;
                case "otherExpenseFile":
                    otherExpenseFilePath = uri.getPath();
                    break;
            }
            Toast.makeText(getContext(), "Chemin du fichier sélectionné : " + uri.getPath(), Toast.LENGTH_SHORT).show();
        }
    }

    private void createExpenseSheetRequest(String kilometerCostsId, String userId, String requestDateVar, String startDateVar, String endDateVar, String transportCategory, String kilometersNumberVar, String transportExpenseVar, String transportExpenseFilePath, String nightsNumberVar, String accommodationExpenseVar, String accommodationExpenseFilePath, String foodExpenseVar, String foodExpenseFilePath, String otherExpenseVar, String messageVar, String otherExpenseFilePath) {
        String createExpenseSheetURL = "https://jeremiebayon.fr/api/controllers/functionalities/ExpenseSheet/CreateExpenseSheet.php";

        progressDialog.setMessage("Envoi en cours...");
        progressDialog.setCancelable(false);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, createExpenseSheetURL, response -> {
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
                params.put("horsepower", kilometerCostsId);
                params.put("id", userId);
                params.put("request_date", requestDateVar);
                params.put("start_date", startDateVar);
                params.put("end_date", endDateVar);
                params.put("transport_category", transportCategory);
                params.put("transport_expense", transportExpenseVar);
                params.put("kilometers_number", kilometersNumberVar);
                params.put("accommodation_expense", accommodationExpenseVar);
                params.put("nights_number", nightsNumberVar);
                params.put("food_expense", foodExpenseVar);
                params.put("other_expense", otherExpenseVar);
                params.put("message", messageVar);
                params.put("transport_expense_file", transportExpenseFilePath);
                params.put("accommodation_expense_file", accommodationExpenseFilePath);
                params.put("food_expense_file", foodExpenseFilePath);
                params.put("other_expense_file", otherExpenseFilePath);

                Log.d(TAG, params.toString());
                return params;
            }
        };

        requestQueue.add(stringRequest);
        progressDialog.show();
    }
}

