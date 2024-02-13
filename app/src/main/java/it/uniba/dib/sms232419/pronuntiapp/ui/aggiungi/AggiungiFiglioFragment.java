package it.uniba.dib.sms232419.pronuntiapp.ui.aggiungi;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import it.uniba.dib.sms232419.pronuntiapp.MainActivityGenitore;
import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.Figlio;

public class AggiungiFiglioFragment extends Fragment {

    private List<Figlio> figli;
    private MainActivityGenitore mActivity;

    // Array contenente gli ID delle risorse delle immagini
    private final Integer[] images = {R.drawable.bambino_1, R.drawable.bambino_2, R.drawable.bambino_3, R.drawable.bambino_4, R.drawable.bambino_5, R.drawable.bambino_6};

    // Variabile per memorizzare l'ID dell'immagine selezionata
    private int selectedImageId = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivityGenitore) getActivity();

        // Recupero i figli dal bundle passato al fragment
        if (getArguments() != null) {
            figli = getArguments().getParcelableArrayList("figli");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.aggiungi_figli_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText nomeFiglio = view.findViewById(R.id.nome_figlio);
        EditText cognomeFiglio = view.findViewById(R.id.cognome_figlio);
        EditText codiceFiscaleFiglio = view.findViewById(R.id.codiFiscale_figlio);
        EditText dataNascitaFiglio = view.findViewById(R.id.data_nascita_figlio);

        RecyclerView imageRecyclerView = view.findViewById(R.id.imageRecyclerView);
        imageRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        imageRecyclerView.setAdapter(new ImageAdapter());

        EditText editTextDate = view.findViewById(R.id.data_nascita_figlio);
        ImageView iconaCalendario = view.findViewById(R.id.imageViewCalendar);
        iconaCalendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendarioCorrente = Calendar.getInstance();
                int anno = calendarioCorrente.get(Calendar.YEAR);
                int mese = calendarioCorrente.get(Calendar.MONTH);
                int giorno = calendarioCorrente.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // La data selezionata dall'utente
                                String dataSelezionata = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                                dataNascitaFiglio.setText(dataSelezionata);
                            }
                        }, anno, mese, giorno);
                datePickerDialog.show();
            }
        });

        Button confermaAggiungiFiglio = view.findViewById(R.id.conferma_aggiungi_figlio_button);
        confermaAggiungiFiglio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nome = nomeFiglio.getText().toString().trim();
                String cognome = cognomeFiglio.getText().toString().trim();
                String codiceFiscale = codiceFiscaleFiglio.getText().toString().trim();
                String dataNascita = dataNascitaFiglio.getText().toString().trim();

                if (nome.isEmpty() || cognome.isEmpty() || codiceFiscale.isEmpty() || dataNascita.isEmpty() || selectedImageId == -1) {
                    Toast.makeText(getContext(), "Inserisci tutti i dati e seleziona un'immagine", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Genera un token univoco a partire dal codice fiscale per ogni figlio
                String token = generateTokenFromString(codiceFiscale);

                // Salvataggio del figlio nel database
                String genitoreUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> figlio = new HashMap<>();
                figlio.put("nome", nome);
                figlio.put("cognome", cognome);
                figlio.put("codiceFiscale", codiceFiscale);
                figlio.put("dataNascita", dataNascita);
                figlio.put("genitore", genitoreUid);
                figlio.put("logopedista", "");
                figlio.put("idAvatar", selectedImageId);
                figlio.put("token", token);
                db.collection("figli")
                        .add(figlio)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    figli.add(new Figlio(nome, cognome, codiceFiscale, "", genitoreUid, dataNascita, selectedImageId, token));
                                    Toast.makeText(mActivity, "Figlio aggiunto con successo", Toast.LENGTH_SHORT).show();
                                    NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
                                    navController.navigate(R.id.navigation_home);
                                } else {
                                    Toast.makeText(mActivity, "Registrazione fallita", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    // Adapter per le immagini
    class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
            return new ImageViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
            Integer imageResId = images[position];
            holder.imageView.setImageResource(imageResId);

            // Imposta il bordo attorno all'immagine selezionata
            if (imageResId == selectedImageId) {
                holder.imageView.setBackgroundResource(R.drawable.selected_border);
            } else {
                holder.imageView.setBackgroundResource(0);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedImageId = images[position]; // Ottieni l'ID dell'immagine selezionata
                    notifyDataSetChanged(); // Notifica all'adapter che i dati sono stati modificati
                }
            });
        }

        @Override
        public int getItemCount() {
            return images.length;
        }

        // ViewHolder per le immagini
        class ImageViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;

            public ImageViewHolder(@NonNull View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageView);
            }
        }
    }

    // Genera un token univoco a partire dal codice fiscale per ogni figlio
    public static String generateTokenFromString(String codiceFiscale) {
        // Converti la stringa univoca in un array di byte
        byte[] bytes = codiceFiscale.getBytes();

        // Genera un UUID basato sulla stringa univoca
        UUID uuid = UUID.nameUUIDFromBytes(bytes);

        // Converte l'UUID in una stringa
        String token = uuid.toString();

        // Rimuovi eventuali trattini dalla stringa generata
        token = token.replaceAll("-", "");

        return token;
    }
}
