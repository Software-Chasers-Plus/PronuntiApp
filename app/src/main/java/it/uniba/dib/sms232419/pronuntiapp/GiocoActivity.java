package it.uniba.dib.sms232419.pronuntiapp;

import static android.app.PendingIntent.getActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import it.uniba.dib.sms232419.pronuntiapp.R;

public class GiocoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gioco);

        ImageView backButton = findViewById(R.id.exit_button_start_game);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
