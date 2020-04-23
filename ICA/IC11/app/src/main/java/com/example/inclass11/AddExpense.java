package com.example.inclass11;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class AddExpense extends AppCompatActivity {


    private FirebaseFirestore db;

    EditText expenseTitleET;
    EditText expenseCostET;
    Spinner expenseCategorySpinner;

    Button add;
    Button cancel;
    private String TAG = "IC11-AE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        setTitle("Add Expense");


        expenseTitleET = findViewById(R.id.addExpenseNameEditText);
        expenseCostET = findViewById(R.id.addAmountEditText);
        expenseCategorySpinner = findViewById(R.id.addCategorySpinner);
        add = findViewById(R.id.addExpenseButton);
        cancel = findViewById(R.id.addCancelButton);

        db = FirebaseFirestore.getInstance();


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDataValid()) {
                    double cost = Double.parseDouble(expenseCostET.getText().toString());
                    String title = expenseTitleET.getText().toString().trim();
                    String category = expenseCategorySpinner.getSelectedItem().toString();
                    Date date = new Date();

                    final Expense generatedExpense = new Expense(title, category, cost, date);

                    db.collection("expenses").add(generatedExpense)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    generatedExpense.setId(documentReference.getId());
                                }
                            });

                    finish();
                }
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        expenseCategorySpinner.setAdapter(adapter);
    }

    private boolean isDataValid() {
        boolean validity = true;

        double cost;
        String title;
        String category;

        title = expenseTitleET.getText().toString().trim();
        category = expenseCategorySpinner.getSelectedItem().toString();

        if (category.equals("Select Category")) {
            TextView errorText = (TextView)expenseCategorySpinner.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error

            validity = false;
        }


        if (title.length() <= 0) {
            expenseTitleET.setError("Expense title cannot be blank");
            validity = false;
        }

        try {
            cost = Double.parseDouble(expenseCostET.getText().toString());
        } catch (NumberFormatException nfe) {
            expenseCostET.setError("Amount cannot be blank");
            Toast.makeText(this, "Check errors", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (cost <= 0) {
            expenseCostET.setError("Cost must be higher than $0.00");
            validity = false;
        }

        int indexOfDecimal = expenseCostET.getText().toString().indexOf(".");

        if (indexOfDecimal != -1 && expenseCostET.getText().toString().substring(indexOfDecimal+1).length() > 2){
            expenseCostET.setError("Cost must be in increments of $0.01");
            validity = false;
        }

        if(!validity){
            Toast.makeText(this, "Invalid Data", Toast.LENGTH_SHORT).show();
        }

        return validity;
    }
}