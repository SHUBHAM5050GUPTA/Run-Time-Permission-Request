package com.example.shubhamgupta.readingsms;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    public static  final  int MY_PERMISSIONS_REQUEST_READ_SMS_AND_CONTACTS=101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button smsButton= (Button) findViewById(R.id.button_sms);
        textView= (TextView) findViewById(R.id.textview_sms);

        smsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_SMS)
                        == PackageManager.PERMISSION_GRANTED&&
                        ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS)
                                == PackageManager.PERMISSION_GRANTED)
                {

                    Toast.makeText(MainActivity.this,"The Permission is Already Granted!",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    askSMSPermission();
                }

            }
        });
    }

    public void loadSMS()
    {
        StringBuilder builder=new StringBuilder();
        ContentResolver contentResolver=getContentResolver();
        Cursor cursor=contentResolver.query(Telephony.Sms.CONTENT_URI,null,null,null,null);

        if(cursor.getCount()>0)
        {
            int i=0;
            while(cursor.moveToNext()&&i<5)
            {

                String id=cursor.getString(cursor.getColumnIndex(Telephony.Sms._ID));
                String name=getContactbyPhoneNumber(getApplicationContext(),
                        cursor.getString(cursor.getColumnIndexOrThrow("address")));
                String dateString=cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE_SENT));

                Date date = new Date(Long.parseLong(dateString));
               // Cursor cursor1=contentResolver.query(Telephony.Sms.)

                builder.append("Name: ").append(name).append("\n").append("date: ").append(date).append("\n\n");
                i++;
            }
        }
        cursor.close();

        textView.setText(builder.toString());

    }

    public String getContactbyPhoneNumber(Context c, String phoneNumber) {

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        String[] projection = {ContactsContract.PhoneLookup.DISPLAY_NAME};
        Cursor cursor = c.getContentResolver().query(uri, projection, null, null, null);

        if (cursor == null) {
            return phoneNumber;
        }else {
            String name = phoneNumber;
            try {

                if (cursor.moveToFirst()) {
                    name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                }

            } finally {
                cursor.close();
            }

            return name;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_SMS_AND_CONTACTS
                    : {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    loadSMS();

                } else {

                    Toast.makeText(this,"Permission Not Granted",Toast.LENGTH_LONG).show();
                }
                return;
            }

        }

    }

    public void askSMSPermission()
    {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_SMS )&& ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.READ_CONTACTS))
            {
                new AlertDialog.Builder(this)
                        .setTitle("Permission needed to read the SMS and Contacts")
                        .setMessage("This permission is needed because of this and that")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.READ_SMS,
                                                Manifest.permission.READ_CONTACTS},
                                        MY_PERMISSIONS_REQUEST_READ_SMS_AND_CONTACTS);

                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create().show();
            }
            else
            {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_SMS,Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_SMS_AND_CONTACTS);
            }

    }

}
