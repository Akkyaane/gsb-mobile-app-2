package com.example.gsb_mobile_app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateExpenseSheet extends Fragment {
    private Bitmap transportExpenseFileBitmap, accommodationExpenseFileBitmap, foodExpenseFileBitmap, otherExpenseFileBitmap;
    private String requestDateVarFormatted, startDateVarFormatted, endDateVarFormatted;
    private ProgressDialog progressDialog;
    private RequestQueue requestQueue;
    private String transportCategory, lastClickedButtonId, transportExpenseFileBase64, accommodationExpenseFileBase64, foodExpenseFileBase64, otherExpenseFileBase64;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_expense_sheet, container, false);

        Button createExpenseSheetButton = view.findViewById(R.id.submitButton);

        EditText requestDateEditText = view.findViewById(R.id.requestDateEditText);
        EditText startDateEditText = view.findViewById(R.id.startDateEditText);
        EditText endDateEditText = view.findViewById(R.id.endDateEditText);
        EditText kilometersNumberEditText = view.findViewById(R.id.kilometersNumberEditText);
        EditText transportExpenseEditText = view.findViewById(R.id.transportExpenseEditText);
        EditText nightsNumberEditText = view.findViewById(R.id.nightsNumberEditText);
        EditText accommodationExpenseEditText = view.findViewById(R.id.accommodationExpenseEditText);
        EditText foodExpenseEditText = view.findViewById(R.id.foodExpenseEditText);
        EditText otherExpenseEditText = view.findViewById(R.id.otherExpenseEditText);
        EditText messageEditText = view.findViewById(R.id.messageEditText);

        ImageView transportExpenseFileImageView = view.findViewById(R.id.transportExpenseFileImageView);
        ImageView accommodationExpenseFileImageView = view.findViewById(R.id.accommodationExpenseFileImageView);
        ImageView foodExpenseFileImageView = view.findViewById(R.id.foodExpenseFileImageView);
        ImageView otherExpenseFileImageView = view.findViewById(R.id.otherExpenseFileImageView);

        Spinner createExpenseSheetSpinner = view.findViewById(R.id.createExpenseSheetSpinner);

        progressDialog = new ProgressDialog(getContext());
        requestQueue = Volley.newRequestQueue(requireActivity());
        createExpenseSheetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = createExpenseSheetSpinner.getSelectedItem().toString();
                updateFields(selectedItem, transportExpenseEditText, kilometersNumberEditText);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ActivityResultLauncher<Intent> activityResultLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        Uri uri = data.getData();
                        try {
                            switch (lastClickedButtonId) {
                                case "transportExpenseFile":
                                    transportExpenseFileBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
                                    transportExpenseFileImageView.setImageBitmap(transportExpenseFileBitmap);
                                    break;
                                case "accommodationExpenseFile":
                                    accommodationExpenseFileBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
                                    accommodationExpenseFileImageView.setImageBitmap(accommodationExpenseFileBitmap);
                                    break;
                                case "foodExpenseFile":
                                    foodExpenseFileBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
                                    foodExpenseFileImageView.setImageBitmap(foodExpenseFileBitmap);
                                    break;
                                case "otherExpenseFile":
                                    otherExpenseFileBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
                                    otherExpenseFileImageView.setImageBitmap(otherExpenseFileBitmap);
                                    break;
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
        transportExpenseFileImageView.setOnClickListener(v -> {
            lastClickedButtonId = "transportExpenseFile";
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intent);
        });
        accommodationExpenseFileImageView.setOnClickListener(v -> {
            lastClickedButtonId = "accommodationExpenseFile";
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intent);
        });
        foodExpenseFileImageView.setOnClickListener(v -> {
            lastClickedButtonId = "foodExpenseFile";
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intent);
        });
        otherExpenseFileImageView.setOnClickListener(v -> {
            lastClickedButtonId = "otherExpenseFile";
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intent);
        });

        Bundle bundle = getArguments();
        assert bundle != null;
        String userId = bundle.getString("userId");
        String kilometerCostsId = bundle.getString("kilometerCostsId");

        createExpenseSheetButton.setOnClickListener(v -> {
            String requestDateVar = requestDateEditText.getText().toString();
            String startDateVar = startDateEditText.getText().toString();
            String endDateVar = endDateEditText.getText().toString();
            String kilometersNumberVar = kilometersNumberEditText.getText().toString();
            String transportExpenseVar = transportExpenseEditText.getText().toString();
            String nightsNumberVar = nightsNumberEditText.getText().toString();
            String accommodationExpenseVar = accommodationExpenseEditText.getText().toString();
            String foodExpenseVar = foodExpenseEditText.getText().toString();
            String otherExpenseVar = otherExpenseEditText.getText().toString();
            String messageVar = messageEditText.getText().toString();

            if (requestDateVar.isEmpty()) {
                Toast.makeText(getContext(), "Le champ Date de création est vide", Toast.LENGTH_SHORT).show();
            } else if (startDateVar.isEmpty()) {
                Toast.makeText(getContext(), "Le champ Date de départ est vide", Toast.LENGTH_SHORT).show();
            } else if (endDateVar.isEmpty()) {
                Toast.makeText(getContext(), "Le champ Date de retour est vide", Toast.LENGTH_SHORT).show();
            } else {
                SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);

                try {
                    Date requestDateVarParsed = inputDateFormat.parse(requestDateVar);
                    assert requestDateVarParsed != null;
                    requestDateVarFormatted = outputDateFormat.format(requestDateVarParsed);
                    Date startDateVarParsed = inputDateFormat.parse(startDateVar);
                    assert startDateVarParsed != null;
                    startDateVarFormatted = outputDateFormat.format(startDateVarParsed);
                    Date endDateVarParsed = inputDateFormat.parse(endDateVar);
                    assert endDateVarParsed != null;
                    endDateVarFormatted = outputDateFormat.format(endDateVarParsed);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                ByteArrayOutputStream byteArrayOutputStream;
                byteArrayOutputStream = new ByteArrayOutputStream();

                if (transportExpenseFileBitmap != null) {
                    transportExpenseFileBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    byte[] bytes = byteArrayOutputStream.toByteArray();
                    transportExpenseFileBase64 = Base64.encodeToString(bytes, Base64.DEFAULT);
                }

                if (accommodationExpenseFileBitmap != null) {
                    accommodationExpenseFileBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    byte[] bytes = byteArrayOutputStream.toByteArray();
                    accommodationExpenseFileBase64 = Base64.encodeToString(bytes, Base64.DEFAULT);
                }

                if (foodExpenseFileBitmap != null) {
                    foodExpenseFileBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    byte[] bytes = byteArrayOutputStream.toByteArray();
                    foodExpenseFileBase64 = Base64.encodeToString(bytes, Base64.DEFAULT);
                }

                if (otherExpenseFileBitmap != null) {
                    otherExpenseFileBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    byte[] bytes = byteArrayOutputStream.toByteArray();
                    otherExpenseFileBase64 = Base64.encodeToString(bytes, Base64.DEFAULT);
                }

                createExpenseSheetRequest(kilometerCostsId, userId, requestDateVarFormatted, startDateVarFormatted, endDateVarFormatted, transportCategory,
                        kilometersNumberVar, transportExpenseVar, transportExpenseFileBase64, nightsNumberVar, accommodationExpenseVar, accommodationExpenseFileBase64, foodExpenseVar, foodExpenseFileBase64,
                        otherExpenseVar, messageVar, otherExpenseFileBase64);
            }
        });

        return view;
    }
    private void updateFields(String selectedItem, EditText transportExpenseEditText, EditText kilometersNumberEditText) {
        switch (selectedItem) {
            case "Avion":
                transportCategory = "1";
                transportExpenseEditText.setEnabled(true);
                kilometersNumberEditText.setEnabled(false);
                break;
            case "Train":
                transportCategory = "2";
                transportExpenseEditText.setEnabled(true);
                kilometersNumberEditText.setEnabled(false);
                break;
            case "Bus/Car/Taxi":
                transportCategory = "3";
                transportExpenseEditText.setEnabled(true);
                kilometersNumberEditText.setEnabled(false);
                break;
            case "Voiture":
                transportCategory = "4";
                transportExpenseEditText.setEnabled(false);
                kilometersNumberEditText.setEnabled(true);
                break;
        }
    }
    private void createExpenseSheetRequest(String kilometerCostsId, String userId, String requestDateVarFormatted, String startDateVarFormatted,
                                           String endDateVarFormatted, String transportCategory, String kilometersNumberVar, String transportExpenseVar, String transportExpenseFileBase64,
                                           String nightsNumberVar, String accommodationExpenseVar, String accommodationExpenseFileBase64, String foodExpenseVar, String foodExpenseFileBase64,  String otherExpenseVar,
                                           String messageVar, String otherExpenseFileBase64) {
        String createExpenseSheetURL = "https://jeremiebayon.fr/api/controllers/functionalities/ExpenseSheet/CreateExpenseSheet.php";

        progressDialog.setMessage("Envoi...");
        progressDialog.setCancelable(false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, createExpenseSheetURL,
                response -> {
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
                },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "Échec de la connexion au serveur.", Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("horsepower", kilometerCostsId);
                params.put("id", userId);
                params.put("request_date", requestDateVarFormatted);
                params.put("start_date", startDateVarFormatted);
                params.put("end_date", endDateVarFormatted);
                params.put("transport_category", transportCategory);
                params.put("transport_expense", transportExpenseVar);
                params.put("kilometers_number", kilometersNumberVar);
                params.put("accommodation_expense", accommodationExpenseVar);
                params.put("nights_number", nightsNumberVar);
                params.put("food_expense", foodExpenseVar);
                params.put("other_expense", otherExpenseVar);
                params.put("message", messageVar);

                if (transportExpenseFileBase64 != null) {
                    params.put("transport_expense_file", transportExpenseFileBase64);
                } else {
                    params.put("transport_expense_file", "");
                }
                if (accommodationExpenseFileBase64 != null) {
                    params.put("accommodation_expense_file", accommodationExpenseFileBase64);
                } else {
                    params.put("accommodation_expense_file", "");
                }
                if (foodExpenseFileBase64 != null) {
                    params.put("food_expense_file", foodExpenseFileBase64);
                } else {
                    params.put("food_expense_file", "");
                }
                if (otherExpenseFileBase64 != null) {
                    params.put("other_expense_file", otherExpenseFileBase64);
                } else {
                    params.put("other_expense_file", "");
                }

                return params;
            }
        };

        requestQueue.add(stringRequest);
        progressDialog.show();
    }
}
