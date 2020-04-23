package com.example.inclass11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditExpense extends AppCompatActivity {

    private FirebaseFirestore db;

    EditText expenseNameET;
    EditText expenseCostET;
    Spinner expenseCategorySpinner;

    Button save;
    Button cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expense);
        setTitle("Edit Expense");

        expenseNameET = findViewById(R.id.editExpenseNameET);
        expenseCostET = findViewById(R.id.editExpenseAmountET);
        expenseCategorySpinner = findViewById(R.id.editExpenseCategorySpinner);
        save = findViewById(R.id.editSaveButton);
        cancel = findViewById(R.id.editCancelButton);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        expenseCategorySpinner.setAdapter(adapter);

        Intent intent = getIntent();

        db = FirebaseFirestore.getInstance();

        final Expense expense = (Expense) intent.getSerializableExtra(ShowExpenses.EXPENSE_KEY);

        assert expense != null;
        expenseNameET.setText(expense.getTitle());
        expenseCostET.setText(String.format("%s", expense.getCost()));

        setSpinner(expense.getCategory());

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isDataValid()){
                    String title = expenseNameET.getText().toString().trim();
                    String category = expenseCategorySpinner.getSelectedItem().toString();
                    double cost = Double.parseDouble(expenseCostET.getText().toString());
                    expense.setTitle(title);
                    expense.setCategory(category);
                    expense.setCost(cost);

                    db.collection("expenses")
                            .document(expense.getId())
                            .update(expense.toHashMap())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    finish();
                                }
                            });
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void setSpinner(String category) {
        ArrayAdapter adapter = (ArrayAdapter) expenseCategorySpinner.getAdapter();
        int pos = adapter.getPosition(category);
        expenseCategorySpinner.setSelection(pos);
    }

    private boolean isDataValid() {
        boolean validity = true;

        double cost;
        String title;
        String category;

        title = expenseNameET.getText().toString().trim();
        category = expenseCategorySpinner.getSelectedItem().toString();

        if (category.equals("Select Category")) {
            TextView errorText = (TextView)expenseCategorySpinner.getSelectedView();
            errorText.setError("");
            errorText.setTextColor(Color.RED);//just to highlight that this is an error

            validity = false;
        }


        if (title.length() <= 0) {
            expenseNameET.setError("Expense title cannot be blank");
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
            Toast.makeText(this, "Check errors", Toast.LENGTH_SHORT).show();
        }

        return validity;
    }
}
