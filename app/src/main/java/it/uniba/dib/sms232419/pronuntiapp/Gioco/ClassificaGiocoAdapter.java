package it.uniba.dib.sms232419.pronuntiapp.Gioco;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.Figlio;

public class ClassificaGiocoAdapter extends RecyclerView.Adapter<ClasssificaGiocoHolderView> {
    private static final String TAG = "ClassificaGiocoAdapter";

    Context context;
    private ArrayList<Figlio> bambiniList;
    private ClickClassificaGiocoListener clickClassificaGiocoListener;

    public ClassificaGiocoAdapter(Context context, ArrayList<Figlio> bambiniList, ClickClassificaGiocoListener listener) {
        this.bambiniList = bambiniList;
        this.clickClassificaGiocoListener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public ClasssificaGiocoHolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ClasssificaGiocoHolderView(LayoutInflater.from(parent.getContext()).inflate(R.layout.classifica_bambino_cardview, parent, false), clickClassificaGiocoListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ClasssificaGiocoHolderView holder, int position) {
        holder.posizioneTextView.setText(String.valueOf(position + 1));
        holder.nomeTextView.setText(bambiniList.get(position).getNome());
        holder.punteggioTextView.setText(String.valueOf(bambiniList.get(position).getPunteggioGioco()));
    }

    @Override
    public int getItemCount() {
        return bambiniList.size();
    }
}
