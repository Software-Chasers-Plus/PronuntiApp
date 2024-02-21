package it.uniba.dib.sms232419.pronuntiapp.ui.esercizi;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import it.uniba.dib.sms232419.pronuntiapp.R;

public class EserciziHolderView extends RecyclerView.ViewHolder implements View.OnClickListener{
    TextView textViewNomeEsercizio, textViewTipologiaEserciio;
    MaterialButton deleteButtonEsercizio;

    private ClickEserciziListener clickEserciziListener;

    // Costruttore che riceve un'istanza di ClickListener
    public EserciziHolderView(View itemView, ClickEserciziListener listener) {
        super(itemView);
        // Imposta questo ViewHolder come gestore di clic
        itemView.setOnClickListener(this);
        // Salva il riferimento all'istanza di ClickListener
        this.clickEserciziListener = listener;

        textViewNomeEsercizio = itemView.findViewById(R.id.esercizio_nome);
        textViewTipologiaEserciio = itemView.findViewById(R.id.esercizio_tipologia);
        deleteButtonEsercizio = itemView.findViewById(R.id.delete_button);

        deleteButtonEsercizio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickEserciziListener != null){
                    clickEserciziListener.onDeleteClick(getAdapterPosition());
                }
            }
        });
    }

    // Metodo chiamato quando un elemento della RecyclerView viene cliccato
    @Override
    public void onClick(View view) {
        // Verifica se il ClickListener è stato assegnato
        if(clickEserciziListener != null){
            // Passa la posizione dell'elemento cliccato al ClickListener
            clickEserciziListener.onItemClick(getAdapterPosition());

        }
    }
}
