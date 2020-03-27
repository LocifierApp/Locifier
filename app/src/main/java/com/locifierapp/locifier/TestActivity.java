package com.locifierapp.locifier;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.locifierapp.locifier.notification.ArrivalNotification;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        configureBackButton();
        configureOpenMapsButton();
        configureNotificationAlarmButton();
    }

    private void configureBackButton(){

        Button backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v){
                finish();
            }
        });
    }

    private void configureOpenMapsButton(){
        Button openMapsActivityButton = (Button) findViewById(R.id.open_maps_activity_button);

        openMapsActivityButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(TestActivity.this, MapsActivity.class));
            }
        });
    }

    private void configureNotificationAlarmButton(){

        Button notificationAlarmButton = (Button) findViewById(R.id.notification_alarm_button);
        notificationAlarmButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v){
                Intent intent = new Intent(TestActivity.this, TestActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(TestActivity.this, 0, intent, 0);

                new ArrivalNotification(pendingIntent, TestActivity.this);


                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    AudioAttributes aa = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build();
                    ringtone.setAudioAttributes(aa);
                } else {
                    ringtone.setStreamType(AudioManager.STREAM_ALARM);
                }
                ringtone.play();


            }
        });
    }
}
