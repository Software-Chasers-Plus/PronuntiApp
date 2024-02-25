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

import it.uniba.dib.sms232419.pronuntiapp.databinding.ActivityMainLogopedistaBinding;
import it.uniba.dib.sms232419.pronuntiapp.model.Figlio;
import it.uniba.dib.sms232419.pronuntiapp.model.Genitore;
import it.uniba.dib.sms232419.pronuntiapp.model.Prenotazione;
import it.uniba.dib.sms232419.pronuntiapp.ui.esercizi.CreazioneEsercizi;
import it.uniba.dib.sms232419.pronuntiapp.ui.esercizi.EserciziFragment;

public class MainActivityLogopedista extends AppCompatActivity{


    // binding: crea la classe e collega in automatico al file layout per facilitare operazioni (non si usa il findViewById)
    private ActivityMainLogopedistaBinding binding;

    public List<Figlio> figli ;
    public List<Prenotazione> prenotazioni;
    public List<Genitore> genitori;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //recupero i pazienti passati dall'activity precedente tramite il bundle
        final Intent src = getIntent();
        if (src != null) {
            if(src.getExtras() != null){
                Bundle bundle = src.getExtras();
                figli = bundle.getParcelableArrayList("figli");
                prenotazioni= bundle.getParcelableArrayList("prenotazioni");
                Log.d("MainActivityLogopedista", "onCreate: " + figli.size());
            }else{
                Log.d("MainActivityLogopedista", "src.getExtras() è null");
            }
        }else{
            Log.d("MainActivityLogopededista", "src è null");
        }


        binding = ActivityMainLogopedistaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passa ogni ID di menu come un insieme di ID perché ciascuno
        // menu dovrebbero essere considerati destinazioni di primo livello.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home_logopedista,
                R.id.navigation_dashboard_logopedista,
                R.id.navigation_notifications_logopedista).build();



        // Configura NavController per la navigazione
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main_logopedista);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        // Connetti NavController con BottomNavigationView
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    //metodo che permette di tronare al fragment precedente
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main_logopedista);
        return navController.navigateUp();
    }
}