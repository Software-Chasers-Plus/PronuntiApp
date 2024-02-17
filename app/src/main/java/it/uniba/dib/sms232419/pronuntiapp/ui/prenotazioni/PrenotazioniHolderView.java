package it.uniba.dib.sms232419.pronuntiapp.ui.prenotazioni;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import it.uniba.dib.sms232419.pronuntiapp.R;

public class PrenotazioniHolderView extends RecyclerView.ViewHolder implements View.OnClickListener {

    TextView textViewLogopedistaFiglio, textViewDataPrenotazione, textViewOraPrenotazione;

    private ClickPrenotazioniListener clickPrenotazioniListener;

    // Costruttore che riceve un'istanza di ClickListener
    public PrenotazioniHolderView(View itemView) {
        super(itemView);
        // Imposta questo ViewHolder come gestore di clic
        itemView.setOnClickListener(this);

        textViewLogopedistaFiglio = itemView.findViewById(R.id.logopedista_prenotazione);
        textViewDataPrenotazione = itemView.findViewById(R.id.data_prenotazione);
        textViewOraPrenotazione = itemView.findViewById(R.id.ora_prenotazione);
    }

    // Metodo chiamato quando un elemento della RecyclerView viene cliccato
    @Override
    public void onClick(View view) {
        // Verifica se il ClickListener è stato assegnato
        if(clickPrenotazioniListener != null){
            // Passa la posizione dell'elemento cliccato al ClickListener
            clickPrenotazioniListener.onItemClick(getAdapterPosition());
        }
    }
}
