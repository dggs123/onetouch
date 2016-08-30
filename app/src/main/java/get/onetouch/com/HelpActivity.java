package get.onetouch.com;

import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import get.onetouch.com.Interpolator.LatLngInterpolator;
import get.onetouch.com.models.User;

/**
 * Created by Gulzar on 30-08-2016.
 */
public class HelpActivity extends BaseActivity implements OnMapReadyCallback {
    TextView mHelpWanted;
    String helpUserId;
    Double helpLat,helpLong;
    private GoogleMap mMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        Bundle b=getIntent().getExtras();
        mHelpWanted= (TextView) findViewById(R.id.helpWanted);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Typeface Roboto = Typeface.createFromAsset(getAssets(),"CaviarDreams_Bold.ttf");
        mHelpWanted.setTypeface(Roboto);

        //Get Value from Intent
        if(b!=null)
        {
            helpUserId=b.getString("helpuserID");
        }
        initialize();
        Log.d("XOXO","Help User ID"+helpUserId);


        //Temporary terminate the Service
        if(isMyServiceRunning(BackgroundLocationService.class))
            stopService(new Intent(HelpActivity.this,BackgroundLocationService.class));


        //Listner to listen to change in victims location!
        mDatabase.child("users").child(helpUserId).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User mUser=dataSnapshot.getValue(User.class);
                        helpLat= Double.valueOf(mUser.lat);
                        helpLong=Double.valueOf(mUser.lang);
                        Log.d("XOXO","Help lat lang "+helpLat+helpLong);


                        if(helpLat!=null && helpLong!=null){
                            LatLng sydney = new LatLng(helpLat, helpLong);
                            mMap.addMarker(new MarkerOptions().position(sydney).title("Victim"));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));}
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("XXXX", "getUser:onCancelled", databaseError.toException());
                    }





                });


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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


    }
}
