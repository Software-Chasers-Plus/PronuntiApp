package it.uniba.dib.sms232419.pronuntiapp.ui.esercizi;

import android.os.Bundle;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import it.uniba.dib.sms232419.pronuntiapp.R;

public class CreazioneEsercizi extends AppCompatActivity {

    private static final int RADIO_BUTTON_IMMGAGINI_ID = R.id.radioButtonImmagini;
    // Questo metodo viene chiamato per inflare il layout del fragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_creazione_esercizio);

        // Inizializza i componenti UI e gestisci gli eventi
        RadioGroup radioGroup = findViewById(R.id.radioGroup);



        // Puoi aggiungere qui la logica per inizializzare i componenti UI o gestire gli eventi
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Fragment fragment;
                if (checkedId == R.id.radioButtonImmagini) {
                    fragment = new EsercizioDenominazioneImmagine(); // Crea un'istanza del primo fragment
                } else if (checkedId == R.id.radioButtonParole) {
                    fragment = new EsercizioRipetizioneParole(); // Crea un'istanza del secondo fragment
                } else if(checkedId == R.id.radioButtonCoppie){
                    fragment = new EsercizioRiconoscimentoCoppie(); // Crea un'istanza del terzo fragment
                }
                else {
                    return;
                }

                // Sostituisci il fragment corrente con il nuovo fragment

                // Ottieni il FragmentManager
                FragmentManager fragmentManager = getSupportFragmentManager();
                // Inizia una transazione
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                // Aggiungi il fragment al FrameLayout con l'ID "fragment_container"
                transaction.replace(R.id.fragment_container_view, fragment);

                // Esegui la transazione
                transaction.commit();

            }});
    }
}
