package it.uniba.dib.sms232419.pronuntiapp.ui.info.dettaglioScheda;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import it.uniba.dib.sms232419.pronuntiapp.R;

public class EserciziHolderView extends RecyclerView.ViewHolder{
    TextView textViewNomeEsercizio, textViewTipologiaEsercizio, textDataEsercizio, textCompletamentoEsercizio;

    // Costruttore che riceve un'istanza di ClickListener
    public EserciziHolderView(View itemView) {
        super(itemView);

        textViewNomeEsercizio = itemView.findViewById(R.id.nome_esercizio_dettaglio_scheda);
        textViewTipologiaEsercizio = itemView.findViewById(R.id.categoria_esercizio_dettaglio_scheda);
        textDataEsercizio = itemView.findViewById(R.id.data_esercizio_dettaglio_scheda);
        textCompletamentoEsercizio = itemView.findViewById(R.id.stato_esercizio_dettaglio_scheda);
    }
}
