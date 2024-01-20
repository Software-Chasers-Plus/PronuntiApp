package it.uniba.dib.sms232419.pronuntiapp.ui.info;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.Figlio;


public class InfoPazienteFragment extends Fragment {

    //in questo caso rappresenta il paziente e non il figlio
    private Figlio figlio;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            //recupero il figlio dal bundle passato al fragment
            figlio = getArguments().getParcelable("figlio");

            Log.d("InfoPazienteFragment", "Paziente recuperato: "+figlio.getNome());
        }else{
            Log.d("InfoPazienteFragment", "Bundle non nullo");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dettagli_paziente, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView nomeFiglio = view.findViewById(R.id.nome_paziente);
        nomeFiglio.setText(figlio.getNome());

        TextView cognomeFiglio = view.findViewById(R.id.cognome_paziente);
        cognomeFiglio.setText(figlio.getCognome());

        TextView codiceFiscaleFiglio = view.findViewById(R.id.codiceFiscale_paziente);
        codiceFiscaleFiglio.setText(figlio.getCodiceFiscale());

        TextView dataNascitaFiglio = view.findViewById(R.id.data_nascita_paziente);
        dataNascitaFiglio.setText(figlio.getDataNascita().toString());

        TextView emailLogopedistaFiglio = view.findViewById(R.id.genitore);
        emailLogopedistaFiglio.setText(figlio.getEmailGenitore());
    }
}