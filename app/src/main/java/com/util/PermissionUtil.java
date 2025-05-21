package com.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.RolandAssoh.stopgalere.ci.R;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Obrina.KIMI on 11/23/2017.
 */

public class PermissionUtil {
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static boolean isPermissionNeed = false;


    public PermissionUtil(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.permission_preference), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        isPermissionNeed = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }


    private  void requestGroupPermission(CopyOnWriteArrayList<String> permissions, Activity activity)
    {
        try{
            if(isPermissionNeed){
                if(permissions.size()>0){
                    String[] permissionList = new String[permissions.size()];
                    permissions.toArray(permissionList);
                    ActivityCompat.requestPermissions(activity, permissionList, Constant.REQUEST_GROUP_PERMISSION);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void requestAllPermissions(){
        try{
            if(isPermissionNeed){
                CopyOnWriteArrayList<String> permissionNeeded = new CopyOnWriteArrayList<>();
                CopyOnWriteArrayList<String> permissionAvailable = new CopyOnWriteArrayList<>();
                permissionAvailable.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                permissionAvailable.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                permissionAvailable.add(Manifest.permission.RECEIVE_SMS);
                permissionAvailable.add(Manifest.permission.READ_SMS);
                permissionAvailable.add(Manifest.permission.SEND_SMS);
                permissionAvailable.add(Manifest.permission.ACCESS_FINE_LOCATION);
                permissionAvailable.add(Manifest.permission.ACCESS_COARSE_LOCATION);
                permissionAvailable.add(Manifest.permission.READ_PHONE_STATE);
                permissionAvailable.add(Manifest.permission.VIBRATE);

                for(String permission: permissionAvailable){
                    if(ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                        permissionNeeded.add(permission);
                }
                if(permissionNeeded.size() > 0)
                    requestGroupPermission(permissionNeeded, (Activity) context);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onAllPermissionError( CopyOnWriteArrayList<String> nameArray){
        try{
            if(isPermissionNeed){
                if(nameArray.size() > 0){
                    String content = context.getString(R.string.error_permission_all_left)+ " ";
                    if (context != null){
                        for(String name: nameArray){


                            if(name.equals(Manifest.permission.READ_EXTERNAL_STORAGE)){
                                if(!content.contains(context.getString(R.string.perm_storage)))
                                    content = content + context.getString(R.string.perm_storage) + " ";
                            }
                            if(name.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                                if(!content.contains(context.getString(R.string.perm_storage)))
                                    content = content + context.getString(R.string.perm_storage) + " ";

                            }

                            if(name.equals(Manifest.permission.RECEIVE_SMS)){
                                if(!content.contains(context.getString(R.string.perm_sms)))
                                    content = content + context.getString(R.string.perm_sms) + " ";
                            }

                            if(name.equals(Manifest.permission.READ_SMS)){
                                if(!content.contains(context.getString(R.string.perm_sms)))
                                    content = content + context.getString(R.string.perm_sms) + " ";
                            }

                            if(name.equals(Manifest.permission.SEND_SMS)){
                                if(!content.contains(context.getString(R.string.perm_sms)))
                                    content = content + context.getString(R.string.perm_sms) + " ";
                            }

                            if(name.equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                                if(!content.contains(context.getString(R.string.perm_position)))
                                    content = content + context.getString(R.string.perm_position) + " ";
                            }

                            if(name.equals(Manifest.permission.ACCESS_COARSE_LOCATION)){
                                if(!content.contains(context.getString(R.string.perm_position)))
                                    content = content + context.getString(R.string.perm_position) + " ";
                            }

                            if(name.equals(Manifest.permission.READ_PHONE_STATE)){
                                if(!content.contains(context.getString(R.string.perm_phone)))
                                    content = content + context.getString(R.string.perm_phone )+ " ";
                            }

                            if(name.equals(Manifest.permission.VIBRATE)){
                                if(!content.contains(context.getString(R.string.perm_phone)))
                                    content = content + context.getString(R.string.perm_phone )+ " ";
                            }
                        }

                    }

                    content = content + " " + context.getString(R.string.error_permission_all_right);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(context.getString(R.string.error_permission_all_title));
                    builder.setMessage(content);


                    builder.setPositiveButton(context.getString(R.string.allow), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package",(context.getPackageName()), null);
                            intent.setData(uri);
                            context.startActivity(intent);
                        }
                    });

                    builder.setNegativeButton(context.getString(R.string.cancel_dialog), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            ((Activity) context).finish();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean isPermissionsGranted(){
        CopyOnWriteArrayList<String> permissionAvailable = new CopyOnWriteArrayList<>();
        permissionAvailable.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissionAvailable.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissionAvailable.add(Manifest.permission.RECEIVE_SMS);
        permissionAvailable.add(Manifest.permission.READ_SMS);
        permissionAvailable.add(Manifest.permission.SEND_SMS);
        permissionAvailable.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissionAvailable.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionAvailable.add(Manifest.permission.READ_PHONE_STATE);
        permissionAvailable.add(Manifest.permission.VIBRATE);
        for(String permission: permissionAvailable){
            if(ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }

    public @Nullable
    CopyOnWriteArrayList<String> getPermissionsDenied(){
        try{
            CopyOnWriteArrayList<String> permissionAvailable = new CopyOnWriteArrayList<>();
            CopyOnWriteArrayList<String> permissionDenied = new CopyOnWriteArrayList<>();

            permissionAvailable.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            permissionAvailable.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            permissionAvailable.add(Manifest.permission.RECEIVE_SMS);
            permissionAvailable.add(Manifest.permission.READ_SMS);
            permissionAvailable.add(Manifest.permission.SEND_SMS);
            permissionAvailable.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissionAvailable.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissionAvailable.add(Manifest.permission.READ_PHONE_STATE);
            permissionAvailable.add(Manifest.permission.VIBRATE);
            for(String permission: permissionAvailable){
                if(ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                    permissionDenied.add(permission);
            }
            return permissionDenied;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }



    public boolean isAllPermissionsGranded(){
        try{
            if(isPermissionNeed){
                CopyOnWriteArrayList<String> permissionNeeded = new CopyOnWriteArrayList<>();
                CopyOnWriteArrayList<String> permissionAvailable = new CopyOnWriteArrayList<>();
                permissionAvailable.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                permissionAvailable.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                permissionAvailable.add(Manifest.permission.RECEIVE_SMS);
                permissionAvailable.add(Manifest.permission.READ_SMS);
                permissionAvailable.add(Manifest.permission.SEND_SMS);
                permissionAvailable.add(Manifest.permission.ACCESS_FINE_LOCATION);
                permissionAvailable.add(Manifest.permission.ACCESS_COARSE_LOCATION);
                permissionAvailable.add(Manifest.permission.READ_PHONE_STATE);
                permissionAvailable.add(Manifest.permission.VIBRATE);
                for(String permission: permissionAvailable){
                    if(ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                        permissionNeeded.add(permission);
                }
                if(permissionNeeded.size() > 0){
                    return false;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }



}
