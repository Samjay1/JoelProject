package com.example.transportdisplay;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;

public class MapsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {
    //4444444444444444444444444444444444444
    DrawerLayout drawer;
    ActionBarDrawerToggle mytoggel;
    AlertDialog.Builder alertDialog;
    String MyPreference = "MyPrefs";
    String heademail;
    private final int RESULT_LOAD_IMAGE = 1;
    ImageView navImage;
    int permission;
    private FusedLocationProviderClient fusedLocationClient;
    Location mylocation;

    LocationListener locationListener;
    LocationManager locationManager;
    LatLng latLng;
    //44444444444444444444444444444444

    private static final String TAG = MapsActivity.class.getSimpleName();
    private HashMap<String, Marker> mMarkers = new HashMap<>();
    private HashMap<String, Marker> mMarker_user = new HashMap<>();
    public GoogleMap mMap, mMapp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //44444444444444444444444
        drawer = (DrawerLayout) findViewById(R.id.drawer);
        mytoggel = new ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close);
        drawer.addDrawerListener(mytoggel);
        mytoggel.syncState();

        SharedPreferences sharedPreferences = getSharedPreferences(MyPreference, Context.MODE_PRIVATE);
        heademail = sharedPreferences.getString("email", "example@yahoo.com");

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //44444444444444444444444444
        permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        //44444444444444444444444444
        /// swiping ability here
//        final SwipeRefreshLayout swipe = (SwipeRefreshLayout) findViewById(R.id.swipe);
//        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        swipe.setRefreshing(false);
//                    }
//                }, 4000);
//            }
//        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView nav = (NavigationView) findViewById(R.id.nav_view);
        nav.setNavigationItemSelectedListener(this);

        View headerView = nav.getHeaderView(0);
        TextView navEmail = (TextView) headerView.findViewById(R.id.headerEmail);
        navEmail.setText(heademail);
        navImage = headerView.findViewById(R.id.headerImage);
        navImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imagepath = data.getData();
            navImage.setImageURI(imagepath);

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Authenticate with Firebase when the Google map is loaded
        mMap = googleMap;



        subscribeToUpdates();

        mMap.setMaxZoomPreference(15);
//        mMap.setMyLocationEnabled(true);
//4444444444444444444444444444444444444444444444444444444

//        if (permission == PackageManager.PERMISSION_GRANTED) {
////            getDeviceCurrentLocation();
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//                return;
//            }
//
//            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
//                @Override
//                public void onSuccess(Location location) {
//                        if(location != null){
//                            mylocation = location;
//                            googleMap.addMarker(new MarkerOptions()
//                                    .position(new LatLng(5.6163690, -0.1878269))
//                                    .title("First Pit Stop")
//                                    .icon(BitmapDescriptorFactory
//                                            .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
//
//                        }
//                }
//            });
//
//            mMapp.setMyLocationEnabled(true);
//
//        }


    }


    private void subscribeToUpdates() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_path));
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                setMarker(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                setMarker(dataSnapshot);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void setMarker(DataSnapshot dataSnapshot) {
        // When a location update is received, put or update
        // its value in mMarkers, which contains all the markers
        // for locations received, so that we can build the
        // boundaries required to show them all on the map at once
        String key = dataSnapshot.getKey();
        HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();
        double lat = Double.parseDouble(value.get("latitude").toString());
        double lng = Double.parseDouble(value.get("longitude").toString());
        LatLng location = new LatLng(lat, lng);
        if (!mMarkers.containsKey(key)) {
            //44444444444444444444444
            // for setting a new marker image -> which is a bus image here
            int height = 50;
            int width = 50;
            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.one);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

            mMarkers.put(key, mMap.addMarker(new MarkerOptions().title("Bus").position(location)
                    .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))));
        } else {
            mMarkers.get(key).setPosition(location);
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : mMarkers.values()) {
            builder.include(marker.getPosition());
        }
        //latlng() viewport size here
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 300));
    }


    // MY CODE HERE


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mytoggel.onOptionsItemSelected(item))
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

       if(id == R.id.headerEmail){

       }
        if(id== R.id.help){
            Toast.makeText(this,"Help works",Toast.LENGTH_SHORT).show();

        }
        else if(id== R.id.aboutus){
            Toast.makeText(this,"Contact Us works",Toast.LENGTH_SHORT).show();

        }

        else if(id== R.id.logout){

           alertDialog = new AlertDialog.Builder(this);
           alertDialog.setMessage("Do you want to Logout?");
           alertDialog.setTitle("Confirm Logout");
           alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
               }
           });
           alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
               @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
               @Override
               public void onClick(DialogInterface dialog, int which) {
                   Toast.makeText(getApplicationContext(),"Logging out...",Toast.LENGTH_SHORT).show();
                   startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                   MapsActivity.this.finishAffinity();
                   dialog.cancel();
               }
           });
           alertDialog.show();
        }

        return true;
    }



}