package com.epicodus.myrestaurants.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.epicodus.myrestaurants.R;
import com.epicodus.myrestaurants.adapters.RestaurantListAdapter;
import com.epicodus.myrestaurants.models.Restaurant;
import com.epicodus.myrestaurants.services.YelpService;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RestaurantsActivity extends AppCompatActivity {
    public static final String TAG = RestaurantsActivity.class.getSimpleName();
//    @Bind(R.id.recyclerView) RecyclerView mRecyclerView;
//    private RestaurantListAdapter mAdapter;
    @Bind(R.id.locationTextView) TextView mLocationTextView;
    @Bind(R.id.listView) ListView mListView;
    public ArrayList<Restaurant> restaurants = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants);
        ButterKnife.bind(this);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, restaurants);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String restaurant = ((TextView)view).getText().toString();
                Toast.makeText(RestaurantsActivity.this, restaurant, Toast.LENGTH_LONG).show();
            }
        });


        Intent intent = getIntent();
        String location = intent.getStringExtra("location");

        mLocationTextView.setText("Here are all the restaurants near: " + location);
        getRestaurants(location);
    }

    private void getRestaurants(String location) {
        final YelpService yelpService = new YelpService();

        yelpService.findRestaurants(location, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) {
                restaurants = yelpService.processResults(response);

                RestaurantsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        String[] restaurantNames = new String[restaurants.size()];
                        for (int i = 0; i < restaurantNames.length; i++) {
                            restaurantNames[i] = restaurants.get(i).getName();

                            ArrayAdapter adapter = new ArrayAdapter(RestaurantsActivity.this,
                                    android.R.layout.simple_list_item_1, restaurantNames);
                            mListView.setAdapter(adapter);

                            for (Restaurant restaurant : restaurants) {
                                Log.d(TAG, "Name: " + restaurant.getName());
                                Log.d(TAG, "Phone: " + restaurant.getPhone());
                                Log.d(TAG, "Website: " + restaurant.getWebsite());
                                Log.d(TAG, "Image url: " + restaurant.getImageUrl());
                                Log.d(TAG, "Rating: " + Double.toString(restaurant.getRating()));
                                Log.d(TAG, "Address: " + android.text.TextUtils.join(", ", restaurant.getAddress()));
                                Log.d(TAG, "Categories: " + restaurant.getCategories().toString());
                            }

//                        mAdapter = new RestaurantListAdapter(getApplicationContext(), restaurants);
//                        mRecyclerView.setAdapter(mAdapter);
//                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager
//                                (RestaurantsActivity.this);
//                        mRecyclerView.setLayoutManager(layoutManager);
//                        mRecyclerView.setHasFixedSize(true); //h & w should remain the same

                        }
                    }
                });

//                try {
//                    String jsonData = response.body().string();
//                    if (response.isSuccessful()) {
//                        Log.v(TAG, jsonData);
//                        restaurants = yelpService.processResults(response);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        });
    }
}
