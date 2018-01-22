package data;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.RolandAssoh.stopgalere.ci.LeMotisDetailActivity;
import com.RolandAssoh.stopgalere.ci.R;

import java.util.ArrayList;

import model.LettreMotivation;

/**
 * Created by Obrina.KIMI on 7/3/2017.
 */
public class LeMotisCompleteAdapter extends ArrayAdapter<LettreMotivation>{

    private int layoutResource;
    private Activity activity;
    private ArrayList<LettreMotivation> leMotisList = new ArrayList<>();

    public LeMotisCompleteAdapter(Activity act, int resource, ArrayList<LettreMotivation> data ) {
        super(act, resource, data);

        layoutResource = resource;
        activity = act;
        leMotisList = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return leMotisList.size();
    }

    @Override
    public LettreMotivation getItem(int position) {
        return leMotisList.get(position);
    }

    @Override
    public int getPosition(LettreMotivation item) {
        return super.getPosition(item);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ViewHolder holder = null;

        if (row == null || (row.getTag() == null))
        {
            LayoutInflater inflater = LayoutInflater.from(activity);
            row = inflater.inflate(layoutResource, null);

            holder = new ViewHolder();
            holder.leMotisTitle = (TextView) row.findViewById(R.id.leMotisTitleId);

            row.setTag(holder);
        }else
        {
            holder = (ViewHolder) row.getTag();
        }

        holder.leMotis = getItem(position);
        holder.leMotisTitle.setText(holder.leMotis.getTitle());


//      go to details activity
        final ViewHolder finalHolder = holder;
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // put code to go to details activity
                Intent i = new Intent(activity, LeMotisDetailActivity.class);

                Bundle mBundle = new Bundle();
                mBundle.putSerializable("leMotisObj", finalHolder.leMotis);
                i.putExtras(mBundle);
                i.putExtra("lmDlOk","1");

                activity.startActivity(i);

            }
        });


        return row;
    }

    public class ViewHolder
    {
        LettreMotivation leMotis;
        TextView leMotisTitle;

    }



}
