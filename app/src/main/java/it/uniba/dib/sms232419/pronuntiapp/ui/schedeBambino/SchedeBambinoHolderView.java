package it.uniba.dib.sms232419.pronuntiapp.ui.schedeBambino;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import it.uniba.dib.sms232419.pronuntiapp.R;

public class SchedeBambinoHolderView extends RecyclerView.ViewHolder implements View.OnClickListener{
    TextView textViewNomeScheda, textViewNumeroEsercizi, textViewStatoScheda, textViewEserciziCompletati;
    MaterialButton eliminaButton, avviaGiocoButton;
    MaterialCardView cardView;
    Boolean completata;

    private final ClickSchedeBambinoListener clickSchedeBambinoListener;

    // Costruttore che riceve un'istanza di ClickListener
    public SchedeBambinoHolderView(View itemView, ClickSchedeBambinoListener listener) {
        super(itemView);
        // Imposta questo ViewHolder come gestore di clic
        itemView.setOnClickListener(this);
        // Salva il riferimento all'istanza di ClickListener
        this.clickSchedeBambinoListener = listener;

        textViewNomeScheda = itemView.findViewById(R.id.nome_scheda);
        textViewNumeroEsercizi = itemView.findViewById(R.id.numero_esercizi_scheda);
        textViewStatoScheda = itemView.findViewById(R.id.stato_scheda);
        textViewEserciziCompletati = itemView.findViewById(R.id.esercizi_completati_scheda);
        eliminaButton = itemView.findViewById(R.id.elimina_button);
        avviaGiocoButton = itemView.findViewById(R.id.avvia_gioco_button);
        cardView = itemView.findViewById(R.id.cardView_scheda);

        if(textViewStatoScheda.getText().toString().equals("completata")){
            completata = true;
        } else {
            completata = false;
        }

        eliminaButton.setOnClickListener(v -> {
            if(clickSchedeBambinoListener != null){
                clickSchedeBambinoListener.onEliminaClick(getAdapterPosition(), completata);
            }
        });

        avviaGiocoButton.setOnClickListener(v -> {
            if(clickSchedeBambinoListener != null){
                clickSchedeBambinoListener.onAvviaGiocoClick(getAdapterPosition(), completata);
            }
        });
    }

    // Metodo chiamato quando un elemento della RecyclerView viene cliccato
    @Override
    public void onClick(View view) {
        if(textViewStatoScheda.getText().toString().equals("completata")){
            completata = true;
        } else {
            completata = false;
        }
        // Verifica se il ClickListener è stato assegnato
        if (clickSchedeBambinoListener != null) {
            // Passa la posizione dell'elemento cliccato al ClickListener
            clickSchedeBambinoListener.onItemClick(getAdapterPosition(), completata);
        }
    }
}
