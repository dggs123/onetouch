package get.onetouch.com;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
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
    public String Ulat,Ulang,helpuid;
    Boolean help=false;
    public String TAG="TapButtonActivity";
    ValueEventListener postListener;

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

        postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                User mUser = dataSnapshot.getValue(User.class);
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
    public void onStart() {
        super.onStart();
        mDatabase.child("users").child(Uid).addValueEventListener(postListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (postListener != null) {
            mDatabase.child("users").child(Uid).removeEventListener(postListener);

        }
    }


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
                           if(dist<1000.0 && dist>10.00 && !ChildSnapshot.getKey().toString().equals(Uid))
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
                Toast.makeText(TapButtonActivity.this, "SEARCHING.....", Toast.LENGTH_LONG).show();
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
