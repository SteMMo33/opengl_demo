/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.opengl;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;


/**
 * Domande:
 * <li> come fa a puntare al layout main.xml ??
 * In realtà quello che si vede è i titolo del layout (?).
 * La TextView non è visibile !?! Era stato impostato come non visibile nel layout !
 * Come si punta alla TV ?? Non era definito l'ID della TV
 * </li>
 *
 *
 * @todo Aggiungere supporto per GPS
 * @TODO: 12/01/18 Agganciare movimento device con spostamento mappa
 * @TODO: 12/01/18 inserire gestione overlay per interfaccia UI
 *
 */
public class OpenGLES20Activity extends Activity implements LocationListener {

    //SM ? private GLSurfaceView mGLView;
    private MyGLSurfaceView mGLView;

    private MyGpsDevice mGps;
    private boolean mGpsIsEnabled;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity
        mGLView = new MyGLSurfaceView(this);
        setContentView(mGLView);


        // Creazione supporto per GPS - da fare ..
        mGps = new MyGpsDevice();

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (lm != null) {
            lm.requestLocationUpdates( LocationManager.GPS_PROVIDER, 5, 50, this);
            mGpsIsEnabled = true;
            Log.d(TAG, "GpsUpdates enabled!");
        }
        else
            mGLView.UpdateMsg("lm is null");

    }


    @Override
    protected void onPause() {
        super.onPause();
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.
        mGLView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        mGLView.onResume();
    }

    private void UpdateMsg(String msg) {
        mGLView.UpdateMsg(msg);this.setTitle(msg);
    }


    @Override
    public void onLocationChanged(Location location) {

        Toast.makeText(
                getBaseContext(),
                "Location changed: Lat: " + location.getLatitude() + " Lng: "
                        + location.getLongitude(), Toast.LENGTH_SHORT).show();

        String longitude = "Longitude: " + location.getLongitude();
        Log.v(TAG, longitude);

        String latitude = "Latitude: " + location.getLatitude();
        Log.v(TAG, latitude);

         /*------- To get city name from coordinates -------- */
        String cityName = null;
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1);
            if (addresses.size() > 0) {
                System.out.println(addresses.get(0).getLocality());
                cityName = addresses.get(0).getLocality();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        String s = longitude + "\n" + latitude + "\n\nMy Current City is: "
                + cityName;
        Log.d(TAG, s);

        // --
        double speed = location.getSpeed(); //spedd in meter/minute
        speed = (speed*3600)/1000;      // speed in km/minute
        Log.d(TAG, "Vel: " + speed);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.d(TAG, "StatusChanged: " + s);
        UpdateMsg("StatusChanged: " + s);
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.d(TAG, "ProviderEnabled: "+s);
        UpdateMsg("ProviderEnabled: " + s);
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.d(TAG, "ProviderDisabled: "+s);
        UpdateMsg("ProviderDisabled: " + s);
    }
}