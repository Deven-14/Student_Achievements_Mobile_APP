package com.bmsce.studentachievements.Achievement;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import com.bmsce.studentachievements.R;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AchievementsRecViewAdapter extends RecyclerView.Adapter<AchievementsRecViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Achievement> achievements;
    private int[] event_colors;
    private Random rand;


    public AchievementsRecViewAdapter(Context context, ArrayList<Achievement> achievements) {
        this.context = context;
        this.achievements = achievements;
        rand = new Random();
        event_colors = context.getResources().getIntArray(R.array.event_colors);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.achievements_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nameOfEvent.setText(achievements.get(position).getNameOfEvent());
        holder.detailsOfEvent.setText(achievements.get(position).getDetailsOfEvent());
        String certificate_link = achievements.get(position).getCertificate();
        holder.certificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(certificate_link.compareTo("None") == 0) {
                    Toast.makeText(context, "No Certificate Available", Toast.LENGTH_SHORT).show();
                } else {
                    Uri uri = Uri.parse(certificate_link);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    context.startActivity(intent);
                }
            }
        });
        int randIndex = rand.nextInt(event_colors.length);
        holder.parent.setCardBackgroundColor(event_colors[randIndex]);
    }

    @Override
    public int getItemCount() {
        return achievements.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CardView parent;
        private TextView nameOfEvent;
        private TextView detailsOfEvent;
        private ImageButton certificate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.achievements_list_item_layout);
            nameOfEvent = itemView.findViewById(R.id.name_of_event);
            detailsOfEvent = itemView.findViewById(R.id.details_of_event);
            certificate = itemView.findViewById(R.id.certificate);
        }
    }
}
