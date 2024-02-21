package it.uniba.dib.sms232419.pronuntiapp.Gioco;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.Figlio;

public class ClassificaGiocoFragment extends Fragment implements ClickClassificaGiocoListener{

    private static final String TAG = "ClassificaGiocoFragment";
    private ConstraintLayout layout;
    private GiocoActivity giocoActivity;
    private RecyclerView recyclerView;

    ArrayList<Figlio> bambiniList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classifica_gioco, container, false);


        layout = view.findViewById(R.id.layout_classifica_gioco);

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.classificaGiocoRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        //Creazione riferimento al database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Log.d(TAG, "Riferimento al database creato");

        db.collection("figli")
                .orderBy("punteggioGioco")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        bambiniList = new ArrayList<>();

                        for(QueryDocumentSnapshot document : task.getResult()) {
                            bambiniList.add(createFiglio(document));
                        }

                        Log.d(TAG, "bambiniList: " + bambiniList);

                        if(!bambiniList.isEmpty()){
                            recyclerView.setAdapter(new ClassificaGiocoAdapter(requireContext(), bambiniList, ClassificaGiocoFragment.this));
                            recyclerView.getAdapter().notifyDataSetChanged();
                        }
                    } else {
                        Log.d(TAG, "Errore durante la query per i bambini disponibili", task.getException());
                    }
        });
    }

    @Override
    public void onBambinoClick(int position) {
        Log.d(TAG, "onBambinoClick: " + position);
    }

    private Figlio createFiglio(QueryDocumentSnapshot nuovoFiglio){
        return (new Figlio(
                nuovoFiglio.get("nome").toString(),
                nuovoFiglio.get("cognome").toString(),
                nuovoFiglio.get("codiceFiscale").toString(),
                nuovoFiglio.get("logopedista").toString(),
                nuovoFiglio.get("genitore").toString(),
                nuovoFiglio.get("dataNascita").toString(),
                Integer.parseInt(nuovoFiglio.get("idAvatar").toString()),
                nuovoFiglio.get("token").toString(),
                (long)nuovoFiglio.get("punteggioGioco")
        ));
    }
}
