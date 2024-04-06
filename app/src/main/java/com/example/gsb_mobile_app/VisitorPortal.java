package com.example.gsb_mobile_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VisitorPortal extends AppCompatActivity {
    ProgressDialog progressDialog;
    RequestQueue requestQueue;
    Button createExpenseSheet;
    public static final String EXTRA_MESSAGE = "com.example.gsb_mobile_app.extra.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_visitor_portal);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        String id, firstName, lastName;

        try {
            JSONObject jsonObject = new JSONObject(message);
            id = jsonObject.getString("user_id");
            firstName = jsonObject.getString("first_name");
            lastName = jsonObject.getString("last_name").toUpperCase();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        String formattedData = "Bienvenue " + firstName + " " + lastName;
        TextView welcome = findViewById(R.id.visitorPortalTitle);
        welcome.setText(formattedData);

        createExpenseSheet = findViewById(R.id.createExpenseSheetButton);
        progressDialog = new ProgressDialog(this);
        requestQueue = Volley.newRequestQueue(this);

        progressDialog.setMessage("Récupération des données en cours...");
        progressDialog.setCancelable(false);

        gettingExpenseSheetsRequest(id);

        createExpenseSheet.setOnClickListener(v -> createExpenseSheet());
    }

    private void gettingExpenseSheetsRequest(String user_id) {
        String gettingExpenseSheetsURL = "https://jeremiebayon.fr/api/controllers/portals/visitor.php";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, gettingExpenseSheetsURL, response -> {
            progressDialog.dismiss();
            LinearLayout expenseSheetsList = findViewById(R.id.expenseSheetsList);
            LayoutInflater inflater = LayoutInflater.from(VisitorPortal.this);

            try {
                JSONObject jsonObject = new JSONObject(response);
                int status = jsonObject.getInt("status");
                if (status == 200) {
                    Toast.makeText(VisitorPortal.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
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
                    Toast.makeText(VisitorPortal.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(VisitorPortal.this, "Un problème est survenu. Veuillez recommencer.", Toast.LENGTH_LONG).show();
                }
            },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(VisitorPortal.this, "Échec de la connexion au serveur. Veuillez recommencer.", Toast.LENGTH_LONG).show();
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

    private void createExpenseSheet() {
        Intent intent = new Intent(VisitorPortal.this, CreateExpenseSheet.class);
        startActivity(intent);
    }

    public void logout(View view) {
        finish();
    }
}