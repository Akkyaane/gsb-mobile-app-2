package com.example.gsb_mobile_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

public class SecondActivity extends AppCompatActivity {
    private static final String TAG = "SecondActivity";
    private VisitorPortal visitorPortalFragment;
    private CreateExpenseSheet createExpenseSheetFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        String userId, kilometerCostsId, roleId, firstName, lastName, email, status;
        visitorPortalFragment = new VisitorPortal();
        createExpenseSheetFragment = new CreateExpenseSheet();

        if (message != null) {
            try {
                JSONObject jsonObject = new JSONObject(message);
                userId = jsonObject.getString("user_id");
                kilometerCostsId = jsonObject.getString("kilometer_costs_id");
                roleId = jsonObject.getString("role_id");
                firstName = jsonObject.getString("first_name");
                lastName = jsonObject.getString("last_name");
                email = jsonObject.getString("email");
                status = jsonObject.getString("status");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            Bundle bundle = new Bundle();
            bundle.putString("userId", userId);
            bundle.putString("kilometerCostsId", kilometerCostsId);
            bundle.putString("roleId", roleId);
            bundle.putString("firstName", firstName);
            bundle.putString("lastName", lastName);
            bundle.putString("email", email);
            bundle.putString("status", status);

            visitorPortalFragment.setArguments(bundle);
        } else {
            Log.d(TAG, "Message is null.");
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.navbar);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.home) {
                loadFragment(visitorPortalFragment);
            } else if (itemId == R.id.createExpenseSheet) {
                loadFragment(createExpenseSheetFragment);
            }

            return true;
        });

        loadFragment(visitorPortalFragment);

        bottomNavigationView.getMenu().findItem(R.id.home).setChecked(true);
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }
}
