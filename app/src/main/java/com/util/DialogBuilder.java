package com.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.RolandAssoh.stopgalere.ci.R;

import model.Emploi;

import static com.util.Constant.ACTIVITY_FINISH;


public class DialogBuilder {

	private Activity act;
    private int actionCode;
	private Dialog mDialog;

	private TextView mDialogTitle;
	private TextView mDialogMessage;
	private TextView mDialogOkButton;
	private ImageView mDialogImage;
    private String title, message;

	public DialogBuilder(Activity act, int actionCode, String title, String message) {
		this.act = act;
        this.title = title;
        this.message = message;
        this.actionCode = actionCode;
	}

	public void showDialog() {
		if (mDialog == null) {
			mDialog = new Dialog(act, R.style.CustomDialogTheme);
		}
		mDialog.setContentView(R.layout.dialog_error_info);
		mDialog.setCancelable(true);
		mDialog.show();

		mDialogTitle = (TextView) mDialog.findViewById(R.id.dialog_universal_info_title);
		mDialogMessage = (TextView) mDialog.findViewById(R.id.dialog_universal_info_message);
		mDialogOkButton = (TextView) mDialog.findViewById(R.id.dialog_universal_info_ok);
		mDialogImage = (ImageView) mDialog.findViewById(R.id.dialog_universal_info_image);

        mDialogTitle.setText(title);
        mDialogMessage.setText(message);
		
		initDialogButtons();
	}

