package com.example.midterm;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private static String TAG = "MIDTERM_ADAPTER";

    public static InteractWithNote interact;

    public interface InteractWithNote{
        void selectNote(Note note);
        void deleteNote(Note note);
    }

    Context ctx;
    ArrayList<Note> notes = new ArrayList<>();


    public NoteAdapter(Context ctx, ArrayList<Note> notes) {
        this.ctx = ctx;
        this.notes = notes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout rv_layout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.note_layout, parent, false);
        return new ViewHolder(rv_layout);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        interact = (InteractWithNote) ctx;

        holder.noteTitle.setText(notes.get(position).getTitle());

        holder.noteTitle.setLongClickable(true);
        holder.noteTitle.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Log.d(TAG, "onLongClick: long clicked " + notes.get(position).getTitle() + " at " + position );
                interact.deleteNote(notes.get(position));
                return true;
            }
        });

        holder.noteTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clicked " + notes.get(position).getTitle() + " at " + position );
                interact.selectNote(notes.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView noteTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.noteTitleLayout);
        }
    }
}
