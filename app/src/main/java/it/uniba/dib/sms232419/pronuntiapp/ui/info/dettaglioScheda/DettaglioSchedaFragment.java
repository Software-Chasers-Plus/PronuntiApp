package it.uniba.dib.sms232419.pronuntiapp.ui.info.dettaglioScheda;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

import it.uniba.dib.sms232419.pronuntiapp.AccessoActivity;
import it.uniba.dib.sms232419.pronuntiapp.MainActivityGenitore;
import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.databinding.FragmentDettaglioSchedaBinding;
import it.uniba.dib.sms232419.pronuntiapp.databinding.FragmentHomeBinding;
import it.uniba.dib.sms232419.pronuntiapp.model.Figlio;
import it.uniba.dib.sms232419.pronuntiapp.model.Genitore;
import it.uniba.dib.sms232419.pronuntiapp.model.Scheda;
import it.uniba.dib.sms232419.pronuntiapp.ui.home.HomeViewModel;

public class DettaglioSchedaFragment extends Fragment {

    private static final String TAG = "DettaglioSchedaFragment";
    private FragmentDettaglioSchedaBinding binding;

    private Scheda scheda;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            // recupero la scheda dal bundle passato al fragment
            scheda = getArguments().getParcelable("scheda");

            Log.d(TAG, "Scheda recuperato: "+ scheda.getNome());
        } else {
            Log.d(TAG, "Bundle nullo");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentDettaglioSchedaBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setto il titolo della scheda
        TextView nomeScheda = view.findViewById(R.id.nome_scheda_dettaglio);
        nomeScheda.setText(scheda.getNome());
    }
}
