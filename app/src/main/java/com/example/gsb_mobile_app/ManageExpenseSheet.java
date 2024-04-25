package com.example.gsb_mobile_app;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class ManageExpenseSheet extends Fragment {
    private static final String TAG = "ManageExpenseSheet";
    ProgressDialog progressDialog;
    RequestQueue requestQueue;
    private static final String EXTRA_MESSAGE = "com.example.gsb_mobile_app.extra.MESSAGE";
    public static ManageExpenseSheet newInstance(String message) {
        ManageExpenseSheet fragment = new ManageExpenseSheet();
        Bundle args = new Bundle();

        args.putString(EXTRA_MESSAGE, message);
        fragment.setArguments(args);

        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_expense_sheet, container, false);

        progressDialog = new ProgressDialog(getActivity());
        requestQueue = Volley.newRequestQueue(getActivity());

        progressDialog.setMessage("Récupération des données...");
        progressDialog.setCancelable(false);

        return view;
    }
}
