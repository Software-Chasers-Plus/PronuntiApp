package it.uniba.dib.sms232419.pronuntiapp.Gioco;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import it.uniba.dib.sms232419.pronuntiapp.R;

public class EsercizioGiocoFragmentTipologia3 extends Fragment {

    private String esercizio;
    private GiocoActivity giocoActivity;
    private ConstraintLayout layout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Recupera la scheda selezionata dall'utente
        if(getArguments() != null && getArguments().getString("esercizio") != null) {
            esercizio = getArguments().getString("esercizio");
            Log.d("EsercizioGiocoFragmentTipologia3", "Scheda: " + esercizio + " caricata");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_esercizio_gioco_tipologia3, container, false);
        layout = view.findViewById(R.id.esercizio_tipologia3);

        giocoActivity = (GiocoActivity) getActivity();

        // Inizializza il layout con l'immagine di sfondo selezionata in base a  public Integer sfondoSelezionato di GiocoActivity
        switch (giocoActivity.sfondoSelezionato) {
            case 0:
                layout.setBackgroundResource(R.drawable.deserto);
                break;
            case 1:
                layout.setBackgroundResource(R.drawable.antartide);
                break;
            case 2:
                layout.setBackgroundResource(R.drawable.giungla);
                break;
        }
        return view;
    }



}
