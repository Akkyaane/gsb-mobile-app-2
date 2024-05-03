package com.example.gsb_mobile_app;

import android.annotation.SuppressLint;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ManageExpenseSheet extends Fragment {
    private Bitmap transportExpenseFileBitmap, accommodationExpenseFileBitmap, foodExpenseFileBitmap, otherExpenseFileBitmap;
    private Button updateButton, submitButton, deleteButton;
    private EditText requestDateEditText, startDateEditText, endDateEditText, kilometersNumberEditText, transportExpenseEditText1, transportExpenseEditText2, transportExpenseEditText3, nightsNumberEditText, accommodationExpenseEditText1, accommodationExpenseEditText2, accommodationExpenseEditText3, foodExpenseEditText1, foodExpenseEditText2, foodExpenseEditText3, otherExpenseEditText1, otherExpenseEditText2, otherExpenseEditText3, messageEditText, treatmentStatusEditText, remarkEditText;
    private ProgressDialog progressDialog;
    private RequestQueue requestQueue;
    private String requestDateVarFormatted, startDateVarFormatted, endDateVarFormatted;
    private String mode, id, userId, kilometerCostsId, expenseSheetId, receiptsId, requestDate, startDate, endDate, transportCategory, kilometersNumber, kilometerExpense, kilometerExpenseRefund, kilometerExpenseUnrefund, transportExpense, transportExpenseRefund, transportExpenseUnrefund, transportExpenseFile, transportExpenseFile64Base, nightsNumber, accommodationExpense, accommodationExpenseRefund, accommodationExpenseUnrefund, accommodationExpenseFile, accommodationExpenseFile64Base, foodExpense, foodExpenseRefund,  foodExpenseUnrefund, foodExpenseFile, foodExpenseFile64Base, otherExpense, otherExpenseRefund, otherExpenseUnrefund, message, otherExpenseFile, otherExpenseFile64Base, treatmentStatus, remark;
    private Spinner createExpenseSheetSpinner;
    private static final String EXTRA_MESSAGE = "com.example.gsb_mobile_app.extra.MESSAGE";
    private static final String KILOMETER_COSTS_ID = "com.example.gsb_mobile_app.extra.KILOMETER_COSTS_ID";

    private static final String TAG = "ManageExpenseSheet";
    private TextView manageExpenseSheetTitle1TextView, requestDateTextView, startDateTextView, endDateTextView, transportCategoryTextView, kilometersNumberTextView, transportExpenseTextView1, transportExpenseTextView2, transportExpenseTextView3, transportExpenseFileTextView, nightsNumberTextView, accommodationExpenseTextView1, accommodationExpenseTextView2, accommodationExpenseTextView3, accommodationExpenseFileTextView, foodExpenseTextView1, foodExpenseTextView2, foodExpenseTextView3, foodExpenseFileTextView, otherExpenseTextView1, otherExpenseTextView2, otherExpenseTextView3, otherExpenseFileTextView, messageTextView, manageExpenseSheetTitle2TextView, treatmentStatusTextView, remarkTextView;

    public static ManageExpenseSheet newInstance(String message, String kilometerCostsId) {
        ManageExpenseSheet fragment = new ManageExpenseSheet();
        Bundle bundle = new Bundle();

        bundle.putString(EXTRA_MESSAGE, message);
        bundle.putString(KILOMETER_COSTS_ID, kilometerCostsId);
        fragment.setArguments(bundle);

        return fragment;
    }
    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_expense_sheet, container, false);

        updateButton = view.findViewById(R.id.updateButton);
        deleteButton = view.findViewById(R.id.deleteButton);
        submitButton = view.findViewById(R.id.submitButton);

        requestDateEditText = view.findViewById(R.id.requestDateEditText);
        startDateEditText = view.findViewById(R.id.startDateEditText);
        endDateEditText = view.findViewById(R.id.endDateEditText);
        kilometersNumberEditText = view.findViewById(R.id.kilometersNumberEditText);
        transportExpenseEditText1 = view.findViewById(R.id.transportExpenseEditText1);
        transportExpenseEditText2 = view.findViewById(R.id.transportExpenseEditText2);
        transportExpenseEditText3 = view.findViewById(R.id.transportExpenseEditText3);
        nightsNumberEditText = view.findViewById(R.id.nightsNumberEditText);
        accommodationExpenseEditText1 = view.findViewById(R.id.accommodationExpenseEditText1);
        accommodationExpenseEditText2 = view.findViewById(R.id.accommodationExpenseEditText2);
        accommodationExpenseEditText3 = view.findViewById(R.id.accommodationExpenseEditText3);
        foodExpenseEditText1 = view.findViewById(R.id.foodExpenseEditText1);
        foodExpenseEditText2 = view.findViewById(R.id.foodExpenseEditText2);
        foodExpenseEditText3 = view.findViewById(R.id.foodExpenseEditText3);
        otherExpenseEditText1 = view.findViewById(R.id.otherExpenseEditText1);
        otherExpenseEditText2 = view.findViewById(R.id.otherExpenseEditText2);
        otherExpenseEditText3 = view.findViewById(R.id.otherExpenseEditText3);
        messageEditText = view.findViewById(R.id.messageEditText);
        treatmentStatusEditText = view.findViewById(R.id.treatmentStatusEditText);
        remarkEditText = view.findViewById(R.id.remarkEditText);

        ImageView transportExpenseFileImageView = view.findViewById(R.id.transportExpenseFileImageView);
        ImageView accommodationExpenseFileImageView = view.findViewById(R.id.accommodationExpenseFileImageView);
        ImageView foodExpenseFileImageView = view.findViewById(R.id.foodExpenseFileImageView);
        ImageView otherExpenseFileImageView = view.findViewById(R.id.otherExpenseFileImageView);

        createExpenseSheetSpinner = view.findViewById(R.id.createExpenseSheetSpinner);

        manageExpenseSheetTitle1TextView = view.findViewById(R.id.manageExpenseSheetTitle1);
        requestDateTextView = view.findViewById(R.id.requestDateTextView);
        startDateTextView = view.findViewById(R.id.startDateTextView);
        endDateTextView = view.findViewById(R.id.endDateTextView);
        transportCategoryTextView = view.findViewById(R.id.transportCategoryTextView);
        kilometersNumberTextView = view.findViewById(R.id.kilometersNumberTextView);
        transportExpenseTextView1 = view.findViewById(R.id.transportExpenseTextView1);
        transportExpenseTextView2 = view.findViewById(R.id.transportExpenseTextView2);
        transportExpenseTextView3 = view.findViewById(R.id.transportExpenseTextView3);
        transportExpenseFileTextView = view.findViewById(R.id.transportExpenseFileTextView);
        nightsNumberTextView = view.findViewById(R.id.nightsNumberTextView);
        accommodationExpenseTextView1 = view.findViewById(R.id.accommodationExpenseTextView1);
        accommodationExpenseTextView2 = view.findViewById(R.id.accommodationExpenseTextView2);
        accommodationExpenseTextView3 = view.findViewById(R.id.accommodationExpenseTextView3);
        accommodationExpenseFileTextView = view.findViewById(R.id.accommodationExpenseFileTextView);
        foodExpenseTextView1 = view.findViewById(R.id.foodExpenseTextView1);
        foodExpenseTextView2 = view.findViewById(R.id.foodExpenseTextView2);
        foodExpenseTextView3 = view.findViewById(R.id.foodExpenseTextView3);
        foodExpenseFileTextView = view.findViewById(R.id.foodExpenseFileTextView);
        otherExpenseTextView1 = view.findViewById(R.id.otherExpenseTextView1);
        otherExpenseTextView2 = view.findViewById(R.id.otherExpenseTextView2);
        otherExpenseTextView3 = view.findViewById(R.id.otherExpenseTextView3);
        messageTextView = view.findViewById(R.id.messageTextView);
        otherExpenseFileTextView = view.findViewById(R.id.otherExpenseFileTextView);
        manageExpenseSheetTitle2TextView = view.findViewById(R.id.manageExpenseSheetTitle2);
        treatmentStatusTextView = view.findViewById(R.id.treatmentStatusTextView);
        remarkTextView = view.findViewById(R.id.remarkTextView);

        progressDialog = new ProgressDialog(getActivity());
        requestQueue = Volley.newRequestQueue(requireActivity());

        ActivityResultLauncher<Intent> activityResultLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        Uri uri = data.getData();
                        try {
                            switch (id) {
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

        transportExpenseFileImageView.setOnClickListener(view1 -> {
            id = "transportExpenseFile";
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intent);
        });
        accommodationExpenseFileImageView.setOnClickListener(v -> {
            id = "accommodationExpenseFile";
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intent);
        });
        foodExpenseFileImageView.setOnClickListener(v -> {
            id = "foodExpenseFile";
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intent);
        });
        otherExpenseFileImageView.setOnClickListener(v -> {
            id = "otherExpenseFile";
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intent);
        });

        Bundle bundle = getArguments();

        if (bundle != null) {
            String data = bundle.getString(EXTRA_MESSAGE);
            kilometerCostsId = bundle.getString(KILOMETER_COSTS_ID);

            try {
                JSONObject jsonObject = new JSONObject(data);
                List<String> keys = Arrays.asList("expense_sheet_id", "user_id", "receipts_id", "request_date", "start_date", "end_date",
                        "transport_category", "kilometers_number", "kilometer_expense", "kilometer_expense_refund",
                        "kilometer_expense_unrefund", "transport_expense", "transport_expense_refund", "transport_expense_unrefund",
                        "transport_expense_file", "nights_number", "accommodation_expense", "accommodation_expense_refund",
                        "accommodation_expense_unrefund", "accommodation_expense_file", "food_expense", "food_expense_refund",
                        "food_expense_unrefund", "food_expense_file", "other_expense", "other_expense_refund", "other_expense_unrefund",
                        "message", "other_expense_file", "treatment_status", "remark");

                for (String key : keys) {
                    switch (key) {
                        case "expense_sheet_id":
                            expenseSheetId = jsonObject.getString(key);
                            break;
                        case "user_id":
                            userId = jsonObject.getString(key);
                            break;
                        case "receipts_id":
                            receiptsId = jsonObject.getString(key);
                            break;
                        case "request_date":
                            requestDate = jsonObject.getString(key);
                            break;
                        case "start_date":
                            startDate = jsonObject.getString(key);
                            break;
                        case "end_date":
                            endDate = jsonObject.getString(key);
                            break;
                        case "transport_category":
                            transportCategory = jsonObject.getString(key);
                            break;
                        case "kilometers_number":
                            kilometersNumber = jsonObject.getString(key);
                            break;
                        case "kilometer_expense":
                            kilometerExpense = jsonObject.getString(key);
                            break;
                        case "kilometer_expense_refund":
                            kilometerExpenseRefund = jsonObject.getString(key);
                            break;
                        case "kilometer_expense_unrefund":
                            kilometerExpenseUnrefund = jsonObject.getString(key);
                            break;
                        case "transport_expense":
                            transportExpense = jsonObject.getString(key);
                            break;
                        case "transport_expense_refund":
                            transportExpenseRefund = jsonObject.getString(key);
                            break;
                        case "transport_expense_unrefund":
                            transportExpenseUnrefund = jsonObject.getString(key);
                            break;
                        case "transport_expense_file":
                            transportExpenseFile = jsonObject.getString(key);
                            break;
                        case "nights_number":
                            nightsNumber = jsonObject.getString(key);
                            break;
                        case "accommodation_expense":
                            accommodationExpense = jsonObject.getString(key);
                            break;
                        case "accommodation_expense_refund":
                            accommodationExpenseRefund = jsonObject.getString(key);
                            break;
                        case "accommodation_expense_unrefund":
                            accommodationExpenseUnrefund = jsonObject.getString(key);
                            break;
                        case "accommodation_expense_file":
                            accommodationExpenseFile = jsonObject.getString(key);
                            break;
                        case "food_expense":
                            foodExpense = jsonObject.getString(key);
                            break;
                        case "food_expense_refund":
                            foodExpenseRefund = jsonObject.getString(key);
                            break;
                        case "food_expense_unrefund":
                            foodExpenseUnrefund = jsonObject.getString(key);
                            break;
                        case "food_expense_file":
                            foodExpenseFile = jsonObject.getString(key);
                            break;
                        case "other_expense":
                            otherExpense = jsonObject.getString(key);
                            break;
                        case "other_expense_refund":
                            otherExpenseRefund = jsonObject.getString(key);
                            break;
                        case "other_expense_unrefund":
                            otherExpenseUnrefund = jsonObject.getString(key);
                            break;
                        case "message":
                            message = jsonObject.getString(key);
                            break;
                        case "other_expense_file":
                            otherExpenseFile = jsonObject.getString(key);
                            break;
                        case "treatment_status":
                            treatmentStatus = jsonObject.getString(key);
                            break;
                        case "remark":
                            remark = jsonObject.getString(key);
                            break;
                    }
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        } else {
            Log.d(TAG, "Bundle is null.");
        }

        setData();

        mode = "reading";
        setMode();

        updateButton.setOnClickListener(v ->  {
            mode = "updating";
            setMode();
        });

        submitButton.setOnClickListener(v -> {
            String requestDateVar = requestDateEditText.getText().toString();
            String startDateVar = startDateEditText.getText().toString();
            String endDateVar = endDateEditText.getText().toString();
            String kilometersNumberVar, transportExpenseVar, nightsNumberVar, accommodationExpenseVar, foodExpenseVar, otherExpenseVar, messageVar;

            if (!kilometersNumberEditText.getText().toString().equals("0")) {
                kilometersNumberVar = kilometersNumberEditText.getText().toString();
            } else {
                kilometersNumberVar = "";
            }

            if (!transportExpenseEditText1.getText().toString().equals("0")) {
                transportExpenseVar = transportExpenseEditText1.getText().toString();
            } else {
                transportExpenseVar = "";
            }

            if (!nightsNumberEditText.getText().toString().equals("0")) {
                nightsNumberVar = nightsNumberEditText.getText().toString();
            } else {
                nightsNumberVar = "";
            }

            if (!accommodationExpenseEditText1.getText().toString().equals("0")) {
                accommodationExpenseVar = accommodationExpenseEditText1.getText().toString();
            } else {
                accommodationExpenseVar = "";
            }

            if (!foodExpenseEditText1.getText().toString().equals("0")) {
                foodExpenseVar = foodExpenseEditText1.getText().toString();
            } else {
                foodExpenseVar = "";
            }

            if (!otherExpenseEditText1.getText().toString().equals("0")) {
                otherExpenseVar = otherExpenseEditText1.getText().toString();
            } else {
                otherExpenseVar = "";
            }

            if (!messageEditText.getText().toString().equals("N/A")) {
                messageVar = messageEditText.getText().toString();
            } else {
                messageVar = "";
            }

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
                    transportExpenseFile64Base = Base64.encodeToString(bytes, Base64.DEFAULT);
                }

                if (accommodationExpenseFileBitmap != null) {
                    accommodationExpenseFileBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    byte[] bytes = byteArrayOutputStream.toByteArray();
                    accommodationExpenseFile64Base = Base64.encodeToString(bytes, Base64.DEFAULT);
                }

                if (foodExpenseFileBitmap != null) {
                    foodExpenseFileBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    byte[] bytes = byteArrayOutputStream.toByteArray();
                    foodExpenseFile64Base = Base64.encodeToString(bytes, Base64.DEFAULT);
                }

                if (otherExpenseFileBitmap != null) {
                    otherExpenseFileBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                    byte[] bytes = byteArrayOutputStream.toByteArray();
                    otherExpenseFile64Base = Base64.encodeToString(bytes, Base64.DEFAULT);
                }

                updateExpenseSheetRequest(kilometerCostsId, userId, requestDateVarFormatted, startDateVarFormatted, endDateVarFormatted, transportCategory,
                        kilometersNumberVar, transportExpenseVar, transportExpenseFile64Base, nightsNumberVar, accommodationExpenseVar, accommodationExpenseFile64Base, foodExpenseVar, foodExpenseFile64Base,
                        otherExpenseVar, messageVar, otherExpenseFile64Base);
            }
        });

        deleteButton.setOnClickListener(v -> deleteExpenseSheetRequest(expenseSheetId));

        return view;
    }
    private void updateExpenseSheetRequest(String kilometerCostsId, String userId, String requestDateVarFormatted, String startDateVarFormatted,
                                           String endDateVarFormatted, String transportCategory, String kilometersNumberVar, String transportExpenseVar, String transportExpenseFileBase64,
                                           String nightsNumberVar, String accommodationExpenseVar, String accommodationExpenseFileBase64, String foodExpenseVar, String foodExpenseFileBase64,  String otherExpenseVar,
                                           String messageVar, String otherExpenseFileBase64) {
        String updateExpenseSheetURL = "https://jeremiebayon.fr/api/controllers/functionalities/ExpenseSheet/UpdateExpenseSheet.php";

        progressDialog.setMessage("Envoi...");
        progressDialog.setCancelable(false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, updateExpenseSheetURL,
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
                params.put("user_id", userId);
                params.put("updateid", expenseSheetId);
                params.put("receipts_id", receiptsId);
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

                if (!Objects.equals(transportExpenseFile, "N/A")) {
                    params.put("transport_expense_file", transportExpenseFile);
                } else if (transportExpenseFileBase64 != null) {
                    params.put("transport_expense_file", transportExpenseFileBase64);
                } else {
                    params.put("transport_expense_file", "");
                }

                if (!Objects.equals(accommodationExpenseFile, "N/A")) {
                    params.put("accommodation_expense_file", accommodationExpenseFile);
                } else if (accommodationExpenseFileBase64 != null) {
                    params.put("accommodation_expense_file", accommodationExpenseFileBase64);
                } else {
                    params.put("accommodation_expense_file", "");
                }

                if (!Objects.equals(foodExpenseFile, "N/A")) {
                    params.put("food_expense_file", foodExpenseFile);
                } else if (foodExpenseFileBase64 != null) {
                    params.put("accommodation_expense_file", foodExpenseFileBase64);
                } else {
                    params.put("food_expense_file", "");
                }

                if (!Objects.equals(otherExpenseFile, "N/A")) {
                    params.put("other_expense_file", otherExpenseFile);
                } else if (otherExpenseFileBase64 != null) {
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
    private void deleteExpenseSheetRequest(String expenseSheetId) {
        String deleteExpenseSheetURL = "https://jeremiebayon.fr/api/controllers/functionalities/ExpenseSheet/DeleteExpenseSheet.php";

        progressDialog.setMessage("En cours...");
        progressDialog.setCancelable(false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, deleteExpenseSheetURL,
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
                params.put("id", expenseSheetId);
                return params;
            }
        };

        requestQueue.add(stringRequest);
        progressDialog.show();
    }
    private String formatDate(String value) {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

        try {
            Date date = inputDateFormat.parse(value);
            assert date != null;
            return outputDateFormat.format(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    @SuppressLint("SetTextI18n")
    private void setData() {
        String manageExpenseSheetTitle1TextViewText = manageExpenseSheetTitle1TextView.getText().toString();
        manageExpenseSheetTitle1TextView.setText(manageExpenseSheetTitle1TextViewText + expenseSheetId);

        List<String> items = Arrays.asList(requestDate, startDate, endDate);
        for (String item : items) {
            requestDateEditText.setText(formatDate(item));
            startDateEditText.setText(formatDate(item));
            endDateEditText.setText(formatDate(item));
        }

        String transportCategoryTextViewText = transportCategoryTextView.getText().toString();

        if (!Objects.equals(transportCategory, "1")) {
            transportCategoryTextView.setText(transportCategoryTextViewText + " Avion");
        } else if (!Objects.equals(transportCategory, "2")) {
            transportCategoryTextView.setText(transportCategoryTextViewText + " Train");
        } else if (!Objects.equals(transportCategory, "3")) {
            transportCategoryTextView.setText(transportCategoryTextViewText + " Bus/Car/Taxi");
        } else if (!Objects.equals(transportCategory, "4")) {
            transportCategoryTextView.setText(transportCategoryTextViewText + " Voiture");
        } else {
            transportCategoryTextView.setText(transportCategoryTextViewText + " N/A");
        }

        if (!kilometersNumber.equals("null")) {
            kilometersNumberEditText.setText(kilometersNumber);
        } else {
            kilometersNumberEditText.setText("0");
        }

        if (!transportExpense.equals("null")) {
            transportExpenseEditText1.setText(transportExpense);
            transportExpenseEditText2.setText(transportExpenseRefund);
            if (!transportExpenseUnrefund.equals("null")) {
                transportExpenseEditText3.setText(transportExpenseUnrefund);
            } else {
                transportExpenseEditText3.setText("0");
            }
        } else if (!kilometerExpense.equals("null")) {
            transportExpenseEditText1.setText(kilometerExpense);
            transportExpenseEditText2.setText(kilometerExpenseRefund);
            transportExpenseEditText3.setText(kilometerExpenseUnrefund);
            if (!kilometerExpenseUnrefund.equals("null")) {
                transportExpenseEditText3.setText(kilometerExpenseUnrefund);
            } else {
                transportExpenseEditText3.setText("0");
            }
        } else {
            transportExpenseEditText1.setText("0");
            transportExpenseEditText2.setText("0");
            transportExpenseEditText3.setText("0");
        }

        String transportExpenseFileTextViewText = transportExpenseFileTextView.getText().toString();
        if (!transportExpenseFile.equals("null")) {
            transportExpenseFileTextView.setText(transportExpenseFileTextViewText + " " + transportExpenseFile);
        } else {
            transportExpenseFileTextView.setText(transportExpenseFileTextViewText + " N/A");
        }

        if (!nightsNumber.equals("null")) {
            nightsNumberEditText.setText(nightsNumber);
        } else {
            nightsNumberEditText.setText("0");
        }

        if (!accommodationExpense.equals("null")) {
            accommodationExpenseEditText1.setText(accommodationExpense);
            accommodationExpenseEditText2.setText(accommodationExpenseRefund);
            if (!accommodationExpenseUnrefund.equals("null")) {
                accommodationExpenseEditText3.setText(accommodationExpenseUnrefund);
            } else {
                accommodationExpenseEditText3.setText("0");
            }
        } else {
            accommodationExpenseEditText1.setText("0");
            accommodationExpenseEditText2.setText("0");
            accommodationExpenseEditText3.setText("0");
        }

        String accommodationExpenseFileTextViewText = accommodationExpenseFileTextView.getText().toString();
        if (!accommodationExpenseFile.equals("null")) {
            accommodationExpenseFileTextView.setText(accommodationExpenseFileTextViewText + " " + accommodationExpenseFile);
        } else {
            accommodationExpenseFileTextView.setText(accommodationExpenseFileTextViewText + " N/A");
        }

        if (!foodExpense.equals("null")) {
            foodExpenseEditText1.setText(foodExpense);
            foodExpenseEditText2.setText(foodExpenseRefund);
            if (!foodExpenseUnrefund.equals("null")) {
                accommodationExpenseEditText3.setText(foodExpenseUnrefund);
            } else {
                foodExpenseEditText3.setText("0");
            }
        } else {
            foodExpenseEditText1.setText("0");
            foodExpenseEditText2.setText("0");
            foodExpenseEditText3.setText("0");
        }

        String foodExpenseFileTextViewText = foodExpenseFileTextView.getText().toString();
        if (!foodExpenseFile.equals("null")) {
            foodExpenseFileTextView.setText(foodExpenseFileTextViewText + " " + foodExpenseFile);
        } else {
            foodExpenseFileTextView.setText(foodExpenseFileTextViewText + " N/A");
        }

        if (!otherExpense.equals("null")) {
            otherExpenseEditText1.setText(otherExpense);
            otherExpenseEditText2.setText(otherExpenseRefund);
            if (!otherExpenseUnrefund.equals("null")) {
                otherExpenseEditText3.setText(otherExpenseUnrefund);
            } else {
                otherExpenseEditText3.setText("0");
            }
        } else {
            otherExpenseEditText1.setText("0");
            otherExpenseEditText2.setText("0");
            otherExpenseEditText3.setText("0");
        }

        if (!message.equals("null")) {
            messageEditText.setText(message);
        } else {
            messageEditText.setText("N/A");
        }

        String otherExpenseFileTextViewText = otherExpenseFileTextView.getText().toString();
        if (!otherExpenseFile.equals("null")) {
            otherExpenseFileTextView.setText(otherExpenseFileTextViewText + " " + otherExpenseFile);
        } else {
            otherExpenseFileTextView.setText(otherExpenseFileTextViewText + " N/A");
        }

        if (Objects.equals(treatmentStatus, "1")) {
            treatmentStatusEditText.setText("Validée");
        } else if (Objects.equals(treatmentStatus, "2")) {
            treatmentStatusEditText.setText("Refusée");
        } else {
            treatmentStatusEditText.setText("En attente de traitement");
        }

        if (!remark.equals("null")) {
            remarkEditText.setText(remark);
        } else {
            remarkEditText.setText("N/A");
        }
    }
    private void setItemsVisibility(String mode) {
        switch (mode) {
            case "reading":
                List<View> DisabledItems = Arrays.asList(
                        requestDateEditText, startDateEditText, endDateEditText, createExpenseSheetSpinner, kilometersNumberEditText,
                        transportExpenseEditText1, transportExpenseEditText2, transportExpenseEditText3, nightsNumberEditText, accommodationExpenseEditText1, accommodationExpenseEditText2, accommodationExpenseEditText3, foodExpenseEditText1, foodExpenseEditText2, foodExpenseEditText3, otherExpenseEditText1, otherExpenseEditText2, otherExpenseEditText3, messageEditText, treatmentStatusEditText, remarkEditText
                );

                for (View view : DisabledItems) {
                    view.setEnabled(false);
                }
                break;

            case "updating":
                List<View> hiddenItems = Arrays.asList(
                        requestDateTextView, startDateTextView, endDateTextView, transportCategoryTextView,
                        kilometersNumberTextView, transportExpenseTextView1, transportExpenseTextView2,
                        transportExpenseEditText2, transportExpenseTextView3, transportExpenseEditText3,
                        nightsNumberTextView, accommodationExpenseTextView1, accommodationExpenseTextView2,
                        accommodationExpenseEditText2, accommodationExpenseTextView3, accommodationExpenseEditText3,
                        foodExpenseTextView1, foodExpenseTextView2, foodExpenseEditText2, foodExpenseTextView3,
                        foodExpenseEditText3, otherExpenseTextView1, otherExpenseTextView2, otherExpenseEditText2,
                        otherExpenseTextView3, otherExpenseEditText3, messageTextView, manageExpenseSheetTitle2TextView,
                        treatmentStatusTextView, treatmentStatusEditText, remarkTextView, remarkEditText
                );

                for (View view : hiddenItems) {
                    view.setVisibility(View.GONE);
                }

                List<View> EnabledItems = Arrays.asList(
                        requestDateEditText, startDateEditText, endDateEditText, kilometersNumberEditText,
                        transportExpenseEditText1, nightsNumberEditText, accommodationExpenseEditText1,
                        foodExpenseEditText1, otherExpenseEditText1, messageEditText
                );

                for (View view : EnabledItems) {
                    view.setEnabled(true);
                }
                break;
        }
    }
    private void setButtonsVisibility(String mode) {
        switch (mode) {
            case "reading":
                switch (treatmentStatus) {
                    case "1":
                    case "2":
                        List<View> hiddenItems = Arrays.asList(updateButton, submitButton, deleteButton);

                        for (View view : hiddenItems) {
                            view.setVisibility(View.GONE);
                        }
                        break;
                    case "null":
                        List<View> visibleItems = Arrays.asList(updateButton, deleteButton);

                        for (View view : visibleItems) {
                            view.setVisibility(View.VISIBLE);
                        }

                        submitButton.setVisibility(View.GONE);
                        break;
                }
                break;
            case "updating":
                List<View> hiddenItems = Arrays.asList(updateButton, deleteButton);

                for (View view : hiddenItems) {
                    view.setVisibility(View.GONE);
                }

                submitButton.setVisibility(View.VISIBLE);
                break;
        }
    }
    private void updateItemsVisibility(String item) {
        switch (item) {
            case "Avion":
                transportCategory = "1";
                transportExpenseEditText1.setEnabled(true);
                kilometersNumberEditText.setEnabled(false);
                break;
            case "Train":
                transportCategory = "2";
                transportExpenseEditText1.setEnabled(true);
                kilometersNumberEditText.setEnabled(false);
                break;
            case "Bus/Car/Taxi":
                transportCategory = "3";
                transportExpenseEditText1.setEnabled(true);
                kilometersNumberEditText.setEnabled(false);
                break;
            case "Voiture":
                transportCategory = "4";
                transportExpenseEditText1.setEnabled(false);
                kilometersNumberEditText.setEnabled(true);
                break;
        }
    }
    @SuppressLint("SetTextI18n")
    private void setMode() {
        switch (mode) {
            case "reading":
                setItemsVisibility(mode);
                setButtonsVisibility(mode);
                break;
            case "updating":
                setItemsVisibility(mode);
                setButtonsVisibility(mode);
                createExpenseSheetSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String item = createExpenseSheetSpinner.getSelectedItem().toString();
                        updateItemsVisibility(item);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
                break;
        }
    }
}

