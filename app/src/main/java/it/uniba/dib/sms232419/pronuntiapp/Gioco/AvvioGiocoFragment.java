package it.uniba.dib.sms232419.pronuntiapp.Gioco;

import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import it.uniba.dib.sms232419.pronuntiapp.LoginFragment;
import it.uniba.dib.sms232419.pronuntiapp.R;

public class AvvioGiocoFragment extends Fragment {

    private MediaPlayer mediaPlayer;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.avvio_gioco_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);


        // Inizializza il MediaPlayer con il file audio desiderato
        mediaPlayer = MediaPlayer.create(getActivity(), R.raw.audio_sottofondo);

        // Avvia la riproduzione
        mediaPlayer.start();

        ImageView backButton = view.findViewById(R.id.exit_button_start_game);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quando si fa clic sul pulsante di uscita, ferma la riproduzione della musica
                mediaPlayer.stop();
                getActivity().finish();
            }
        });

        ImageView impostazioni = view.findViewById(R.id.settings_button_start_game);
        impostazioni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.avvio_gioco_fragment, ImpostazioniGiocoFragment.class, null)
                        .commit();
            }
        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Assicurati di rilasciare le risorse del MediaPlayer quando l'activity viene distrutta
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}