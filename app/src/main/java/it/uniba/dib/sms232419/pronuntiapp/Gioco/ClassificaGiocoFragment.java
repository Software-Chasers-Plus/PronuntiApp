package it.uniba.dib.sms232419.pronuntiapp.Gioco;

import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.Figlio;

public class ClassificaGiocoFragment extends Fragment implements ClickClassificaGiocoListener{

    private static final String TAG = "ClassificaGiocoFragment";
    private GiocoActivity giocoActivity;
    private RecyclerView recyclerView;

    ArrayList<Figlio> bambiniList;

    Figlio bambinoPosizione1;
    Figlio bambinoPosizione2;
    Figlio bambinoPosizione3;

    ImageView bambino1ImageView;
    ImageView bambino2ImageView;
    ImageView bambino3ImageView;

    TextView nomeBambino1TextView;
    TextView nomeBambino2TextView;
    TextView nomeBambino3TextView;

    TextView punteggioBambino1TextView;
    TextView punteggioBambino2TextView;
    TextView punteggioBambino3TextView;

    String pathImmagineBambino;
    int resourceId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classifica_gioco, container, false);

        giocoActivity = (GiocoActivity) getActivity();



        bambino1ImageView = view.findViewById(R.id.bambino1ImageView);
        bambino2ImageView = view.findViewById(R.id.bambino2ImageView);
        bambino3ImageView = view.findViewById(R.id.bambino3ImageView);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.classificaGiocoRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        MaterialCardView cardView = view.findViewById(R.id.materialCardViewClassifica);

        Log.d(TAG, giocoActivity.sfondoSelezionato.toString());
        // Inizializza il layout con l'immagine di sfondo selezionata in base a  public Integer sfondoSelezionato di GiocoActivity
        switch (giocoActivity.sfondoSelezionato) {
            case 0:
                cardView.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primaryDeserto));
                break;
            case 1:
                cardView.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primaryAntartide));
                break;
            case 2:
                cardView.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.primaryGiungla));
                break;
        }

        //Creazione riferimento al database
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Log.d(TAG, "Riferimento al database creato");

        db.collection("figli")
                .whereEqualTo("logopedista", giocoActivity.figlio.getLogopedista())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        bambiniList = new ArrayList<>();

                        for(QueryDocumentSnapshot document : task.getResult()) {
                            bambiniList.add(createFiglio(document));
                        }

                        //Ordina la lista dei bambini in base al punteggio
                        bambiniList.sort((o1, o2) -> (int) (o2.getPunteggioGioco() - o1.getPunteggioGioco()));

                        Log.d(TAG, "bambiniList: " + bambiniList);

                        if(!bambiniList.isEmpty()) {
                            // Setta i primi tre bambini
                            //Setta l'immagine, il nome e il punteggio del primo bambino
                            bambinoPosizione1 = bambiniList.get(0);
                            pathImmagineBambino = "bambino_" + bambinoPosizione1.getIdAvatar();
                            resourceId = getResources().getIdentifier(pathImmagineBambino, "drawable", requireContext().getPackageName());
                            if (resourceId != 0) {
                                bambino1ImageView.setImageResource(resourceId);
                            }

                            nomeBambino1TextView = view.findViewById(R.id.nome1TextView);
                            adjustTextDimension(nomeBambino1TextView, bambinoPosizione1.getNome());

                            punteggioBambino1TextView = view.findViewById(R.id.punteggio1TextView);
                            punteggioBambino1TextView.setText(String.valueOf(bambinoPosizione1.getPunteggioGioco()));


                            //Setta l'immagine, il nome e il punteggio del secondo bambino
                            if (bambiniList.size() > 1) {
                                bambinoPosizione2 = bambiniList.get(1);
                                pathImmagineBambino = "bambino_" + bambinoPosizione2.getIdAvatar();
                                resourceId = getResources().getIdentifier(pathImmagineBambino, "drawable", requireContext().getPackageName());
                                if (resourceId != 0) {
                                    bambino2ImageView.setImageResource(resourceId);
                                }
                                nomeBambino2TextView = view.findViewById(R.id.nome2TextView);
                                adjustTextDimension(nomeBambino2TextView, bambinoPosizione2.getNome());

                                punteggioBambino2TextView = view.findViewById(R.id.punteggio2TextView);
                                punteggioBambino2TextView.setText(String.valueOf(bambinoPosizione2.getPunteggioGioco()));
                            } else {
                                CardView cardView2 = view.findViewById(R.id.cardview_secondo_posto);
                                cardView2.setVisibility(View.GONE);
                            }

                            if (bambiniList.size() > 2) {
                                //Setta l'immagine, il nome e il punteggio del terzo bambino
                                bambinoPosizione3 = bambiniList.get(2);
                                pathImmagineBambino = "bambino_" + bambinoPosizione3.getIdAvatar();
                                resourceId = getResources().getIdentifier(pathImmagineBambino, "drawable", requireContext().getPackageName());
                                if (resourceId != 0) {
                                    bambino3ImageView.setImageResource(resourceId);
                                }
                                nomeBambino3TextView = view.findViewById(R.id.nome3TextView);
                                adjustTextDimension(nomeBambino3TextView, bambinoPosizione3.getNome());

                                punteggioBambino3TextView = view.findViewById(R.id.punteggio3TextView);
                                punteggioBambino3TextView.setText(String.valueOf(bambinoPosizione3.getPunteggioGioco()));
                            } else {
                                CardView cardView3 = view.findViewById(R.id.cardview_terzo_posto);
                                cardView3.setVisibility(View.GONE);
                            }

                            if (bambiniList.size() > 3) {
                                for (int i = 0; i < 3; i++) {
                                    bambiniList.remove(0);
                                }
                                //Setta la recyclerView con i bambini rimanenti
                                recyclerView.setAdapter(new ClassificaGiocoAdapter(requireContext(), bambiniList, ClassificaGiocoFragment.this, giocoActivity));
                                recyclerView.getAdapter().notifyDataSetChanged();

                                //Modifca del colore negli elementi della recyclerView
                                for (int i = 0; i < bambiniList.size(); i++) {

                                }
                            }
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
                (long)nuovoFiglio.get("punteggioGioco"),
                Integer.parseInt(nuovoFiglio.get("sfondoSelezionato").toString()),
                Integer.parseInt(nuovoFiglio.get("personaggioSelezionato").toString())
        ));
    }

    private void adjustTextDimension(TextView textView, String text){
        int maxLength = 20; // Maximum length before text size starts decreasing
        float maxSize = 24f; // Maximum text size
        float minSize = 12f; // Minimum text size

        // Calculate the text size based on the length of the text
        float textSize = maxSize - (maxSize - minSize) * (Math.min(text.length(), maxLength) / (float) maxLength);

        // Set the text size
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);

    }
}
