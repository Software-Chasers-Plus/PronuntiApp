package it.uniba.dib.sms232419.pronuntiapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AccessoActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accesso_app);
        auth = FirebaseAuth.getInstance();

        if (savedInstanceState == null) {
            //inserisco il fragment per il login
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.login_signup_fragment, LoginFragment.class, null)
                    .commit();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //verifico se l'utente è già loggato
        if (auth.getCurrentUser() != null) {
            //faccio partire l'activity principale
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}
