package com.techone.keeper.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.techone.keeper.R;

public class AboutUsSliderAdapter extends PagerAdapter {
    Context ctx;

    public AboutUsSliderAdapter(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater=(LayoutInflater)ctx.getSystemService(ctx.LAYOUT_INFLATER_SERVICE);
        View view =layoutInflater.inflate(R.layout.aboutus_slider,container,false);
        container.addView(view);
        ImageView profile=view.findViewById(R.id.profile);
        ImageView gmailicon=view.findViewById(R.id.gmail);
        ImageView linkedin_icon=view.findViewById(R.id.linkedin);
        ImageView phoneicon=view.findViewById(R.id.person);
        TextView name=view.findViewById(R.id.name);
        TextView desc=view.findViewById(R.id.desc);
        TextView mailid=view.findViewById(R.id.mailid);
        TextView phoneno=view.findViewById(R.id.phone_no);
        TextView linkedin_id=view.findViewById(R.id.linkedin_name);

        switch (position){
            case 0:
                profile.setImageResource(R.drawable.aksh);
                name.setText("Aksh Mehta");
                desc.setText("Android App Developer");
                mailid.setText("akshmehta576@gmail.com");
                mailid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:" + "akshmehta576@gmail.com")); // only email apps should handle this

                        ctx.startActivity(intent);
                    }
                });
                linkedin_id.setText("https://www.linkedin.com/in/aksh-mehta-2001");
                linkedin_id.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i1 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/aksh-mehta-2001"));
                        ctx.startActivity(i1);
                    }
                });
                phoneno.setText("+91 7976438203");
                phoneno.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i2=new Intent(Intent.ACTION_DIAL, Uri.parse("tel:7976438203"));
                        ctx.startActivity(i2);
                    }
                });
                break;
            case 1:
                profile.setImageResource(R.drawable.chirag);
                name.setText("Chirag Rameja");
                desc.setText("Android App Developer");
                mailid.setText("ramejachirag052@gmail.com");
                mailid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:" + "ramejachirag052@gmail.com"));
                        ctx.startActivity(intent);
                    }
                });
                linkedin_id.setText("https://www.linkedin.com/in/chirag-rameja");
                linkedin_id.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i1 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/chirag-rameja"));
                        ctx.startActivity(i1);
                    }
                });
                phoneno.setText("+91 6376651155");
                phoneno.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i2=new Intent(Intent.ACTION_DIAL, Uri.parse("tel:6376651155"));
                        ctx.startActivity(i2);
                    }
                });
                break;
            case 2:
                profile.setImageResource(R.drawable.monty);
                name.setText("Mohit Chug (Mentor)");
                desc.setText("Software Engineer");
                mailid.setText("chug.mohit7@gmail.com");

                mailid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:" + "chug.mohit7@gmail.com"));
                        ctx.startActivity(intent);
                    }
                });
                linkedin_id.setText("https://www.linkedin.com/in/mohitchug07");
                linkedin_id.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i1 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/mohitchug07"));
                        ctx.startActivity(i1);
                    }
                });
                phoneno.setText("+91 7976062641");
                phoneno.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i2=new Intent(Intent.ACTION_DIAL, Uri.parse("tel:7976062641"));
                        ctx.startActivity(i2);
                    }
                });
                break;
        }

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
