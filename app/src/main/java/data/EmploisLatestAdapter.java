package data;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.RolandAssoh.stopgalere.ci.EmploiDetailActivity;
import com.RolandAssoh.stopgalere.ci.MainActivity;
import com.RolandAssoh.stopgalere.ci.R;

import java.util.ArrayList;

import model.Emploi;

/**
 * Created by Obrina.KIMI on 7/3/2017.
 */
public class EmploisLatestAdapter extends ArrayAdapter<Emploi>{

    private int layoutResource;
    private Activity activity;
    private ArrayList<Emploi> emploiList = new ArrayList<>();

    public EmploisLatestAdapter(Activity act, int resource, ArrayList<Emploi> data ) {
        super(act, resource, data);

        layoutResource = resource;
        activity = act;
        emploiList = data;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return emploiList.size();
    }

    @Override
    public Emploi getItem(int position) {
        return emploiList.get(position);
    }

    @Override
    public int getPosition(Emploi item) {
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
            holder.emploiTitle = (TextView) row.findViewById(R.id.titleTextId);
            holder.emploiCity = (TextView) row.findViewById(R.id.cityTextId);
            holder.emploiSalary = (TextView) row.findViewById(R.id.salairyId);
            holder.emploiEndDate = (TextView) row.findViewById(R.id.endDateId);

            row.setTag(holder);
        }else
        {
            holder = (ViewHolder) row.getTag();
        }

        holder.emploi = getItem(position);

        holder.emploiTitle.setText(holder.emploi.getTitle());
        holder.emploiCity.setText(holder.emploi.getCity());
        holder.emploiSalary.setText(holder.emploi.getSalary());
        holder.emploiEndDate.setText(holder.emploi.getEndDate());
        //Hide empty field
        if (holder.emploi.getSalary().isEmpty() || holder.emploi.getSalary().equals("0") || holder.emploi.getSalary().trim().toLowerCase()
                .equals("fcfa")) {
            holder.emploiSalary.setVisibility(View.GONE);
        }

        return row;
    }

    public class ViewHolder
    {
        Emploi emploi;
        TextView emploiTitle;
        TextView emploiDescription;
        TextView emploiWebSite;
        TextView emploiSalary;
        TextView emploiCity;
        TextView emploiEndDate;

    }



}