	private void initDialogButtons() {

		mDialogOkButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				mDialog.dismiss();
                if(actionCode == ACTIVITY_FINISH)
                act.finish();
			}
		});
	}

	public void dismissDialog() {
		mDialog.dismiss();
	}

    public static class Apply {

        private Activity act;
        private Dialog mDialog;

        private TextView titleText;
        private TextView messageText;
        private TextView continuButton, cancelButton;
        private ImageView mDialogImage;
        private String title, message, emploiEmail;
        private boolean isEmailAvailable;

        public Apply(Activity act, boolean emailAvailable, String title, String message, String emploiEmail){
            this.act = act;
            this.title = title;
            this.isEmailAvailable = emailAvailable;
            this.message = message;
            this.emploiEmail = emploiEmail;

        }

        public void showDialog() {
            if (mDialog == null) {
                mDialog = new Dialog(act, R.style.CustomDialogTheme);
            }
            mDialog.setContentView(R.layout.dialog_apply);
            mDialog.setCancelable(true);
            mDialog.show();

            titleText = (TextView) mDialog.findViewById(R.id.dialog_apply_title);
            messageText = (TextView) mDialog.findViewById(R.id.dialog_apply_message);
            continuButton = (TextView) mDialog.findViewById(R.id.dialog_apply_continu);
            mDialogImage = (ImageView) mDialog.findViewById(R.id.dialog_apply_image);
            cancelButton = (TextView) mDialog.findViewById(R.id.dialog_apply_cancel);
            if(!isEmailAvailable)
            {
                continuButton.setText(act.getString(R.string.signUp));
            }

            titleText.setText(title);
            messageText.setText(message);

            initDialogButtons();
        }

        private void initDialogButtons() {

            continuButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    if(isEmailAvailable)
                    {
                        // Open Email client
                        Intent intentEmail = new Intent(Intent.ACTION_SEND);
                        intentEmail.setType("message/rfc822");
                        intentEmail.putExtra(Intent.EXTRA_SUBJECT, "Example: CAISSIERE ANNE");
                        intentEmail.putExtra(Intent.EXTRA_TEXT, "Candidature (veuillez joindre les documents démandés");
                        intentEmail.putExtra(Intent.EXTRA_EMAIL,new String[]{emploiEmail} );

                        try{

                            act.startActivity(Intent.createChooser(intentEmail, "Dépôt de candidature encours ...."));
                        }catch (ActivityNotFoundException e)
                        {
                            Toast.makeText(act.getApplicationContext(), "Veuillez installer un client email avant l'envoi ", Toast.LENGTH_LONG).show();
                        }
                    }
                    else
                    {
                        String url = emploiEmail.trim();
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + url));
                        act.startActivity(i);
                    }

                    mDialog.dismiss();
                }
            });
            cancelButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    //
                    mDialog.dismiss();
                }
            });
        }


    }

    public static class LocationError {

        private Activity act;
        private Dialog mDialog;
        private TextView dialogTitle;
        private TextView dialogMessage;
        private TextView dialogButton;
        private ImageView dialogImage;
        private String title, message;

        public LocationError(Activity act, String title, String message) {
            this.act = act;
            this.title = title;
            this.message = message;
        }

        public void showDialog()
        {
            if(mDialog == null)
            {
                mDialog = new Dialog(act, R.style.CustomDialogTheme);
            }
            mDialog.setContentView(R.layout.dialog_location_error);
            mDialog.setCancelable(false);
            mDialog.show();

            dialogTitle = (TextView) mDialog.findViewById(R.id.map_dialog_title_Id);
            dialogMessage = (TextView) mDialog.findViewById(R.id.map_dialog_message_Id);
            dialogButton = (TextView) mDialog.findViewById(R.id.map_dialog_button_Id);
            dialogImage = (ImageView) mDialog.findViewById(R.id.map_dialog_image_Id);

            dialogTitle.setText(title);
            dialogMessage.setText(message);

            initDialogButtons();
        }
        private void initDialogButtons()
        {
            dialogButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDialog.dismiss();
                }
            });
        }

        public void dismissDialog()
        {
            mDialog.dismiss();
        }
    }

    public static class Exit {

        private Activity act;
        private Dialog mDialog;

        private TextView titleText;
        private TextView messageText;
        private TextView yesButton, nonButton;
        private ImageView mDialogImage;
        private String title, message;

        public Exit(Activity act, String title, String message) {
            this.act = act;
            this.title = title;
            this.message = message;
        }

        public void showDialog() {
            if (mDialog == null) {
                mDialog = new Dialog(act, R.style.CustomDialogTheme);
            }
            mDialog.setContentView(R.layout.dialog_exit);
            mDialog.setCancelable(true);
            mDialog.show();

            titleText = (TextView) mDialog.findViewById(R.id.dialog_exit_info_title);
            messageText = (TextView) mDialog.findViewById(R.id.dialog_universal_info_message);
            yesButton = (TextView) mDialog.findViewById(R.id.dialog_exit_yes);
            nonButton = (TextView) mDialog.findViewById(R.id.dialog_exit_no);
            mDialogImage = (ImageView) mDialog.findViewById(R.id.dialog_exit_info_image);

            titleText.setText(title);
            messageText.setText(message);

            initDialogButtons();
        }

        private void initDialogButtons() {

            final String appId = act.getPackageName(); //your application package name i.e play store application url

            yesButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    dismissDialog();
                    act.finish();
                }
            });
            nonButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    //
                    dismissDialog();
                }
            });
        }

        public void dismissDialog() {
            mDialog.dismiss();
        }
    }

    public static class Update {

        private Activity act;
        private Dialog mDialog;

        private TextView titleText;
        private TextView messageText;
        private TextView updateButton, cancelButton;
        private ImageView mDialogImage;
        private String title, message;

        public Update(Activity act, String title, String message) {
            this.act = act;
            this.title = title;
            this.message = message;
        }

        public void showDialog() {
            if (mDialog == null) {
                mDialog = new Dialog(act, R.style.CustomDialogTheme);
            }
            mDialog.setContentView(R.layout.dialog_update);
            mDialog.setCancelable(true);
            mDialog.show();

            titleText = (TextView) mDialog.findViewById(R.id.dialog_universal_info_title);
            messageText = (TextView) mDialog.findViewById(R.id.dialog_universal_info_message);
            updateButton = (TextView) mDialog.findViewById(R.id.dialog_info_update);
            mDialogImage = (ImageView) mDialog.findViewById(R.id.dialog_universal_info_image);
            cancelButton = (TextView) mDialog.findViewById(R.id.dialog_info_cancel);

            titleText.setText(title);
            messageText.setText(message);

            initDialogButtons();
        }

        private void initDialogButtons() {

            final String appId = act.getPackageName(); //your application package name i.e play store application url

            updateButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {

                    try {
                        act.startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("market://details?id=" + appId
                                )));

                    } catch (ActivityNotFoundException anfe) {
                        act.startActivity(new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id=" +appId
                                )));
                    }
                    mDialog.dismiss();
                }
            });
            cancelButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    //
                    mDialog.dismiss();
                }
            });
        }

        public void dismissDialog() {
            mDialog.dismiss();
        }
    }

    public static class LocationSetting {

        private Activity act;
        private Dialog mDialog;

        private TextView enableButton;
        private TextView cancelButton;
        private TextView messageText;


        public LocationSetting(Activity act) {
            this.act = act;
        }

        public void showDialog() {
            if (mDialog == null) {
                mDialog = new Dialog(act, R.style.CustomDialogTheme);
            }
            mDialog.setContentView(R.layout.dialog_gps);
            mDialog.setCancelable(false);
            mDialog.show();

            messageText = (TextView) mDialog.findViewById(R.id.map_dialog_setMessageId);
            enableButton = (TextView) mDialog.findViewById(R.id.map_dialog_enbale_Id);
            cancelButton = (TextView) mDialog.findViewById(R.id.map_dialog_cancel_Id);


            initDialogFreeButtons();
        }


        private void initDialogFreeButtons() {

            enableButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    //check input
                    Intent callGPSSettingIntent = new Intent(
                                                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                            act.startActivity(callGPSSettingIntent);
                    dismissDialog();
                }
            });

            cancelButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    dismissDialog();
                    act.finish();
                }
            });

        }


        public void dismissDialog() {
            mDialog.dismiss();
        }
    }

    public static class EmploisShare {

        private Activity act;
        private Dialog mDialog;
        private Emploi emploi;

        private TextView sendButton;
        private TextView cancelButton;
        private TextView titleName;
        private TextView titlePhone;
        private EditText inputName;
        private EditText inputPhone;
        private LinearLayout mDialogFreeLayout;


        public EmploisShare(Activity act, Emploi emploi) {
            this.act = act;
            this.emploi = emploi;
        }

        public void showDialog() {
            if (mDialog == null) {
                mDialog = new Dialog(act, R.style.CustomDialogTheme);
            }
            mDialog.setContentView(R.layout.dialog_share);
            mDialog.show();

            mDialogFreeLayout = (LinearLayout) mDialog.findViewById(R.id.dialog_social_layout);
            sendButton = (TextView) mDialog.findViewById(R.id.dialog_share_send);
            cancelButton = (TextView) mDialog.findViewById(R.id.dialog_share_cancel);
            titleName = (TextView) mDialog.findViewById(R.id.dialog_share_name);
            titlePhone = (TextView) mDialog.findViewById(R.id.dialog_share_phone);
            inputName = (EditText) mDialog.findViewById(R.id.dialog_share_name_edit);
            inputPhone = (EditText) mDialog.findViewById(R.id.dialog_share_phone_edit);



            Typeface sRobotoThin = Typeface.createFromAsset(act.getAssets(),
                    "fonts/Roboto-Light.ttf");
            inputName.setTypeface(sRobotoThin);
            inputPhone.setTypeface(sRobotoThin);

            initDialogFreeButtons();
        }


        private void initDialogFreeButtons() {

            sendButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    //check input
                    if (!inputName.getText().toString().equals("")|!inputPhone.getText().toString().equals("")) {
                        String smsMessage;
                        smsMessage ="Recherche de: "+ emploi.getTitle().toString().trim()+ " Date de publication: " +
                                emploi.getAddDate().toString().trim() + " Date limite: " + emploi.getEndDate().toString().trim() +
                                "\n" + "pour plus de details, veuillez télécharger l'application" +
                                "\n" + "http://play.google.com/store/apps/details?id=" +act.getPackageName() +
                                "\n" + "Votre Bienfaiteur(trice): " + inputName.getText().toString();
                        Intent sendIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+inputPhone.getText().toString()));
                        sendIntent.putExtra("sms_body", smsMessage);
                        act.startActivity(sendIntent);
                    }else
                    {
                        //Fields are empty
                        Toast.makeText(act.getApplicationContext(), "Veuillez remplir les 2 champs", Toast.LENGTH_LONG).show();
                    }
                    dismissDialog();
                }
            });

            cancelButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View view) {
                    dismissDialog();
                }
            });

        }


        public void dismissDialog() {
            mDialog.dismiss();
        }
    }
}
