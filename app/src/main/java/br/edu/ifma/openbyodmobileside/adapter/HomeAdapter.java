package br.edu.ifma.openbyodmobileside.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import br.edu.ifma.openbyodmobileside.R;
import br.edu.ifma.openbyodmobileside.modelo.ItemHome;

/**
 * Created by Windows on 25/08/2017.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {
    private List<ItemHome> itens;
    private int rowLayout;
    private Context context;
    private static AdapterInterface homeInterface;

    public static class HomeViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout itensLayout;
        TextView titulo;
        TextView descricao;
        ImageView imagem;

        public HomeViewHolder(View v) {
            super(v);
            itensLayout = (RelativeLayout) v.findViewById(R.id.home_layout);
            titulo = (TextView) v.findViewById(R.id.tituloItem);
            descricao = (TextView) v.findViewById(R.id.descricaoItem);
            imagem = (ImageView) v.findViewById(R.id.imagemItem);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    homeInterface.onItemClick(getPosition());
                }
            });
        }
    }

    public HomeAdapter(List<ItemHome> itens, int rowLayout,
                         Context context, AdapterInterface homeInterface) {
        this.itens = itens;
        this.rowLayout = rowLayout;
        this.context = context;
        this.homeInterface = homeInterface;
    }

    @Override
    public HomeAdapter.HomeViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new HomeAdapter.HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HomeAdapter.HomeViewHolder holder, final int position) {
        holder.titulo.setText(itens.get(position).getTitulo());
        holder.descricao.setText(itens.get(position).getDescricao());
        holder.imagem.setImageDrawable(itens.get(position).getImagem());
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }
}
