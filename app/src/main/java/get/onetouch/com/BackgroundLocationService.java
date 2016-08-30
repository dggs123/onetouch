package get.onetouch.com;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import get.onetouch.com.models.User;

/**
 * Created by Gulzar on 29-08-2016.
 */
public class BackgroundLocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    IBinder mBinder = new LocalBinder();

    private GoogleApiClient mGoogleApiClient;
    private String TAG="gulxxx";

    private LocationRequest mLocationRequest;
    public FirebaseAuth mAuth;
    FirebaseUser user;
    private String mUID;

    //Getting Firebase Reference intial i.e https://employee-tracker123.firebaseio.com/EmployeeID
    DatabaseReference mRef;
    DatabaseReference mDatabase;
    //Getting FireBase Reference i.e https://employee-tracker123.firebaseio.com/EmployeeID/<User Id>
    DatabaseReference UserId;
    User mUser;
    int flag=1;


    // Flag that indicates if a request is underway.
    private boolean mInProgress;

    private Boolean servicesAvailable = false;

    public class LocalBinder extends Binder {
        public BackgroundLocationService getServerInstance() {
            return BackgroundLocationService.this;
        }
    }




    @Override
    public void onCreate() {
        super.onCreate();

        buildGoogleApiClient();
        user=FirebaseAuth.getInstance().getCurrentUser();
        mUID= user.getUid();


        Log.i(TAG,"UID"+mUID);


        intializefirebase();

        Log.i(TAG, "onCreate");

        mInProgress = false;

        servicesAvailable = servicesConnected();


        //Help Listener i.e If the other user ask for help
        mDatabase.child("users").child(mUID).addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User mUser=dataSnapshot.getValue(User.class);
                        String userHelp=mUser.help.toString();
                        Log.d("XOXO","User Help VALUE :"+userHelp);
                        if(userHelp.equals("true"))
                        {
                            Intent intent = new Intent(BackgroundLocationService.this, HelpActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
                            Bundle b=new Bundle();
                            b.putString("helpuserID",mUser.helpUserId);
                            intent.putExtras(b);

                            startActivity(intent);
                        }

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("XXXX", "getUser:onCancelled", databaseError.toException());
                    }
                });

    }


    private void intializefirebase() {

        mRef = FirebaseDatabase.getInstance().getReference();
        mDatabase=mRef;


    }


    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        this.mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {

            return true;
        } else {

            return false;
        }
    }

    public int onStartCommand (Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "onStart");
        Foreground();
        if(!servicesAvailable || mGoogleApiClient.isConnected() || mInProgress)
            return START_STICKY;
        buildGoogleApiClient();
        if(!mGoogleApiClient.isConnected() || !mGoogleApiClient.isConnecting() && !mInProgress)
        {
            mInProgress = true;
            mGoogleApiClient.connect();
        }

        return START_STICKY;
    }

    private void Foreground() {


        //Pending intent responds on click on notification takes back to Main Activity
        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        Notification notification = new Notification.Builder(this)
                .setContentTitle("OneTouch")
                .setContentText("Self help is the best help")
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                .setContentIntent(resultPendingIntent)
                .build();

        startForeground(133, notification);
    }


    // Define the callback method that receives location updates
    @Override
    public void onLocationChanged(Location location) {
        // Report to the UI that the location was updated
        String msg = Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        updateFirebase(location.getLatitude(),location.getLongitude());
        Log.d("debug", msg);
    }

    private void updateFirebase(Double lat,Double lang) {

        //Getting current date and time
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());
        Log.d("XOXO","Lat"+lat+"Lang"+lang);

        if(flag==1) {
            mUser = new User(user.getDisplayName(), user.getEmail(), String.valueOf(lat), String.valueOf(lang), currentDateandTime,1);
            Map<String, Object> userValues = mUser.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/users/" + mUID, userValues);
            mRef.updateChildren(childUpdates);
            flag=0;
        }
        else
        {
            mRef.child("users").child(mUID).child("flag").setValue(0);
            mRef.child("users").child(mUID).child("lat").setValue(String.valueOf(lat));
            mRef.child("users").child(mUID).child("lang").setValue(String.valueOf(lang));
            mRef.child("users").child(mUID).child("time").setValue(currentDateandTime);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    @Override
    public void onDestroy(){
        mInProgress = false;
        // Disconnecting the client invalidates it.
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */

    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onConnected(Bundle bundle) {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5000); // Update location every 10 econd


        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }
    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        mInProgress = false;
        Log.i(TAG, "GoogleApiClient connection has failed");
    }
    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApiClient connection has been suspend");
    }


}