package it.uniba.dib.sms232419.pronuntiapp.ui.info.dettaglioScheda;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms232419.pronuntiapp.ui.info.dettaglioScheda.EserciziHolderView;

import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.Esercizio;

public class EserciziAdapter extends RecyclerView.Adapter<EserciziHolderView> {
    Context context;

    List<Esercizio> items;
    List<String> dateEsercizi;
    List<String> completamentoEsercizi;

    // Costruttore che riceve la lista di dati e un'istanza di ClickListener
    public EserciziAdapter(Context context, List<Esercizio> items, List<String> dateEsercizi, List<String> completamentoEsercizi) {
        this.context = context;
        this.items = items;
        this.dateEsercizi = dateEsercizi;
        this.completamentoEsercizi = completamentoEsercizi;
    }

    @NonNull
    @Override
    public EserciziHolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EserciziHolderView(LayoutInflater.from(context).inflate(R.layout.item_esercizio_dettaglio, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull EserciziHolderView holder, int position) {
        holder.textViewNomeEsercizio.setText(items.get(position).getNome());
        holder.textViewTipologiaEsercizio.setText(items.get(position).getTipologia());
        holder.textDataEsercizio.setText(dateEsercizi.get(position));
        if(completamentoEsercizi.get(position).equals("Completato")){
            holder.textCompletamentoEsercizio.setTextColor(context.getResources().getColor(R.color.green));
            holder.textCompletamentoEsercizio.setText(completamentoEsercizi.get(position));
        }else{
            holder.textCompletamentoEsercizio.setTextColor(context.getResources().getColor(R.color.red));
            holder.textCompletamentoEsercizio.setText(completamentoEsercizi.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
