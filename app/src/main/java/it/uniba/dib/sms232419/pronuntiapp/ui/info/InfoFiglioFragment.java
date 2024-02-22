package it.uniba.dib.sms232419.pronuntiapp.ui.info;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;

import it.uniba.dib.sms232419.pronuntiapp.FirebaseHelper;
import it.uniba.dib.sms232419.pronuntiapp.Gioco.GiocoActivity;
import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.Figlio;
import it.uniba.dib.sms232419.pronuntiapp.model.Scheda;
import it.uniba.dib.sms232419.pronuntiapp.ui.schedeBambino.ClickSchedeBambinoListener;
import it.uniba.dib.sms232419.pronuntiapp.ui.schedeBambino.SchedeBambinoAdapter;

public class InfoFiglioFragment extends Fragment implements ClickSchedeBambinoListener {

    String TAG = "InfoFiglioFragment";
    private Figlio figlio;
    private FirebaseFirestore db;
    private ArrayList<Scheda> schedaList;
    private RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            // recupero il figlio dal bundle passato al fragment
            figlio = getArguments().getParcelable("figlio");

            Log.d(TAG, "Figlio recuperato: "+figlio.getNome());
        } else {
            Log.d(TAG, "Bundle nullo");
        }

        // Inizializzazione di Firestore
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dettaglio_figlio, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.schede_recycler_view_paziente);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        TextView codiceFiscaleFiglio = null;
        if (figlio != null) {
            TextView nomeFiglio = view.findViewById(R.id.nome_figlio_dettaglio);
            nomeFiglio.setText(figlio.getNome());

            TextView cognomeFiglio = view.findViewById(R.id.cognome_figlio_dettaglio);
            cognomeFiglio.setText(figlio.getCognome());

            ImageView avatarFiglio = view.findViewById(R.id.avatar_figlio_dettaglio);
            avatarFiglio.setImageResource(trovaIdAvatar(figlio.getIdAvatar()));

            codiceFiscaleFiglio = view.findViewById(R.id.codice_fiscale_figlio_dettaglio);
            codiceFiscaleFiglio.setText(figlio.getCodiceFiscale());

            TextView dataNascitaFiglio = view.findViewById(R.id.data_nascita_figlio_dettaglio);
            dataNascitaFiglio.setText(figlio.getDataNascita());

            TextView tokenFiglio = view.findViewById(R.id.token_figlio_dettaglio);
            tokenFiglio.setText(figlio.getToken());

            ImageView qrCode = view.findViewById(R.id.icon_qr_code);
            qrCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popUpToken();
                }
            });

            TextView emailLogopedistaFiglio = view.findViewById(R.id.email_logopedista_figlio_dettaglio);

            // Visualizza l'email del logopedista subito se disponibile
            if (figlio.getLogopedista() != null && !figlio.getLogopedista().isEmpty()) {
                emailLogopedistaFiglio.setText("Caricamento in corso...");
                db.collection("logopedisti")
                        .document(figlio.getLogopedista())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    String emailLogopedista = documentSnapshot.getString("Email");
                                    if (emailLogopedista != null && !emailLogopedista.isEmpty()) {
                                        emailLogopedistaFiglio.setText(emailLogopedista);
                                    } else {
                                        emailLogopedistaFiglio.setText("Nessun logopedista");
                                    }
                                } else {
                                    Log.d(TAG, "Il documento del logopedista non esiste");
                                    emailLogopedistaFiglio.setText("Nessun logopedista");
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "Errore nel recuperare l'email del logopedista: " + e.getMessage());
                                emailLogopedistaFiglio.setText("Errore nel recuperare l'email del logopedista");
                            }
                        });
            } else {
                emailLogopedistaFiglio.setText("Nessun logopedista");
            }
        } else {
            Log.d(TAG, "Figlio nullo");
        }

        TextView schedeNonCreate = view.findViewById(R.id.text_dashboard);


        //Recupero delle schede e aggiornamento della recycler view
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //Recupero id del paziente
        db.collection("figli")
                .whereEqualTo("codiceFiscale", codiceFiscaleFiglio.getText().toString())
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String pazienteId = document.getId();
                                    Log.d(TAG, "PazienteId: " + pazienteId);
                                    //Recupero le schede del paziente
                                    db.collection("schede")
                                            .whereEqualTo("figlio", pazienteId)
                                            .get()
                                            .addOnCompleteListener(task2 -> {
                                                if (task2.isSuccessful()) {
                                                    schedaList = new ArrayList<>();

                                                    for (QueryDocumentSnapshot document2 : task2.getResult()) {
                                                        Log.d(TAG, "Scheda prima della conversione: " + document2.getString("nome"));
                                                        schedaList.add(FirebaseHelper.creazioneScheda(document2));
                                                    }

                                                    Log.d(TAG, "Schede disponibili: " + schedaList.size());

                                                    if (!schedaList.isEmpty()) {
                                                        Log.d(TAG, "Scheda dopo la conversione: " + schedaList.get(0).getNome());
                                                        recyclerView.setAdapter(new SchedeBambinoAdapter(requireContext(), schedaList, InfoFiglioFragment.this));
                                                        recyclerView.getAdapter().notifyDataSetChanged();
                                                    } else {
                                                        schedeNonCreate.setVisibility(View.VISIBLE);
                                                        Log.d(TAG, "Nessun scheda disponibile");
                                                    }

                                                } else {
                                                    Log.e(TAG, "Errore durante la query per le schede disponibili", task2.getException());
                                                }
                                            });
                                }
                            } else {
                                Log.e(TAG, "Errore durante la query per i figli", task.getException());
                            }
                        });

    }

    private int trovaIdAvatar(int idAvatar) {

        int avatarDrawableId;
        switch (idAvatar) {
            case 0:
                avatarDrawableId = R.drawable.bambino_1;
                break;
            case 1:
                avatarDrawableId = R.drawable.bambino_2;
                break;
            case 2:
                avatarDrawableId = R.drawable.bambino_3;
                break;
            case 3:
                avatarDrawableId = R.drawable.bambino_4;
                break;
            case 4:
                avatarDrawableId = R.drawable.bambino_5;
                break;
            case 5:
                avatarDrawableId = R.drawable.bambino_6;
                break;
            default:
                avatarDrawableId = R.drawable.bambino;
                break;
        }
        return avatarDrawableId;
    }

    private void popUpToken(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.popup_qrcode_token, null);
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();

        ImageView qrCode = view.findViewById(R.id.codice_qr_token);
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            // Genera il QR code con il token del figlio
            BitMatrix bitMatrix = multiFormatWriter.encode(figlio.getToken(), com.google.zxing.BarcodeFormat.QR_CODE, 300, 300);

            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            qrCode.setImageBitmap(bitmap);

            FloatingActionButton shareButton = view.findViewById(R.id.condividi_qr_code_button);
            shareButton.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                   // Condividi il QR code

                                                   String bitmapPath = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), bitmap,getString(R.string.qr_code_token_bambino_pronuntiapp),null);

                                                   Uri uri = Uri.parse(bitmapPath);
                                                   Intent intent = new Intent(Intent.ACTION_SEND);
                                                   intent.setType("image/png");
                                                   intent.putExtra(Intent.EXTRA_STREAM, uri);
                                                   intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.questo_il_qr_code_del_token_del_bambino)+figlio.getNome()+getString(R.string.per_l_applicazione_pronuntiapp));
                                                   startActivity(Intent.createChooser(intent, getString(R.string.condividi_con)));
                                               }
                                           });

            dialog.setView(view);
            dialog.show();
            Log.d(TAG, "QR code generato correttamente: ");
        } catch (Exception e) {
            dialog.show();
            Log.d(TAG, "Errore nella generazione del QR code: " + e.getMessage());
        }
    }

    @Override
    public void onItemClick(int position) {
        //TODO: implementare la visualizzazione dei dettagli scheda
    }

    @Override
    public void onEliminaClick(int position) {
        //Non e' necessario implementarlo poiche' non e' possibile eliminare le schede da parte del genitore
    }

    @Override
    public void onAvviaGiocoClick(int position) {
        // Creo dialog di conferma
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Conferma avvio gioco");
        builder.setMessage("Sei sicuro di voler avviare il gioco?\n\nIl gioco non potrà essere interrotto senza il PIN.");
        builder.setIcon(R.drawable.alert_avvio_gioco);
        builder.setCancelable(false); // L'utente non può chiudere il dialog cliccando fuori da esso

        // Aggiunta dei pulsanti
        builder.setPositiveButton("Si", (dialog, which) -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable("scheda", schedaList.get(position));
            bundle.putParcelable("figlio", figlio);
            Intent intent = new Intent(getActivity(), GiocoActivity.class);
            intent.putExtras(bundle);
            Log.d(TAG, "Avvio gioco con la scheda: " + schedaList.get(position).getNome());
            startActivity(intent);
        });

        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.dismiss();
        });


        // Mostra il dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
