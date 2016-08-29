package get.onetouch.com;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.security.PublicKey;

import get.onetouch.com.models.User;

/**
 * Created by Gulzar on 29-08-2016.
 */
public class TapButtonActivity extends BaseActivity implements View.OnClickListener{
    Toolbar toolbar;
    TextView mAppTitle;
    Boolean iAmIn=true;
    public String Uid;
    public String Ulat,Ulang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tapbutton);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        mAppTitle = (TextView) findViewById(R.id.AppT);
        Typeface Roboto = Typeface.createFromAsset(getAssets(), "CaviarDreams.ttf");
        mAppTitle.setTypeface(Roboto);
        mAppTitle.setTextColor(Color.WHITE);
        initialize();
        initializeAuthRef();
       findViewById(R.id.fab).setOnClickListener(this);
        Uid=getUserId();
        if(iAmIn==true)
            RunInBackground();




    }

    private void RunInBackground() {
        Toast.makeText(TapButtonActivity.this, "Service started", Toast.LENGTH_SHORT).show();
        //BackgroudLocationService is started
        startService(new Intent(getBaseContext(), BackgroundLocationService.class));
        //Toast to display the start service
        Toast.makeText(getBaseContext(), "Service started", Toast.LENGTH_SHORT).show();

    }
    private double calculateDistance(double fromLong, double fromLat,
                                     double toLong, double toLat) {
        double d2r = Math.PI / 180;
        double dLong = (toLong - fromLong) * d2r;
        double dLat = (toLat - fromLat) * d2r;
        double a = Math.pow(Math.sin(dLat / 2.0), 2) + Math.cos(fromLat * d2r)
                * Math.cos(toLat * d2r) * Math.pow(Math.sin(dLong / 2.0), 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = 6367000 * c;
        return Math.round(d);
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
                           if(calculateDistance(Double.parseDouble(Ulang),Double.parseDouble(Ulat),Double.parseDouble(mUser.lang),Double.parseDouble(mUser.lat))<10 && !ChildSnapshot.getKey().toString().equals(Uid))
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

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.fab:
                findUserLatLong();
                findAndSendNotificationToNearUser(Uid);
                break;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
