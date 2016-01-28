/**********************************************************************
 * Title: Solar Compass
 * Description: Android app for finding directions using the sun
 * Author: Agnibho Mondal
 * Website: http://code.agnibho.com/solarcompass
 **********************************************************************
 Copyright (c) 2016 Agnibho Mondal
 All rights reserved
 **********************************************************************
 This file is part of Solar Compass.

 Solar Compass is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Solar Compass is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Solar Compass.  If not, see <http://www.gnu.org/licenses/>.
 **********************************************************************/

package com.agnibho.android.solarcompass;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends Activity {

    private LocationData locationData=LocationData.getInstance();
    private float[] center=new float[2];
    private float currCompass=0;

    private TextView displayLoc;
    private ImageButton locBtn;
    private ImageButton helpBtn;
    private ImageView dial;
    private ImageView compass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)<6 || Calendar.getInstance().get(Calendar.HOUR_OF_DAY)>18){
            nightAlert();
        }

        displayLoc=(TextView)findViewById(R.id.textView4);

        gpsLocation();
        if(locationData.isAvailable()) {
            displayLocation();
        }
        else{
            startActivity(new Intent(MainActivity.this, LocationActivity.class));
        }

        /**
         * LocationData Button
         */
        locBtn = (ImageButton)findViewById(R.id.imageButton);
        locBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LocationActivity.class));
            }
        });

        /**
         * Help Button
         */
        helpBtn = (ImageButton)findViewById(R.id.imageButton2);
        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HelpActivity.class));
            }
        });

        /**
         * Dialer
         */
        dial=(ImageView)findViewById(R.id.imageView);
        dial.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout(){
                center[0]=dial.getPivotX();
                center[1]=dial.getPivotY();
                setDirection();
                if (Build.VERSION.SDK_INT>=16){
                    dial.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                else {
                    dial.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
        dial.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ClipData data = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadow = new View.DragShadowBuilder();
                    dial.startDrag(data, shadow, dial, 0);
                    return true;
                } else {
                    return false;
                }
            }
        });
        dial.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                if (event.getAction() == DragEvent.ACTION_DRAG_LOCATION) {
                    dial.setRotation((float) getAngle(event.getX(), event.getY()));
                }
                else if(event.getAction()==DragEvent.ACTION_DROP){
                    setDirection();
                }
                return true;
            }
        });

        /**
         * Compass
         */
        compass=(ImageView)findViewById(R.id.imageView2);
    }

    @Override
    protected void onResume(){
        super.onResume();
        displayLocation();
    }

    private double getAngle(float x, float y) {
        double angle;
        x=x-center[0];
        y=center[1]-y;
        angle=Math.toDegrees(Math.atan2(x,y));
        angle=dial.getRotation()+angle;
        return angle;
    }

    private void gpsLocation(){
        LocationManager locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        try {
            Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(loc==null){
                loc=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            locationData.setCoordinate(loc.getLatitude(), loc.getLongitude());
        }
        catch (SecurityException e){
            Toast.makeText(getApplicationContext(), "GPS Permission denied.", Toast.LENGTH_LONG).show();
        }
        catch (Exception e){
            Toast.makeText(getApplicationContext(), "GPS location unavailable.", Toast.LENGTH_LONG).show();
        }
    }

    private void displayLocation(){
        String s="";
        if(locationData.getLatitude()>=0){
            s=s+String.format("%.2f", Math.abs(locationData.getLatitude()))+"\u00B0 N \n";
        }
        else{
            s=s+String.format("%.2f", Math.abs(locationData.getLatitude()))+"\u00B0 S \n";
        }
        if(locationData.getLongitude()>=0){
            s=s+String.format("%.2f", Math.abs(locationData.getLongitude()))+"\u00B0 E";
        }
        else{
            s=s+String.format("%.2f", Math.abs(locationData.getLongitude()))+"\u00B0 W";
        }
        displayLoc.setText(s);
    }

    private void setDirection(){
        float direction=(float)CompassLogic.getAngle();
        direction=direction+(dial.getRotation()%360);
        if(Math.abs(direction-currCompass)>180){
            direction=(direction-360)%360;
        }
        RotateAnimation anim = new RotateAnimation(currCompass, direction, compass.getPivotX(), compass.getPivotY());
        anim.setInterpolator(new LinearInterpolator());
        anim.setDuration(500);
        anim.setFillAfter(true);
        compass.startAnimation(anim);
        currCompass=direction;
    }

    private void nightAlert(){
        new AlertDialog.Builder(this)
                .setTitle("Warning!")
                .setMessage("Solar Compass only works during the day.\n" +
                        "(6 AM to 6 PM)\n" +
                        "Because it depends on the location of the sun in the sky to find directions.")
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
