package it.uniba.dib.sms232419.pronuntiapp.ui.info;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.databinding.FragmentHomeBinding;
import it.uniba.dib.sms232419.pronuntiapp.model.Figlio;

public class InfoFiglioFragment extends Fragment {

    private Figlio figlio;
    private FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            // recupero il figlio dal bundle passato al fragment
            figlio = getArguments().getParcelable("figlio");

            Log.d("InfoFiglioFragment", "Figlio recuperato: "+figlio.getNome());
        } else {
            Log.d("InfoFiglioFragment", "Bundle nullo");
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

        if (figlio != null) {
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
                                    Log.d("InfoFiglioFragment", "Il documento del logopedista non esiste");
                                    emailLogopedistaFiglio.setText("Nessun logopedista");
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("InfoFiglioFragment", "Errore nel recuperare l'email del logopedista: " + e.getMessage());
                                emailLogopedistaFiglio.setText("Errore nel recuperare l'email del logopedista");
                            }
                        });
            } else {
                emailLogopedistaFiglio.setText("Nessun logopedista");
            }
        } else {
            Log.d("InfoFiglioFragment", "Figlio nullo");
        }
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
            Log.d("InfoFiglioFragment", "QR code generato correttamente: ");
        } catch (Exception e) {
            dialog.show();
            Log.d("InfoFiglioFragment", "Errore nella generazione del QR code: " + e.getMessage());
        }
    }
}
