package it.uniba.dib.sms232419.pronuntiapp.ui.info;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.databinding.FragmentHomeBinding;
import it.uniba.dib.sms232419.pronuntiapp.model.Figlio;

public class InfoFiglioFragment extends Fragment {

    private Figlio figlio;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            //recupero il figlio dal bundle passato al fragment
            figlio = getArguments().getParcelable("figlio");

            Log.d("InfoFiglioFragment", "Figlio recuperato: "+figlio.getNome());
        }else{
            Log.d("InfoFiglioFragment", "Bundle non nullo");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dettaglio_figlio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView nomeFiglio = view.findViewById(R.id.nome_figlio);
        nomeFiglio.setText(figlio.getNome());

        TextView cognomeFiglio = view.findViewById(R.id.cognome_figlio);
        cognomeFiglio.setText(figlio.getCognome());

        TextView codiceFiscaleFiglio = view.findViewById(R.id.codiceFiscale_figlio);
        codiceFiscaleFiglio.setText(figlio.getCodiceFiscale());

        TextView dataNascitaFiglio = view.findViewById(R.id.data_nascita_figlio);
        dataNascitaFiglio.setText(figlio.getDataNascita().toString());

        TextView emailLogopedistaFiglio = view.findViewById(R.id.email_logopedista_figlio);
        emailLogopedistaFiglio.setText(figlio.getEmailLogopedista());
    }

}
