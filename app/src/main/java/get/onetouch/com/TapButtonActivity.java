package get.onetouch.com;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.skyfishjy.library.RippleBackground;

import org.w3c.dom.Text;

import java.security.PublicKey;
import java.util.ArrayList;

import get.onetouch.com.models.User;

/**
 * Created by Gulzar on 29-08-2016.
 */
public class TapButtonActivity extends BaseActivity implements View.OnClickListener{
    Toolbar toolbar;
    TextView mAppTitle;
    Switch miamIn;
    Boolean iAmIn=true;
    public String Uid;
    public String Ulat,Ulang,helpuid;
    Boolean help=false;
    public String TAG="TapButtonActivity";
    ValueEventListener postListener;
    RippleBackground rippleBackground;
    int flagRipple=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tapbutton);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        mAppTitle = (TextView) findViewById(R.id.AppT);
        miamIn= (Switch) findViewById(R.id.actionbar_service_toggle);
        Typeface Roboto = Typeface.createFromAsset(getAssets(), "CaviarDreams.ttf");
        mAppTitle.setTypeface(Roboto);
        mAppTitle.setTextColor(Color.WHITE);
        rippleBackground=(RippleBackground)findViewById(R.id.content);





        initialize();
        initializeAuthRef();
       findViewById(R.id.fab).setOnClickListener(this);
        Uid=getUserId();
     miamIn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
         @Override
         public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
             if(isChecked==true)
                 RunInBackground();
             else
                 StopService();
         }
     });


        postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                User mUser = dataSnapshot.getValue(User.class);
                Boolean mHelp=mUser.help;
                if(mUser.help==true) {
                    help = true;
                    helpuid = mUser.helpUserId;
                    Log.d(TAG, helpuid);
                }
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };





    }

    private void StopService() {
        //Temporary terminate the Service
        if(isMyServiceRunning(BackgroundLocationService.class))
            stopService(new Intent(TapButtonActivity.this,BackgroundLocationService.class));
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void onStart() {
        super.onStart();
        mDatabase.child("users").child(Uid).addValueEventListener(postListener);
    }

//    @Override
//    public void onStop() {
//        super.onStop();
//        if (postListener != null) {
//            mDatabase.child("users").child(Uid).removeEventListener(postListener);
//
//        }
//    }


        private void RunInBackground() {
        Toast.makeText(TapButtonActivity.this, "Service started", Toast.LENGTH_SHORT).show();
        //BackgroudLocationService is started
        startService(new Intent(getBaseContext(), BackgroundLocationService.class));
        //Toast to display the start service
        Toast.makeText(getBaseContext(), "Service started", Toast.LENGTH_SHORT).show();

    }
    void findUserLatLong()
    {
        showProgressDialog();
        mDatabase.child("users").child(Uid).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                            User mUser=dataSnapshot.getValue(User.class);
                              Ulat=mUser.lat;
                            Ulang=mUser.lang;
                        hideProgressDialog();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("XXXX", "getUser:onCancelled", databaseError.toException());
                    }
                });


    }

    void findAndSendNotificationToNearUser(final String Uid)
    {
        showProgressDialog();
        mDatabase.child("users").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                       for(DataSnapshot ChildSnapshot:dataSnapshot.getChildren())
                       {
                           User mUser=ChildSnapshot.getValue(User.class);
                           float results[]=new float[4];
                            Location.distanceBetween(Double.parseDouble(Ulat),
                                   Double.parseDouble(Ulang),Double.parseDouble(mUser.lat),Double.parseDouble(mUser.lang),results);
                           float dist=results[0];
                           Log.d(TAG,String.valueOf(dist));
                           if(dist<1000.0 && !ChildSnapshot.getKey().toString().equals(Uid))
                           {
                               mUser.help=true;
                               mUser.helpUserId=Uid;
                               mDatabase.child("users").child(ChildSnapshot.getKey()).setValue(mUser);
                           }
                       }


                        hideProgressDialog();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("XXXX", "getUser:onCancelled", databaseError.toException());
                    }
                });
    }
    void resetNotificationToNearUser()
    {
        showProgressDialog();
        mDatabase.child("users").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot ChildSnapshot:dataSnapshot.getChildren())
                        {
                            User mUser=ChildSnapshot.getValue(User.class);
                            mUser.help=false;
                            mUser.helpUserId="";
                            mDatabase.child("users").child(ChildSnapshot.getKey()).setValue(mUser);
                        }
                        hideProgressDialog();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("XXXX", "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.fab:


                if(!rippleBackground.isRippleAnimationRunning()){
                    rippleBackground.startRippleAnimation();
                    findUserLatLong();
                    findAndSendNotificationToNearUser(Uid);
                    Toast.makeText(TapButtonActivity.this, "Don't Panick\n Getting Help", Toast.LENGTH_LONG).show();
                   // sendSMS("9742634857","One Touch");
                }
                else {
                    rippleBackground.stopRippleAnimation();
                    resetNotificationToNearUser();
                }
                break;
        }
    }


    public void sendSMS(String phoneNo, String msg){
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
