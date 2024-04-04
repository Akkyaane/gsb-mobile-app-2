package com.example.gsb_mobile_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText email, password;
    Button login;
    ProgressDialog progressDialog;
    RequestQueue requestQueue;
    public static final String EXTRA_MESSAGE = "com.example.gsb_mobile_app.extra.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);
        login = findViewById(R.id.loginButton);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Connexion en cours... Veuillez patienter.");
        progressDialog.setCancelable(false);

        requestQueue = Volley.newRequestQueue(this);

        login.setOnClickListener(v -> {
            String emailVar = email.getText().toString();
            String passwordVar = password.getText().toString();

            if (emailVar.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Le champ 'E-mail' ne peut être vide.", Toast.LENGTH_SHORT).show();
            } else if (passwordVar.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Le champ 'Mot de passe' ne peut être vide.", Toast.LENGTH_SHORT).show();
            } else {
                loginRequest(emailVar, passwordVar);
            }
        });
    }

    private void loginRequest(String emailVar, String passwordVar) {
        String loginUrl = "https://jeremiebayon.fr/api/controllers/authentication/login.php";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, loginUrl, response -> {
            progressDialog.dismiss();

            try {
                JSONObject jsonObject = new JSONObject(response);
                int status = jsonObject.getInt("status");
                if (status == 200) {
                    Toast.makeText(MainActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(this, VisitorPortal.class);
                    intent.putExtra(EXTRA_MESSAGE, jsonObject.getString("data"));
                    Log.d("Response", response);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Un problème est survenu. Veuillez recommencer.", Toast.LENGTH_LONG).show();
                }
            },
                error -> {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Échec de la connexion au serveur. Veuillez vérifier votre connexion à Internet et recommencer.", Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", emailVar);
                params.put("password", passwordVar);
                return params;
            }
        };

        progressDialog.show();
        requestQueue.add(stringRequest);
    }
}