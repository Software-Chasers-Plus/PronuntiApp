package it.uniba.dib.sms232419.pronuntiapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.List;

import it.uniba.dib.sms232419.pronuntiapp.databinding.ActivityMainBinding;
import it.uniba.dib.sms232419.pronuntiapp.model.Figlio;
import it.uniba.dib.sms232419.pronuntiapp.model.Prenotazione;

public class MainActivityGenitore extends AppCompatActivity {

    private ActivityMainBinding binding;

    public List<Figlio> figli ;

    public List<Prenotazione> prenotazioni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //recupero i figli passati dall'activity precedente tramite il bundle
        final Intent src = getIntent();
        if (src != null) {
            if(src.getExtras() != null){
                Bundle bundle = src.getExtras();
                figli = bundle.getParcelableArrayList("figli");
                Log.d("MainActivity", "nome: " + figli.get(0).getDataNascita());
                prenotazioni = bundle.getParcelableArrayList("prenotazioni");
            }else{
                Log.d("MainActivity", "src.getExtras() è null");
            }
        }else{
            Log.d("MainActivity", "src è null");
        }


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_prenotazioni, R.id.navigation_notifications)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    //metodo che permette di tronare al fragment precedente
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        return navController.navigateUp();
    }

}