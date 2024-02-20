package it.uniba.dib.sms232419.pronuntiapp.Gioco;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.Esercizio;
import it.uniba.dib.sms232419.pronuntiapp.model.EsercizioTipologia1;

public class EsercizioGiocoFragmentTipologia1 extends Fragment {

    private EsercizioTipologia1 esercizio;
    private int sfondoSelezionato = 0;
    private ConstraintLayout layout;
    private GiocoActivity giocoActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Recupera l'esercizio selezionato dall'utente
        if (getArguments() != null && getArguments().getParcelable("esercizio") != null) {
            esercizio = getArguments().getParcelable("esercizio");
            Log.d("EsercizioGiocoFragmentTipologia1", "Scheda: " + esercizio + " caricata");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_esercizio_gioco_tipologia1, container, false);

        layout = view.findViewById(R.id.esercizio_tipologia1);

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
