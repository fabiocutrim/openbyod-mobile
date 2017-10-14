package br.edu.ifma.openbyodmobileside.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import br.edu.ifma.openbyodmobileside.R;
import br.edu.ifma.openbyodmobileside.modelo.Contato;

/**
 * Created by Windows on 23/08/2017.
 */

public class AgendaAdapter extends RecyclerView.Adapter<AgendaAdapter.AgendaViewHolder> {

    private List<Contato> contatos;
    private int rowLayout;
    private Context context;
    private static AdapterInterface agendaInterface;


    public static class AgendaViewHolder extends RecyclerView.ViewHolder {
        LinearLayout contatosLayout;
        TextView nomeContato;
        TextView telefoneContato;
        TextView emailContato;


        public AgendaViewHolder(View v) {
            super(v);
            contatosLayout = (LinearLayout) v.findViewById(R.id.contatos_layout);
            nomeContato = (TextView) v.findViewById(R.id.nomeContato);
            telefoneContato = (TextView) v.findViewById(R.id.telefoneContato);
            emailContato = (TextView) v.findViewById(R.id.emailContato);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    agendaInterface.onItemClick(getPosition());
                }
            });
        }
    }

    public AgendaAdapter(List<Contato> contatos, int rowLayout,
                         Context context, AdapterInterface agendaInterface) {
        this.contatos = contatos;
        this.rowLayout = rowLayout;
        this.context = context;
        this.agendaInterface = agendaInterface;
    }

    @Override
    public AgendaAdapter.AgendaViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new AgendaViewHolder(view);
    }


    @Override
    public void onBindViewHolder(AgendaViewHolder holder, final int position) {
        holder.nomeContato.setText(contatos.get(position).getNome());
        holder.telefoneContato.setText(contatos.get(position).getTelefone());
        holder.emailContato.setText(contatos.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        return contatos.size();
    }
}

