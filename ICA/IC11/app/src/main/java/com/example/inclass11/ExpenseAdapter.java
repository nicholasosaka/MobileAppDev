package com.example.inclass11;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {
    private static final String TAG = "IC11-EA";

    public static InteractWithExpenseApp interact;

    public interface InteractWithExpenseApp{
        void selectExpense(int position);
        void deleteExpense(int position);
    }

    ArrayList<Expense> expenses = new ArrayList<>();

    Context ctx;

    public ExpenseAdapter(Context ctx, ArrayList<Expense> expenses){
        this.expenses = expenses;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ConstraintLayout rv_layout = (ConstraintLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_layout, parent, false);
        return new ViewHolder(rv_layout);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        interact = (InteractWithExpenseApp) ctx;

        holder.expenseName.setText(expenses.get(position).getTitle());
        holder.expenseCost.setText(String.format("%s", expenses.get(position).getCost()));

        holder.itemView.setLongClickable(true);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interact.selectExpense(position);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                interact.deleteExpense(position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView expenseName;
        TextView expenseCost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            expenseName = itemView.findViewById(R.id.adapterExpenseName);
            expenseCost = itemView.findViewById(R.id.adapterExpenseCost);
        }
    }
}
