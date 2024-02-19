package it.uniba.dib.sms232419.pronuntiapp.Gioco;

import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import it.uniba.dib.sms232419.pronuntiapp.model.Scheda;

public class EsercizioGiocoFragment extends Fragment {
    private String esercizio;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Recupera la scheda selezionata dall'utente
        if(getArguments() != null && getArguments().getString("esercizio") != null) {
            esercizio = getArguments().getString("esercizio");
            Log.d("EsercizioGiocoFragment", "Scheda: " +esercizio+ " caricata");
        }
    }
}
