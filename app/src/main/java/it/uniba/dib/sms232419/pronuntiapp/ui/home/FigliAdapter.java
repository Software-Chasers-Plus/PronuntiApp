package it.uniba.dib.sms232419.pronuntiapp.ui.home;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.uniba.dib.sms232419.pronuntiapp.R;
import it.uniba.dib.sms232419.pronuntiapp.model.Figlio;

public class FigliAdapter extends RecyclerView.Adapter<FigliHolderView> {
    private Context context;
    private List<Figlio> items;
    private List<Integer> avatarIds;
    private ClickFigliListener clickFigliListener;

    public FigliAdapter(Context context, List<Figlio> items, List<Integer> avatarIds, ClickFigliListener listener) {
        this.context = context;
        this.items = items;
        this.avatarIds = avatarIds;
        this.clickFigliListener = listener;
    }

    @NonNull
    @Override
    public FigliHolderView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FigliHolderView(LayoutInflater.from(context).inflate(R.layout.item_figli_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FigliHolderView holder, int position) {
        Figlio figlio = items.get(position);
        holder.textViewNomeFiglio.setText(figlio.getNome());
        holder.textViewEtaFiglio.setText(figlio.getDataNascita());
        holder.textViewLogopedistaFiglio.setText(figlio.getLogopedista());

        int avatarId = figlio.getIdAvatar();

        int avatarDrawableId;
        switch (avatarId) {
            case 2131165308:
                avatarDrawableId = R.drawable.bambino_1;
                break;
            case 2131165309:
                avatarDrawableId = R.drawable.bambino_2;
                break;
            case 2131165310:
                avatarDrawableId = R.drawable.bambino_3;
                break;
            case 2131165311:
                avatarDrawableId = R.drawable.bambino_4;
                break;
            case 2131165312:
                avatarDrawableId = R.drawable.bambino_5;
                break;
            case 2131165313:
                avatarDrawableId = R.drawable.bambino_6;
                break;
            default:
                avatarDrawableId = R.drawable.bambino;
                break;
        }

        holder.imageViewFiglio.setImageResource(avatarDrawableId);

        // Imposta il click listener sull'elemento della RecyclerView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                if (clickFigliListener != null && adapterPosition != RecyclerView.NO_POSITION) {
                    // Passa l'indice dell'elemento cliccato al listener
                    clickFigliListener.onItemClick(adapterPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}

