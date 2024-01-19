package it.uniba.dib.sms232419.pronuntiapp.ui.aggiungiFiglio;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.Logopedista;
import it.uniba.dib.sms232419.pronuntiapp.ui.home.FigliHolderView;

public class LogopedistiSimiliAdapter extends RecyclerView.Adapter<LogopedistiSimiliHolderView>{

    Context context;

    List<Logopedista> items;

    private ClickLogopedistiSimiliListener clickLogopedistiSimiliListener;

    // Costruttore che riceve la lista di dati e un'istanza di ClickListener
    public LogopedistiSimiliAdapter(Context context, List<Logopedista> items, ClickLogopedistiSimiliListener listener) {
        this.context = context;
        this.items = items;
        this.clickLogopedistiSimiliListener = listener;
    }

    @NonNull
    @Override
    public LogopedistiSimiliHolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LogopedistiSimiliHolderView(LayoutInflater.from(context).inflate(R.layout.item_logopedisti_simili_view, parent, false), clickLogopedistiSimiliListener);
    }

    @Override
    public void onBindViewHolder(@NonNull LogopedistiSimiliHolderView holder, int position) {
        holder.textViewNomeLogopedista.setText(items.get(position).getNome() + " " + items.get(position).getCognome());
        holder.textViewemailLogopedista.setText(items.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
