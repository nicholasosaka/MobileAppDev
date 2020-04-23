package com.example.inclass11;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.LogDescriptor;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements ExpenseAdapter.InteractWithExpenseApp {

    private FirebaseFirestore db;
    private String TAG = "IC11-MA";

    HashMap<String, Expense> expenses = new HashMap<>();
    ArrayList<Expense> rv_expenses = new ArrayList<>();


    private RecyclerView recyclerView;
    private Button addButton;
    private RecyclerView.LayoutManager rv_manager;
    private ExpenseAdapter rv_adapter;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Expense App");

        Toast.makeText(this, isConnected() ? "Connected" : "Not Connected", Toast.LENGTH_LONG).show();

        recyclerView = findViewById(R.id.recylcerView);
        addButton = findViewById(R.id.addExpenseButton);

        rv_manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(rv_manager);
        rv_adapter = new ExpenseAdapter(this, rv_expenses);
        recyclerView.setAdapter(rv_adapter);

        handler = new Handler(Looper.getMainLooper());

        db = FirebaseFirestore.getInstance();

        db.collection("expenses")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d(TAG, "New expense: " + dc.getDocument().getData());
                                    expenses.put(dc.getDocument().getId(), new Expense(dc.getDocument().getId(), dc.getDocument().getData()));
                                    break;
                                case MODIFIED:
                                    Log.d(TAG, "Modified expense: " + dc.getDocument().getData());
                                    expenses.remove(dc.getDocument().getId());
                                    expenses.put(dc.getDocument().getId(), new Expense(dc.getDocument().getId(), dc.getDocument().getData()));
                                    break;
                                case REMOVED:
                                    Log.d(TAG, "Removed expense: " + dc.getDocument().getData());
                                    expenses.remove(dc.getDocument().getId());
                            }
                        }

                        rv_expenses.clear();

                        for (Map.Entry<String, Expense> entry : expenses.entrySet()) {
                            rv_expenses.add(entry.getValue());
                        }

                        updateList();
                        Collections.sort(rv_expenses, new Comparator<Expense>() {
                            @Override
                            public int compare(Expense a, Expense b) {
                                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                                Date aDate = a.getDate();
                                Date bDate = b.getDate();

                                return aDate.before(bDate) ? 1 : aDate.after(bDate) ? -1 : 0;
                            }
                        });


                        rv_adapter.notifyDataSetChanged();
                        Log.d(TAG, "Current expenses  " + rv_expenses.toString());
                    }
                });


        createListeners();
    }

    private void updateList() {
        if(rv_expenses.size() == 0){
            findViewById(R.id.emptyListText).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.emptyListText).setVisibility(View.INVISIBLE);
        }

        Collections.sort(rv_expenses, new Comparator<Expense>() {
            @Override
            public int compare(Expense a, Expense b) {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                Date aDate = a.getDate();
                Date bDate = b.getDate();

                return aDate.before(bDate) ? 1 : aDate.after(bDate) ? -1 : 0;
            }
        });

        rv_adapter.notifyDataSetChanged();
    }

    private void createListeners() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toAddExpense = new Intent(MainActivity.this, AddExpense.class);
                startActivity(toAddExpense);
            }
        });
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return networkInfo != null && networkInfo.isConnected() &&
                (networkInfo.getType() == ConnectivityManager.TYPE_WIFI
                        || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE);
    }

    @Override
    public void selectExpense(int position) {
        final Expense expense = rv_expenses.get(position);
        Log.d(TAG, "selectExpense: " + expense);
        Intent toShowExpense = new Intent(MainActivity.this, ShowExpenses.class);

        toShowExpense.putExtra(ShowExpenses.EXPENSE_KEY, expense);

        startActivity(toShowExpense);

    }

    @Override
    public void deleteExpense(int position) {
        final Expense expense = rv_expenses.get(position);
        Log.d(TAG, "deleteExpense: " + expense);

        db.collection("expenses")
                .document(expense.getId())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, expense.getTitle() + " deleted", Toast.LENGTH_SHORT).show();
                                    rv_expenses.remove(expense);
                                    updateList();
                                }
                            });
                        }
                    }
                });

    }
}
