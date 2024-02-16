package it.uniba.dib.sms232419.pronuntiapp.Gioco;

import static java.lang.Math.abs;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.uniba.dib.sms232419.pronuntiapp.R;

public class ImpostazioniGiocoFragment extends Fragment {

    private AudioManager audioManager;
    private SeekBar volumeSeekBar;
    private ImageView audioImageView;
    private boolean isAudioOn = true;

    int previousVolume;

    ContentObserver volumeObserver;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inizializza l'AudioManager
        audioManager = (AudioManager) requireActivity().getSystemService(requireActivity().AUDIO_SERVICE);
        previousVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volumeObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange);
                // Ottieni il volume corrente dal ContentResolver
                int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                volumeSeekBar.setProgress(volume);
                // Fai qualcosa con il volume
                Log.d("ImpostazioniGiocoFragment", "Volume: " + volume);
            }
        };
        // Registra il ContentObserver per rilevare i cambiamenti del volume e aggiornare la SeekBar
        requireActivity().getContentResolver().registerContentObserver(Settings.System.CONTENT_URI, true, volumeObserver);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_impostazioni_gioco, container, false);

        volumeSeekBar = view.findViewById(R.id.seekBar);

        // Imposta il massimo volume e il volume attuale
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volumeSeekBar.setMax(maxVolume);
        volumeSeekBar.setProgress(currentVolume);

        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Imposta il volume del flusso audio STREAM_MUSIC
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);

                // Aggiorna la SeekBar
                seekBar.setProgress(progress);

                // Aggiorna lo stato dell'audio e l'immagine corrispondente
                isAudioOn = (progress > 0);
                audioImageView.setImageResource(isAudioOn ? R.drawable.audio_on : R.drawable.audio_off);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        audioImageView = view.findViewById(R.id.audio);
        audioImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAudioOn) {
                    // Spegni l'audio
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                    volumeSeekBar.setProgress(0);
                } else {
                    // Accendi l'audio al volume precedente
                    int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
                    volumeSeekBar.setProgress(currentVolume);
                }

                // Inverti lo stato dell'audio
                isAudioOn = !isAudioOn;

                audioImageView.setImageResource(isAudioOn ? R.drawable.audio_on : R.drawable.audio_off);
            }
        });


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Richiedi il permesso MODIFY_AUDIO_SETTINGS se non è già stato concesso
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.MODIFY_AUDIO_SETTINGS}, 0);
        }

        RecyclerView recyclerView = view.findViewById(R.id.carousel_recycler_view);
        ArrayList<Integer> arrayList = new ArrayList<>();

        arrayList.add(R.drawable.deserto);
        arrayList.add(R.drawable.antartide);
        arrayList.add(R.drawable.giungla);

        // Crea e imposta l'adapter per il RecyclerView
        sfondoAdapter adapter = new sfondoAdapter(getActivity(), arrayList);
        recyclerView.setAdapter(adapter);

        RelativeLayout impostazioni = view.findViewById(R.id.impostazioni_gioco);
        TextView sfondoSelezionato = view.findViewById(R.id.sfondo_selezionato);
        RelativeLayout fragmentLayout = view.findViewById(R.id.impostazioni_gioco);
        CardView cardView = view.findViewById(R.id.cardView);

        adapter.setOnItemClickListener(new sfondoAdapter.OnItemClickListener() {
            @Override
            public void onClick(ImageView imageView, Integer path) {
                switch (path) {
                    case 0:
                        fragmentLayout.setBackgroundResource(R.drawable.deserto);
                        sfondoSelezionato.setText("Deserto");
                        sfondoSelezionato.setTextColor(getResources().getColor(R.color.primaryDeserto));
                        cardView.setBackgroundTintList(getResources().getColorStateList(R.color.primaryDeserto));
                        break;
                    case 1:
                        fragmentLayout.setBackgroundResource(R.drawable.antartide);
                        sfondoSelezionato.setText("Antartide");
                        sfondoSelezionato.setTextColor(getResources().getColor(R.color.secondaryAntartide));
                        cardView.setBackgroundTintList(getResources().getColorStateList(R.color.primaryAntartide));
                        break;
                    case 2:
                        fragmentLayout.setBackgroundResource(R.drawable.giungla);
                        sfondoSelezionato.setText("Giungla");
                        sfondoSelezionato.setTextColor(getResources().getColor(R.color.secondaryGiungla));
                        cardView.setBackgroundTintList(getResources().getColorStateList(R.color.primaryGiungla));
                        break;
                }
            }
        });

        // Imposta il deserto come layout di default selezionato
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                adapter.setDefaultSelectedItemView(recyclerView.getChildAt(0));
                fragmentLayout.setBackgroundResource(R.drawable.deserto);
                sfondoSelezionato.setTextColor(getResources().getColor(R.color.primaryDeserto));
                cardView.setBackgroundTintList(getResources().getColorStateList(R.color.primaryDeserto));
                sfondoSelezionato.setText("Deserto");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        volumeSeekBar = null;
        audioImageView = null;
        requireActivity().getContentResolver().unregisterContentObserver(volumeObserver);
    }
}
