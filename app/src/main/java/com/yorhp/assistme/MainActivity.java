package com.yorhp.assistme;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @author yorhp
 */
public class MainActivity extends AppCompatActivity {

    Switch swAntForest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swAntForest=findViewById(R.id.swAntForest);
        //收能量
        swAntForest.setOnClickListener(v->{
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        });


    }
}
