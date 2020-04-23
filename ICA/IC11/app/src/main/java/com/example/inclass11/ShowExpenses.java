package com.example.inclass11;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ShowExpenses extends AppCompatActivity {

    public static final String EXPENSE_KEY = "EXPENSE";

    TextView nameTV;
    TextView categoryTV;
    TextView costTV;
    TextView dateTV;

    Button editExpense;
    Button close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_expenses);
        setTitle("Show Expense");

        nameTV = findViewById(R.id.showExpenseName);
        categoryTV = findViewById(R.id.showExpenseCategory);
        costTV = findViewById(R.id.showExpenseAmount);
        dateTV = findViewById(R.id.showExpenseDate);

        editExpense = findViewById(R.id.showEditExpenseButton);
        close = findViewById(R.id.showCloseExpenseButton);

        Intent intent = getIntent();

        final Expense expense = (Expense) intent.getSerializableExtra(EXPENSE_KEY);

        assert expense != null;

        String expenseName = expense.getTitle();
        String expenseCategory = expense.getCategory();
        double expenseCost = expense.getCost();
        Date expenseDate = expense.getDate();

        nameTV.setText(expenseName);
        categoryTV.setText(expenseCategory);
        costTV.setText(String.format("$%s", expenseCost));
        dateTV.setText(formatDate(expenseDate));

        editExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toEditExpense = new Intent(ShowExpenses.this, EditExpense.class);
                toEditExpense.putExtra(ShowExpenses.EXPENSE_KEY, expense);

                startActivity(toEditExpense);
                finish();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private String formatDate(Date date) {
        DateFormat formatter = new SimpleDateFormat("MM/dd/YYYY");
        String formattedDate = formatter.format(date);

        return formattedDate;
    }
}
