package com.teincstudios.tickit.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.teincstudios.tickit.R;
import com.teincstudios.tickit.adapters.SpotAdapter;
import com.teincstudios.tickit.adapters.TrendingSpotAdapter;
import com.teincstudios.tickit.interfaces.RecyclerItemClickListener;
import com.teincstudios.tickit.models.JointResponse;
import com.teincstudios.tickit.models.Joints;
import com.teincstudios.tickit.rest.ApiClient;
import com.teincstudios.tickit.rest.ApiInterface;
import com.teincstudios.tickit.utils.RestaurantUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.teincstudios.tickit.utils.Config.PARCELABLE_SPOT;
import static com.teincstudios.tickit.utils.Config.PERMISSION_CALLBACK_CONSTANT;
import static com.teincstudios.tickit.utils.Config.REQUEST_LOCATION;
import static com.teincstudios.tickit.utils.Config.REQUEST_PERMISSION_SETTING;
import static com.teincstudios.tickit.utils.Config.RESERVATION_HASH_ID;
import static com.teincstudios.tickit.utils.Config.RESERVATION_SP;


public class ResHomeActivity extends AppCompatActivity implements android.location.LocationListener {

    static double latitude, longitude;
    String[] permissionsRequired = new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};
    private RecyclerView popularRecyclerView, restaurantsReyclerView;
    private ArrayList<Joints> trendingJointsList, restaurantList, filteredList;
    private RestaurantUtils restaurantUtils;
    private ApiInterface apiService;
    private TrendingSpotAdapter trendingSpotAdapter;
    private SpotAdapter spotAdapter;
    private FirebaseAnalytics mFirebaseAnalytics;
    private String place;
    private String currentCity;
    private TextView locationTextView;
    private LocationManager locationManager;
    private String provider;
    private Location location;
    private SharedPreferences permissionStatus;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest locationRequest;
    private LocationSettingsRequest.Builder locationSettingsRequest;
    private PendingResult<LocationSettingsResult> pendingResult;
    private EditText searchEditText;
    private boolean filterApplied;
    private ArrayList<String> cuisineFilter, priceLevelFilter;
    private ArrayList<Boolean> cuisineFilterSelected, priceLevelFilterSelected;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reshome);

        popularRecyclerView = findViewById(R.id.trendingspot_recyclerview);
        restaurantsReyclerView = findViewById(R.id.restaurants_recyclerview);

        apiService = ApiClient.getClient().create(ApiInterface.class);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        locationTextView = findViewById(R.id.location_textview);

        // sharedPreferences
        sharedPreferences = getSharedPreferences(RESERVATION_SP, MODE_PRIVATE);
        /* Add User */
        boolean tempBool = false;
        if (sharedPreferences.getString(RESERVATION_HASH_ID, null) != null) {
            tempBool = sharedPreferences.getString(RESERVATION_HASH_ID, null).length() < 4;
        }
        if (sharedPreferences.getString(RESERVATION_HASH_ID, null) == null || tempBool) {
            Call<String> addUserCall = apiService.addUser();
            addUserCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        if (!response.body().contains("Error")) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(RESERVATION_HASH_ID, response.body());
                            editor.apply();
                        }
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }

        // Filter Lists
        final ArrayList<String> cuisineList = new ArrayList<>(), priceLevelList = new ArrayList<>();
        cuisineList.add("American");cuisineList.add("Italian");cuisineList.add("Continental");cuisineList.add("Spicy");
        priceLevelList.add("₵");priceLevelList.add("₵₵");priceLevelList.add("₵₵₵");priceLevelList.add("₵₵₵₵");

        final LinearLayoutManager layoutManager = new LinearLayoutManager(ResHomeActivity.this, LinearLayoutManager.HORIZONTAL, false);
        popularRecyclerView.setLayoutManager(layoutManager);
        popularRecyclerView.setItemAnimator(new DefaultItemAnimator());
        popularRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent restaurantIntent = new Intent(ResHomeActivity.this, RestaurantActivity.class);
                restaurantIntent.putExtra(PARCELABLE_SPOT, trendingJointsList.get(position));
                restaurantIntent.putExtra("city_name",currentCity);
                startActivity(restaurantIntent);
            }
        }));

        LinearLayoutManager layoutManagerRestaurants = new LinearLayoutManager(ResHomeActivity.this, LinearLayoutManager.VERTICAL, false);
        restaurantsReyclerView.setLayoutManager(layoutManagerRestaurants);
        restaurantsReyclerView.setItemAnimator(new DefaultItemAnimator());
        restaurantsReyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent restaurantIntent = new Intent(ResHomeActivity.this, RestaurantActivity.class);
                restaurantIntent.putExtra(PARCELABLE_SPOT, restaurantList.get(position));
                restaurantIntent.putExtra("city_name",currentCity);
                startActivity(restaurantIntent);
            }
        }));



        searchEditText = findViewById(R.id.search_edit_text);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchQuery = s.toString();
                if (!searchQuery.trim().isEmpty()) {
                    if (restaurantList != null) {
                        ArrayList<Joints> resultList = new ArrayList<>();
                        ArrayList<Joints> listToSearch;
                        if (filterApplied){
                            listToSearch = filteredList;
                        } else {
                            listToSearch = restaurantList;
                        }
                        for (int i = 0; i < listToSearch.size(); i++) {
                            if (listToSearch.get(i).getSpotName().toLowerCase().contains(searchQuery.toLowerCase())) {
                                resultList.add(listToSearch.get(i));
                            }
                        }
                        if (resultList.isEmpty()){
//                            shimmerLayout.setVisibility(View.GONE);
//                            shimmerLayout.stopShimmerAnimation();
//                            mainContentHolder.setVisibility(View.GONE);
//                            errorImageView.setImageDrawable(getResources().getDrawable(R.drawable.oh_snap));
//                            errorMessageTextView.setText("No restaurants found for your search query. Try a different one!");
//                            errorLayout.setVisibility(View.VISIBLE);
//                            errorImageView.setVisibility(View.GONE);
//                            errorTryAgainButton.setVisibility(View.GONE);
                        } else {
//                            errorLayout.setVisibility(View.GONE);
//                            mainContentHolder.setVisibility(View.VISIBLE);
                        }
                        SpotAdapter resultAdapter = new SpotAdapter(ResHomeActivity.this, resultList, restaurantUtils);
                        restaurantsReyclerView.setAdapter(resultAdapter);
//                        trendingContentHolder.setVisibility(View.GONE);
                    } else {
                        searchEditText.getText().clear();
                    }
                } else {
                    if (restaurantList != null) {
                        if (filterApplied) {
//                            errorLayout.setVisibility(View.GONE);
                            SpotAdapter spotAdapter = new SpotAdapter(ResHomeActivity.this, filteredList, restaurantUtils);
                            restaurantsReyclerView.setAdapter(spotAdapter);
//                            mainContentHolder.setVisibility(View.VISIBLE);
                        } else {
//                            errorLayout.setVisibility(View.GONE);
                            SpotAdapter spotAdapter = new SpotAdapter(ResHomeActivity.this, restaurantList, restaurantUtils);
                            restaurantsReyclerView.setAdapter(spotAdapter);
//                            mainContentHolder.setVisibility(View.VISIBLE);
//                            trendingContentHolder.setVisibility(View.VISIBLE);
                        }
                        if (restaurantList.isEmpty()){
//                            errorLayout.setVisibility(View.VISIBLE);
//                            mainContentHolder.setVisibility(View.GONE);
                        }
                    } else {
                        searchEditText.getText().clear();
                    }
                }
            }
        });


        CardView filterButton = findViewById(R.id.filter_button);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ResHomeActivity.this,"Filter",Toast.LENGTH_LONG).show();
            }
        });

        // Runtime Permissions
        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
        askRequiredPermissions();

        if (place != null) {
            locationTextView.setText(place);
        } else {
            locationTextView.setText(currentCity);
        }
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_REGIONS)
                .build();
        autocompleteFragment.setFilter(typeFilter);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i("MainActivity", "Place: " + place.getName());
                currentCity = place.getName().toString();
                locationTextView.setText(currentCity);
                locationManager.removeUpdates(ResHomeActivity.this);
                // Filter and Search Reset
                searchEditText.getText().clear();
                cuisineFilter = new ArrayList<>();
                priceLevelFilter = new ArrayList<>();
                cuisineFilterSelected = new ArrayList<>();
                priceLevelFilterSelected = new ArrayList<>();
                for (int i=0;i<cuisineList.size();i++){
                    cuisineFilterSelected.add(false);
                }
                for (int i=0;i<priceLevelList.size();i++){
                    priceLevelFilterSelected.add(false);
                }
                // Logging event
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "selected_city");
                bundle.putString(FirebaseAnalytics.Param.VALUE, currentCity);
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                // Assigning latlong
                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;
                fetchData(String.valueOf(0.555), String.valueOf(01.045), "1");
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("MainActivity", "An error occurred: " + status);
            }
        });


        fetchData(String.valueOf(0.555), String.valueOf(01.045), "1");


    }

    public String geodecodeCity(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(
                    latitude, longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                place = address.getLocality();
                currentCity = place;
                if (locationTextView != null) {
                    locationTextView.setText(currentCity);
                    // Logging Event
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "selected_city");
                    bundle.putString(FirebaseAnalytics.Param.VALUE, currentCity);
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                    // assigning latlong
                    this.latitude = latitude;
                    this.longitude = longitude;
                    fetchData(String.valueOf(0.55), String.valueOf(0.222),"1");
                }
                locationManager.removeUpdates(this);
            }
        } catch (IOException e) {
            Log.e("MainActivity", "Unable connect to Geocoder", e);
            //geodecodeCity(latitude,longitude);
        }
        return null;
    }


    private void fetchData(String lat, String lng, String type) {
        //mainContentHolder.setVisibility(View.GONE);
        //shimmerLayout.startShimmerAnimation();
        //shimmerLayout.setVisibility(View.VISIBLE);
        //errorLayout.setVisibility(View.GONE);
        Call<JointResponse> call = apiService.getNearbySpots(lat, lng, type);
        call.enqueue(new Callback<JointResponse>() {
            @Override
            public void onResponse(Call<JointResponse> call, Response<JointResponse> response) {
                if (response.isSuccessful()) {
                    restaurantList = new ArrayList<>();
                    trendingJointsList = new ArrayList<>();
                    restaurantList = response.body().getSpotList();
                    String dateToFormat = response.body().getTime();
                    restaurantUtils = new RestaurantUtils(dateToFormat);
                    // Trending Spots
                    if (restaurantList != null) {
                        for (int i = 0; i < restaurantList.size(); i++) {
                            if (restaurantList.get(i).isTrending()) {
                                trendingJointsList.add(restaurantList.get(i));
                            }
                        }
                        trendingSpotAdapter = new TrendingSpotAdapter(trendingJointsList, restaurantUtils);
                        spotAdapter = new SpotAdapter(ResHomeActivity.this, restaurantList, restaurantUtils);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                popularRecyclerView.setAdapter(trendingSpotAdapter);
                                restaurantsReyclerView.setAdapter(spotAdapter);
                                //shimmerLayout.setVisibility(View.GONE);
                                //shimmerLayout.stopShimmerAnimation();
                                //errorLayout.setVisibility(View.GONE);
                                //mainContentHolder.setVisibility(View.VISIBLE);
                            }
                        });

                        // Error Validation
                        if (restaurantList.isEmpty()) {
                            //shimmerLayout.setVisibility(View.GONE);
                            //shimmerLayout.stopShimmerAnimation();
                            //mainContentHolder.setVisibility(View.GONE);
                            //errorImageView.setImageDrawable(getResources().getDrawable(R.drawable.oh_snap));
                            //errorMessageTextView.setText("No restaurants found in this location, check back soon.");
                            //errorLayout.setVisibility(View.VISIBLE);
                            //errorImageView.setVisibility(View.VISIBLE);
                            //errorTryAgainButton.setVisibility(View.GONE);
                        }

                    } else {
//                        shimmerLayout.setVisibility(View.GONE);
//                        shimmerLayout.stopShimmerAnimation();
//                        mainContentHolder.setVisibility(View.GONE);
//                        errorImageView.setImageDrawable(getResources().getDrawable(R.drawable.oh_snap));
//                        errorMessageTextView.setText("No restaurants found in this location, check back soon.");
//                        errorLayout.setVisibility(View.VISIBLE);
//                        errorImageView.setVisibility(View.VISIBLE);
//                        errorTryAgainButton.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void onFailure(Call<JointResponse> call, Throwable t) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        shimmerLayout.setVisibility(View.GONE);
//                        shimmerLayout.stopShimmerAnimation();
//                        mainContentHolder.setVisibility(View.GONE);
//                        errorImageView.setImageDrawable(getResources().getDrawable(R.drawable.offline_error));
//                        errorMessageTextView.setText("You appear to be offline, please reconnect and try again.");
//                        errorLayout.setVisibility(View.VISIBLE);
//                        errorImageView.setVisibility(View.VISIBLE);
//                        errorTryAgainButton.setVisibility(View.VISIBLE);
                    }
                });
                Log.e("ResHomeActivity", "Error Fetchin Spots from DO:" + t);
            }
        });
    }

    public void getLocation() {
        if (!(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            String provider = locationManager.getBestProvider(new Criteria(), false);
            location = locationManager.getLastKnownLocation(provider);
            Log.i("ResHomeActivity", "isLocationNullingetLocation:" + location);
            if (location != null && currentCity == null) {
                geodecodeCity(location.getLatitude(), location.getLongitude());
            } else {
                if (location != null && currentCity.matches("Pick a Location")) {
                    geodecodeCity(location.getLatitude(), location.getLongitude());
                }
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        if (currentCity == null) {
            geodecodeCity(latitude, longitude);
        } else {
            if (currentCity.matches("Pick a Location")) {
                geodecodeCity(latitude, longitude);
            }
        }

        Log.i("MainActivity", "locationLastKnown:" + location);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void askRequiredPermissions() {
        if (ActivityCompat.checkSelfPermission(this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[1])) {
                //Request Permissions
                ActivityCompat.requestPermissions(ResHomeActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            }

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0], true);
            editor.commit();
        } else {
            //You already have the permission, just go ahead.
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(this)) {
                enableLocation();
            }
            getLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            boolean allgranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if (allgranted) {
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(this)) {
                    enableLocation();
                }
                getLocation();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[1])) {
                // Request Permissions
                ActivityCompat.requestPermissions(ResHomeActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(this)) {
                    enableLocation();
                }
                getLocation();
            }
        }

        if (requestCode == REQUEST_LOCATION) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    // All required changes were successfully made
                    getLocation();
                    break;
                case Activity.RESULT_CANCELED:
                    // The user was asked to change settings, but chose not to
                    Toast.makeText(this, "Please turn on Location Services to detect your current location", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    }

    /* Enabling Location */

    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }

    public void enableLocation() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                    }
                })
                .build();
        mGoogleApiClient.connect();
        mLocationSetting();
    }

    public void mLocationSetting() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1 * 1000);
        locationRequest.setFastestInterval(1 * 1000);

        locationSettingsRequest = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        mResult();

    }

    public void mResult() {
        pendingResult = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, locationSettingsRequest.build());
        pendingResult.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                Status status = locationSettingsResult.getStatus();


                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                        try {

                            status.startResolutionForResult(ResHomeActivity.this, REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {

                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.


                        break;
                }
            }

        });
    }

}
