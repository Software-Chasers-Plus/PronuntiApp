package it.uniba.dib.sms232419.pronuntiapp.ui.info;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
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

        TextView nomeFiglio = view.findViewById(R.id.nome_figlio_dettaglio);
        nomeFiglio.setText(figlio.getNome());

        TextView cognomeFiglio = view.findViewById(R.id.cognome_figlio_dettaglio);
        cognomeFiglio.setText(figlio.getCognome());

        ImageView avatarFiglio = view.findViewById(R.id.avatar_figlio_dettaglio);
        avatarFiglio.setImageResource(figlio.getIdAvatar() + 1);

        TextView codiceFiscaleFiglio = view.findViewById(R.id.codice_fiscale_figlio_dettaglio);
        codiceFiscaleFiglio.setText(figlio.getCodiceFiscale());

        TextView dataNascitaFiglio = view.findViewById(R.id.data_nascita_figlio_dettaglio);
        dataNascitaFiglio.setText(figlio.getDataNascita().toString());

        TextView tokenFiglio = view.findViewById(R.id.token_figlio_dettaglio);
        tokenFiglio.setText(figlio.getToken());

        TextView emailLogopedistaFiglio = view.findViewById(R.id.email_logopedista_figlio_dettaglio);
        emailLogopedistaFiglio.setText(figlio.getLogopedista());
    }

}
