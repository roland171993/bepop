package data;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.RolandAssoh.stopgalere.ci.CvDetailActivity;
import com.RolandAssoh.stopgalere.ci.LeMotisDetailActivity;
import com.RolandAssoh.stopgalere.ci.R;

import java.util.ArrayList;

import model.CV;
import model.LettreMotivation;

/**
 * Created by Obrina.KIMI on 7/3/2017.
 */
public class CvFastAdapter extends ArrayAdapter<CV>{

    private int layoutResource;
    private Activity activity;
    private ArrayList<CV> cvList = new ArrayList<>();

    public CvFastAdapter(Activity act, int resource, ArrayList<CV> data ) {
        super(act, resource, data);

        layoutResource = resource;
        activity = act;
        cvList = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return cvList.size();
    }

    @Override
    public CV getItem(int position) {
        return cvList.get(position);
    }

    @Override
    public int getPosition(CV item) {
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
            holder.cvTitle = (TextView) row.findViewById(R.id.cvTitleId);

            row.setTag(holder);
        }else
        {
            holder = (ViewHolder) row.getTag();
        }

        holder.cv = getItem(position);
        holder.cvTitle.setText(holder.cv.getTitle());


//      go to details activity
        final ViewHolder finalHolder = holder;
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // put code to go to details activity
                Intent i = new Intent(activity, CvDetailActivity.class);

                Bundle mBundle = new Bundle();
                mBundle.putSerializable("cvObj", finalHolder.cv);
                i.putExtras(mBundle);

                activity.startActivity(i);

            }
        });


        return row;
    }

    public class ViewHolder
    {
        CV cv;
        TextView cvTitle;

    }



}
