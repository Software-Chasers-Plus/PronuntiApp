package it.uniba.dib.sms232419.pronuntiapp.ui.info;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.Figlio;


public class InfoPazienteFragment extends Fragment {

    //in questo caso rappresenta il paziente e non il figlio
    private Figlio paziente;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            //recupero il paziente dal bundle passato al fragment
            paziente = getArguments().getParcelable("figlio");

            Log.d("InfoPazienteFragment", "Paziente recuperato: "+paziente.getNome());
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

        TextView nomepaziente = view.findViewById(R.id.nome_paziente);
        nomepaziente.setText(paziente.getNome());

        TextView cognomepaziente = view.findViewById(R.id.cognome_paziente);
        cognomepaziente.setText(paziente.getCognome());

        TextView codiceFiscalepaziente = view.findViewById(R.id.codiceFiscale_paziente);
        codiceFiscalepaziente.setText(paziente.getCodiceFiscale());

        TextView dataNascitapaziente = view.findViewById(R.id.data_nascita_paziente);
        dataNascitapaziente.setText(paziente.getDataNascita().toString());

        TextView emailLogopedistapaziente = view.findViewById(R.id.genitore);
        emailLogopedistapaziente.setText(paziente.getEmailGenitore());

        ExtendedFloatingActionButton creaScheda = view.findViewById(R.id.extended_fab_crea_scheda);
        creaScheda.setOnClickListener(v -> {

            // salvo i pazienti ne bundle per passarli al fragment successivo
            Bundle bundle = new Bundle();
            bundle.putParcelable("paziente", paziente);
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main_logopedista);
            navController.navigate(R.id.navigation_creazione_scheda, bundle);
        });
    }
}