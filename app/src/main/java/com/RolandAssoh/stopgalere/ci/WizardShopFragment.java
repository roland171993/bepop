package com.RolandAssoh.stopgalere.ci;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;


public class WizardShopFragment extends Fragment {

	private static final String ARG_POSITION = "position";

	private int position;
	private ImageView imageMap;
    private TextView messageCv, messageLeMotis, messageGps;

	public static WizardShopFragment newInstance(int position) {
		WizardShopFragment f = new WizardShopFragment();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		position = getArguments().getInt(ARG_POSITION);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_wizard_shop,
				container, false);

        imageMap =(ImageView) rootView.findViewById(R.id.wizard_image_map);
        messageCv = (TextView) rootView.findViewById(R.id.wizard_text_cv);
        messageLeMotis = (TextView) rootView.findViewById(R.id.wizard_text_lemotis);
        messageGps = (TextView) rootView.findViewById(R.id.wizard_text_gps);
		
		if (position == 0) {
            messageCv.setVisibility(View.VISIBLE);
            messageLeMotis.setVisibility(View.GONE);
            messageGps.setVisibility(View.GONE);
            imageMap.setVisibility(View.GONE);
		} else if (position == 1) {
            messageLeMotis.setVisibility(View.VISIBLE);
            messageCv.setVisibility(View.GONE);
            messageGps.setVisibility(View.GONE);
            imageMap.setVisibility(View.GONE);
		} else {
            messageGps.setVisibility(View.VISIBLE);
            imageMap.setVisibility(View.VISIBLE);
            messageCv.setVisibility(View.GONE);
            messageLeMotis.setVisibility(View.GONE);
		}

		ViewCompat.setElevation(rootView, 50);
		return rootView;
	}

}