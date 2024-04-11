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
    String transportCategory = null, transportExpenseFilePath, accommodationExpenseFilePath, foodExpenseFilePath, otherExpenseFilePath;
    Button createExpenseSheetSendButton;
    ProgressDialog progressDialog;
    RequestQueue requestQueue;
    private static final int PICK_FILE_REQUEST_CODE = 123;
    private String lastClickedButtonId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_create_expense_sheet, container, false);

        createExpenseSheetSendButton = rootView.findViewById(R.id.createExpenseSheetSendButton);

        Bundle bundle = getArguments();
        String userId, kilometerCostsId;

        if (bundle != null) {
            userId = bundle.getString("userId");
            kilometerCostsId = bundle.getString("kilometerCostsId");
            String roleId = bundle.getString("roleId");
            String firstName = bundle.getString("firstName");
            String lastName = bundle.getString("lastName");
            String email = bundle.getString("email");
            String status = bundle.getString("status");
        } else {
            userId = null;
            kilometerCostsId = null;
            Log.d(TAG, "Bundle is null.");
        }

        EditText requestDate = rootView.findViewById(R.id.requestDate);
        EditText startDate = rootView.findViewById(R.id.startDate);
        EditText endDate = rootView.findViewById(R.id.endDate);
        EditText KilometersNumber = rootView.findViewById(R.id.KilometersNumber);
        EditText transportExpense = rootView.findViewById(R.id.transportExpense);
        EditText nightsNumber = rootView.findViewById(R.id.nightsNumber);
        EditText accommodationExpense = rootView.findViewById(R.id.accommodationExpense);
        EditText foodExpense = rootView.findViewById(R.id.foodExpense);
        EditText otherExpense = rootView.findViewById(R.id.otherExpense);
        EditText message = rootView.findViewById(R.id.message);

        Spinner createExpenseSheetSpinner = rootView.findViewById(R.id.createExpenseSheetSpinner);
        String selectedTransport = createExpenseSheetSpinner.getSelectedItem().toString();

        if (selectedTransport.equals("Avion")) {
            transportCategory = "1";
        } else if (selectedTransport.equals("Train")) {
            transportCategory = "2";
        } else if (selectedTransport.equals("Bus/Car/Taxi")) {
            transportCategory = "3";
        } else {
            transportCategory = "4";
        }

        Button transportExpenseFile = rootView.findViewById(R.id.transportExpenseFile);
        Button accommodationExpenseFile = rootView.findViewById(R.id.accommodationExpenseFile);
        Button foodExpenseFile = rootView.findViewById(R.id.foodExpenseFile);
        Button otherExpenseFile = rootView.findViewById(R.id.otherExpenseFile);

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

        progressDialog = new ProgressDialog(getContext());
        requestQueue = Volley.newRequestQueue(getActivity());

        createExpenseSheetSendButton.setOnClickListener(v -> {
            String requestDateVar = requestDate.getText().toString();
            String startDateVar = startDate.getText().toString();
            String endDateVar = endDate.getText().toString();
            String KilometersNumberVar = KilometersNumber.getText().toString();
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
            } else {
                createExpenseSheetRequest(kilometerCostsId, userId, requestDateVar, startDateVar, endDateVar, transportCategory, KilometersNumberVar, transportExpenseVar, transportExpenseFilePath, nightsNumberVar, accommodationExpenseVar, accommodationExpenseFilePath, foodExpenseVar, foodExpenseFilePath, otherExpenseVar, messageVar, otherExpenseFilePath);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            Uri fileUri = data.getData();
            if (fileUri != null) {
                String filePath = fileUri.getPath();
                if (filePath != null) {
                    switch (lastClickedButtonId) {
                        case "transportExpenseFile":
                            transportExpenseFilePath = filePath;
                            break;
                        case "accommodationExpenseFile":
                            accommodationExpenseFilePath = filePath;
                            break;
                        case "foodExpenseFile":
                            foodExpenseFilePath = filePath;
                            break;
                        case "otherExpenseFile":
                            otherExpenseFilePath = filePath;
                            break;
                    }
                    Toast.makeText(getContext(), "Fichier sélectionné : " + filePath, Toast.LENGTH_LONG).show();
                } else {
                    Log.d(TAG, "URL NULL");
                }
            }
        }

    }

    private String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String realPath = cursor.getString(columnIndex);
            cursor.close();
            return realPath;
        }
        return uri.getPath();
    }

    private void createExpenseSheetRequest(String kilometerCostsId, String userId, String requestDateVar, String startDateVar, String endDateVar, String transportCategory, String KilometersNumberVar, String transportExpenseVar, String transportExpenseFilePath, String nightsNumberVar, String accommodationExpenseVar, String accommodationExpenseFilePath, String foodExpenseVar, String foodExpenseFilePath, String otherExpenseVar, String messageVar, String otherExpenseFilePath) {
        String createExpenseSheetURL = "https://jeremiebayon.fr/api/controllers/functionalities/ExpenseSheet/CreateExpenseSheet.php";

        progressDialog.setMessage("En cours de soumission...");
        progressDialog.setCancelable(false);
        progressDialog.show();


        StringRequest stringRequest = new StringRequest(Request.Method.POST, createExpenseSheetURL, response -> {
            progressDialog.dismiss();

            try {
                JSONObject jsonObject = new JSONObject(response);
                int status = jsonObject.getInt("status");

                if (status == 200) {
                    Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Response: " + response);
                } else {
                    Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Response: " + response);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d(TAG, "Response: " + response);
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

                if (transportCategory != null) {
                    params.put("transport_category", transportCategory);
                }
                if (transportExpenseVar != null) {
                    params.put("transport_expense", transportExpenseVar);
                }
                if (KilometersNumberVar != null) {
                    params.put("kilometers_number", KilometersNumberVar);
                }
                if (accommodationExpenseVar != null) {
                    params.put("accommodation_expense", accommodationExpenseVar);
                }
                if (nightsNumberVar != null) {
                    params.put("nights_number", nightsNumberVar);
                }
                if (foodExpenseVar != null) {
                    params.put("food_expense", foodExpenseVar);
                }
                if (otherExpenseVar != null) {
                    params.put("other_expense", otherExpenseVar);
                }
                if (messageVar != null) {
                    params.put("message", messageVar);
                }
                if (transportExpenseFilePath != null) {
                    params.put("transport_expense_file", transportExpenseFilePath);
                }
                if (accommodationExpenseFilePath != null) {
                    params.put("accommodation_expense_file", accommodationExpenseFilePath);
                }
                if (foodExpenseFilePath != null) {
                    params.put("food_expense_file", foodExpenseFilePath);
                }
                if (otherExpenseFilePath != null) {
                    params.put("other_expense_file", otherExpenseFilePath);
                }

                return params;
            }
        };

        requestQueue.add(stringRequest);
    }
}

