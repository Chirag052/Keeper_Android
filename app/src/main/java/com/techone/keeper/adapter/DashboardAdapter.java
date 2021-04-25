package com.techone.keeper.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.techone.keeper.Constants;
import com.techone.keeper.NoteMaker;
import com.techone.keeper.R;
import com.techone.keeper.model.SingleNote;

import java.util.ArrayList;

public class DashboardAdapter extends RecyclerView.Adapter<DashboardAdapter.DataViewHolder>  {

    Context context;
    ArrayList<SingleNote> data = new ArrayList<>();
    private int position;
    FirebaseUser user;

    String uid;
    public DashboardAdapter(Context context, ArrayList<SingleNote> data) {
        this.context = context;
        this.data = data;
    }



    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater myInflater = LayoutInflater.from(context);
        View myownview = myInflater.inflate(R.layout.layout_view, parent, false);
        return new DataViewHolder(myownview);
    }

    @Override
    public void onViewRecycled(@NonNull DataViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    public void setPosition(int position) {
        this.position = position;
    }


    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        holder.title.setText(data.get(position).getTitle());
        holder.body.setText(data.get(position).getBody());
        holder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NoteMaker.class);
                Gson gson = new Gson();
                String dataToPass = gson.toJson(data.get(position));
                intent.putExtra("note", dataToPass);
                context.startActivity(intent);

            }
        });
        holder.mainView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //creating a popup menu

                PopupMenu popup = new PopupMenu(context, holder.title);
                //inflating menu from xml resource
                popup.inflate(R.menu.share_delete_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.share:
                                String shareBody = "";
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("text/plain");
                                if(data.get(position).getBody()!=null && data.get(position).getTitle()!=null) {
                                    shareBody = data.get(position).getTitle() + "\n\n" + data.get(position).getBody();
                                }else if (data.get(position).getTitle()!=null && data.get(position).getBody()==null){
                                    shareBody = data.get(position).getTitle();
                                }
                                else if(data.get(position).getTitle()==null && data.get(position).getBody()!=null){
                                    shareBody=data.get(position).getBody();
                                }
                                String shareSubject = "My subject";
                                intent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
                                intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                                context.startActivity(Intent.createChooser(intent, "Share via"));
                                break;
                            case R.id.delete:
                                Toast.makeText(context, "Note move to recycle bin", Toast.LENGTH_SHORT).show();
                                user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null) {
                                    uid = user.getUid();
                                } else {
                                    uid = "XYZ";
                                }
                                    data.get(position).setInTrash(true);
                                FirebaseFirestore db= FirebaseFirestore.getInstance();
                                db.collection(Constants.COLLECTION_REF).document(uid).collection(Constants.MESSAGES).document(data.get(position).getId()).set(data.get(position)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });
                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();
                return true;
            }
        });
        if (data.get(position).getColorPallete() != null && data.get(position).getColorPallete() != "") {
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.RECTANGLE);
            shape.setColor(Color.parseColor(data.get(position).getColorPallete()));
            shape.setStroke(1, Color.GRAY);
            shape.setCornerRadius(16);
            holder.mainView.setBackground(shape);
        } else {
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.RECTANGLE);
            shape.setColor(Color.parseColor("#FFFFFF"));
            shape.setStroke(1, Color.GRAY);
            shape.setCornerRadius(16);
            holder.mainView.setBackground(shape);
        }


    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class DataViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView body;
        RelativeLayout mainView;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            body = (itemView.findViewById(R.id.body));
            mainView = itemView.findViewById(R.id.main_view);
       }
    }
}



