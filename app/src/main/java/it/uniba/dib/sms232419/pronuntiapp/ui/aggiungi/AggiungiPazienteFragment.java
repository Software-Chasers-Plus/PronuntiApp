package it.uniba.dib.sms232419.pronuntiapp.ui.aggiungi;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.io.IOException;

import es.dmoral.toasty.Toasty;
import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.Logopedista;
import it.uniba.dib.sms232419.pronuntiapp.PermissionManager;

public class AggiungiPazienteFragment extends Fragment {
    private Button ricercaPaziente, aggiungiPaziente;
    private ExtendedFloatingActionButton scanQRCode,caricaQRCode;
    private Logopedista logopedista;
    private TextInputEditText tokenPaziente;
    private LinearProgressIndicator progressBar;
    private ImageView avatarPaziente;
    private TextView nomePaziente, dataNascitaPaziente, codiceFiscalePaziente,emailGenitore;
    private String figlioUid;
    private MaterialCardView cardView;
    private FirebaseFirestore db;
    private static final int PAZIENTE_TROVATO = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_STORAGE_PERMISSION = 2;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == PAZIENTE_TROVATO) {
                progressBar.setVisibility(View.GONE);
                cardView.setVisibility(View.VISIBLE);
            }
        }
    };

    private final ActivityResultLauncher<ScanOptions> qrCodeLaucher = registerForActivityResult(new ScanContract(), result ->{
        if(result != null){
            tokenPaziente.setText(result.getContents());
        }else{
            Toasty.error(getActivity(), R.string.nessun_qr_code_trovato, Toast.LENGTH_SHORT).show();
        }
    });


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Recupero i figli dal bundle passato al fragment
        if (getArguments() != null) {
            logopedista = getArguments().getParcelable("logopedista");
        }

        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_aggiungi_paziente, container, false);
        // Ottieni l'activity contenitore
        AppCompatActivity activity = (AppCompatActivity) getActivity();

        // Ottieni l'istanza della BottomNavigationView dall'activity
        BottomNavigationView bottomNavigationView = activity.findViewById(R.id.nav_view);

        // Nascondi la BottomNavigationView impostando la visibilità a GONE
        bottomNavigationView.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tokenPaziente = view.findViewById(R.id.token_paziente_aggiunta);

        ricercaPaziente = view.findViewById(R.id.bottone_ricerca_paziente_aggiunta);

        progressBar = view.findViewById(R.id.progressBar_aggiunta_paziente);

        cardView = view.findViewById(R.id.cardView_info_paziente_aggiunta);
        cardView.setVisibility(View.GONE);

        avatarPaziente = view.findViewById(R.id.avatar_paziente_aggiunta);

        nomePaziente = view.findViewById(R.id.nome_paziente_aggiunta);

        dataNascitaPaziente = view.findViewById(R.id.data_nascita_paziente_aggiunta);

        codiceFiscalePaziente = view.findViewById(R.id.codice_fiscale_paziente_aggiunta);

        emailGenitore = view.findViewById(R.id.email_genitore_paziente_aggiunta);
        ricercaPaziente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String token = tokenPaziente.getText().toString();
                if (token.isEmpty()) {
                    tokenPaziente.setError("Inserisci il token");
                    tokenPaziente.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    db.collection("figli").whereEqualTo("token", token).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if(!task.getResult().isEmpty()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d("AggiungiPazienteFragment", "Figlio trovato: " + document.get("nome").toString());
                                    if (document.get("logopedista").toString().isEmpty()) {
                                        figlioUid = document.getId();
                                        avatarPaziente.setImageResource(trovaIdAvatar(Integer.valueOf(document.get("idAvatar").toString())));
                                        nomePaziente.setText(document.get("nome").toString() + " " + document.get("cognome").toString());
                                        dataNascitaPaziente.setText(document.get("dataNascita").toString());
                                        codiceFiscalePaziente.setText(document.get("codiceFiscale").toString());
                                        db.collection("genitori")
                                                .document(document.get("genitore").toString())
                                                .get()
                                                .addOnCompleteListener(task1 -> {
                                                    if (task1.isSuccessful()) {
                                                        DocumentSnapshot document1 = task1.getResult();
                                                        if (document1.exists()) {
                                                            emailGenitore.setText(document1.get("Email").toString());
                                                            mHandler.sendEmptyMessage(PAZIENTE_TROVATO);
                                                            cardView.setVisibility(View.VISIBLE);
                                                        } else {
                                                            Log.e("AggiungiPazienteFragment", "Errore nella ricerca del genitore");
                                                        }
                                                    }
                                                });
                                    } else {
                                        Toasty.warning(getContext(), R.string.il_paziente_gi_associato_ad_un_logopedista, Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            } else {
                                Log.e("AggiungiPazienteFragment", "Figlio non trovato");
                                Toasty.warning(getContext(), "Il token inserito non è associato a nessun pazziente", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        } else {
                            Log.e("AggiungiPazienteFragment", "Errore nella ricerca del figlio");
                            Toasty.warning(getContext(), "Il token inserito non è associato a nessun pazziente", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });

        aggiungiPaziente = view.findViewById(R.id.bottone_conferma_aggiunti_paziente);
        aggiungiPaziente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (figlioUid != null) {
                    db.collection("figli")
                            .document(figlioUid)
                            .update("logopedista", logopedista.getUID());
                    Toasty.success(getContext(),"Paziente aggiunto correttamente", Toast.LENGTH_SHORT).show();
                    NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main_logopedista);
                    navController.navigate(R.id.navigation_home_logopedista);
                } else {
                    Log.e("AggiungiPazienteFragment", "Figlio nullo");
                }
            }
        });

        scanQRCode = view.findViewById(R.id.fab_scan_qr_aggiunta);
        scanQRCode.setOnClickListener(v -> {
            PermissionManager.requestPermissions(AggiungiPazienteFragment.this, new String[]{Manifest.permission.CAMERA},new PermissionManager.PermissionListener() {

                @Override
                public void onPermissionsGranted() {
                    showCamera();
                }

                @Override
                public void onPermissionsDenied() {
                    // Permesso non concesso, mostra un dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(R.string.permesso_negato)
                            .setMessage(R.string.per_favore_fornisci_il_permesso_per_accedere_alla_fotocamera_del_dispositivo)
                            .setPositiveButton(R.string.impostazioni, (dialog, which) -> {
                                // Aprire le impostazioni
                                openAppSettings();
                            })
                            .show();
                }
            });
        });



        caricaQRCode = view.findViewById(R.id.fab_carica_qr_aggiunta);
        caricaQRCode.setOnClickListener(v -> {
            PermissionManager.requestPermissions(AggiungiPazienteFragment.this, new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, new PermissionManager.PermissionListener() {
                @Override
                public void onPermissionsGranted() {
                    selectImage();
                }

                @Override
                public void onPermissionsDenied() {
                    // Permesso non concesso, mostra un dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(R.string.permesso_negato)
                            .setMessage(R.string.per_favore_fornisci_il_permesso_per_accedere_alla_galleria)
                            .setPositiveButton(R.string.impostazioni, (dialog, which) -> {
                                // Aprire le impostazioni
                                openAppSettings();
                            })
                            .show();
                }
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Ottieni l'activity contenitore
        AppCompatActivity activity = (AppCompatActivity) getActivity();

        // Ottieni l'istanza della BottomNavigationView dall'activity
        BottomNavigationView bottomNavigationView = activity.findViewById(R.id.nav_view);

        // Riporta la BottomNavigationView visibile
        bottomNavigationView.setVisibility(View.VISIBLE);
    }

    //Metodo per mostrare la fotocamera per la scansione del QR Code
    private void showCamera() {
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
        options.setPrompt("Inquadra il QR Code");
        options.setCameraId(0);
        options.setBeepEnabled(true);
        options.setBarcodeImageEnabled(true);
        options.setOrientationLocked(false);

        qrCodeLaucher.launch(options);
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    // Gestione della risposta alla richiesta di permesso
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults,
                new PermissionManager.PermissionListener() {
                    @Override
                    public void onPermissionsGranted() {
                        if(permissions[0].equals(Manifest.permission.CAMERA)){
                            showCamera();
                        } else if(permissions[0].equals(Manifest.permission.READ_MEDIA_IMAGES)){
                            selectImage();
                        }
                        Log.d("EsercizioDenominazioneImmagine", "Permissions granted");
                    }

                    @Override
                    public void onPermissionsDenied() {
                        if(permissions[0].equals(Manifest.permission.CAMERA)){
                            // Permesso non concesso, mostra un dialog
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle(R.string.permesso_negato)
                                    .setMessage(R.string.per_favore_fornisci_il_permesso_per_accedere_alla_fotocamera_del_dispositivo)
                                    .setPositiveButton(R.string.impostazioni, (dialog, which) -> {
                                        // Aprire le impostazioni
                                        openAppSettings();
                                    })
                                    .show();
                            Log.d("EsercizioDenominazioneImmagine", "Permissions denied");
                        } else if(permissions[0].equals(Manifest.permission.READ_MEDIA_IMAGES)){
                            // Permesso non concesso, mostra un dialog
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle(R.string.permesso_negato)
                                    .setMessage(R.string.per_favore_fornisci_il_permesso_per_accedere_alla_galleria)
                                    .setPositiveButton(R.string.impostazioni, (dialog, which) -> {
                                        // Aprire le impostazioni
                                        openAppSettings();
                                    })
                                    .show();
                            Log.d("EsercizioDenominazioneImmagine", "Permissions denied");
                        }
                    }
                });
    }

    // Metodo per aprire le impostazioni dell'applicazione
    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", requireContext().getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Controlla se il risultato proviene dalla richiesta di selezione dell'immagine
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData(); // Ottiene l'URI dell'immagine selezionata

            try {
                // Converte l'URI dell'immagine in un oggetto Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);

                // Mostra un dialogo di conferma per la scelta dell'immagine
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                // Crea un ImageView e imposta l'immagine
                // Ridimensiona l'immagine
                int width = 500; // Imposta la larghezza desiderata
                int height = (int) (bitmap.getHeight() * ((double) width / bitmap.getWidth())); // Calcola l'altezza per mantenere le proporzioni
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);

                ImageView imageView = new ImageView(getContext());
                imageView.setImageBitmap(scaledBitmap);
                builder.setView(imageView)
                        .setTitle("Conferma selezione immagine")
                        .setMessage("Sei sicuro di voler utilizzare questa immagine?")
                        .setPositiveButton("Sì", (dialog, which) -> {
                            // Continua con l'elaborazione dell'immagine
                            processImage(bitmap);
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                            // Annulla l'elaborazione dell'immagine
                            dialog.dismiss();
                        })
                        .show();
            } catch (IOException e) {
                e.printStackTrace(); // Gestisce le eccezioni quando il QR code non viene trovato
            }
        }
    }

    private void processImage(Bitmap bitmap) {
        // Inserisci qui il tuo codice per elaborare l'immagine
        // Crea un array di interi per memorizzare i dati dei pixel dell'immagine
        try {
            int[] intArray = new int[bitmap.getWidth() * bitmap.getHeight()];

            // Copia i dati dei pixel nell'array di interi
            bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

            // Crea una sorgente di luminanza da un array di interi
            LuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray);

            // Crea un'immagine binaria da una sorgente di luminanza
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

            // Crea un lettore per decodificare l'immagine binaria
            Reader reader = new MultiFormatReader();

            // Decodifica l'immagine binaria in un risultato
            Result result = reader.decode(binaryBitmap);

            // Ottiene il testo dal risultato (il contenuto del QR code)
            String qrCodeResult = result.getText();

            // Fai qualcosa con qrCodeResult (il contenuto del QR code)
            tokenPaziente.setText(qrCodeResult);

        } catch (ChecksumException | FormatException | NotFoundException e) {
            e.printStackTrace(); // Gestisce le eccezioni di I/O
        }
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

}
