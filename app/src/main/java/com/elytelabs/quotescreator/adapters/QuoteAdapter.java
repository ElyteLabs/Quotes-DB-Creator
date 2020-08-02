package com.elytelabs.quotescreator.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.elytelabs.quotescreator.activities.DetailActivity;
import com.elytelabs.quotescreator.utils.Constants;

public class QuoteAdapter extends RecyclerView.Adapter<QuoteAdapter.ViewHolder> {

    private Cursor cursor;
    private Context context;

    public QuoteAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View v = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(v, context);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.word.setText(Html.fromHtml(cursor.getString(1)));
    }

    @Override
    public int getItemCount() {
        if (cursor == null) return 0;
        return cursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        Context context;
        TextView word;

        public ViewHolder(@NonNull View itemView, final Context context) {
            super(itemView);

            this.context = context;
            word = itemView.findViewById(android.R.id.text1);
            itemView.setOnClickListener(v -> {

                int position = getBindingAdapterPosition();
                cursor.moveToPosition(position);

                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra(Constants.STRING_EXTRA_ID, cursor.getString(0));
                intent.putExtra(Constants.STRING_EXTRA_QUOTE, cursor.getString(1));
                context.startActivity(intent);

            });

        }
    }
}


