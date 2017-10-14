package br.edu.ifma.openbyodmobileside.fragmento;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import br.edu.ifma.openbyodmobileside.R;
import br.edu.ifma.openbyodmobileside.activity.ActivityPrincipal;
import br.edu.ifma.openbyodmobileside.adapter.AdapterInterface;
import br.edu.ifma.openbyodmobileside.adapter.HomeAdapter;
import br.edu.ifma.openbyodmobileside.modelo.ItemHome;
import butterknife.Bind;
import butterknife.BindDrawable;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentoHome#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentoHome extends Fragment implements AdapterInterface {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ActivityPrincipal activityPrincipal;
    private List<ItemHome> itens;
    @Bind(R.id.home_recycler_view)
    RecyclerView recyclerView;
    @BindDrawable(R.drawable.agenda)
    Drawable imagemAgenda;
    @BindDrawable(R.drawable.contato)
    Drawable imagemContato;
    @BindDrawable(R.drawable.email_2)
    Drawable imagemInbox;
    @BindDrawable(R.drawable.escrever_email)
    Drawable imagemCompose;
    @BindDrawable(R.drawable.folder_medium)
    Drawable imagemArquivos;
    @BindDrawable(R.drawable.notificacoes)
    Drawable imagemNotificacoes;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentoHome() {
        // Required empty publickey constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentoHome.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentoHome newInstance(String param1, String param2) {
        FragmentoHome fragment = new FragmentoHome();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
    }

    private void adiconaItens(View view) {
        itens = new ArrayList<>();
        // Drawable imagemAgenda = view.getResources().getDrawable(R.drawable.agenda);
        ItemHome itemAgenda = new ItemHome("Agenda", "Visualize os seus contatos",
                imagemAgenda);
        ItemHome itemContato = new ItemHome("Novo Contato", "Adicione um novo contato",
                imagemContato);
        ItemHome itemInbox = new ItemHome("Caixa de Entrada", "Visualize os seus e-mails",
                imagemInbox);
        ItemHome itemCompose = new ItemHome("Enviar E-mail", "Escreva um novo-email",
                imagemCompose);
        ItemHome itemArquivos = new ItemHome("Arquivos", "Salve e baixe arquivos diversos",
                imagemArquivos);
        ItemHome itemNotificacoes = new ItemHome("Notificações", "Verifique suas notificações",
                imagemNotificacoes);
        itens.add(itemAgenda);
        itens.add(itemContato);
        itens.add(itemInbox);
        itens.add(itemCompose);
        itens.add(itemArquivos);
        itens.add(itemNotificacoes);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.fragmento_home, container, false);
        ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        AdapterInterface homeInterface = this;
        activityPrincipal = (ActivityPrincipal) getActivity();
        // Adiciona os itens à lista
        adiconaItens(view);
        // Monta o Adapter
        recyclerView.setAdapter(new HomeAdapter
                (itens, R.layout.list_item_home,
                        activityPrincipal, homeInterface));
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(int posicao) {
        ItemHome item = itens.get(posicao);
        if (item.getTitulo().equals("Agenda")) {
            activityPrincipal.carregaFragmento("Agenda", new FragmentoAgenda());
        }
        if (item.getTitulo().equals("Novo Contato")) {
            activityPrincipal.carregaFragmento("Contato", new FragmentoContato());
        }
        if (item.getTitulo().equals("Caixa de Entrada")) {
            activityPrincipal.carregaFragmento("E-mail", new FragmentoInbox());
        }
        if (item.getTitulo().equals("Enviar E-mail")) {
            activityPrincipal.carregaFragmento("Escrever E-mail", new FragmentoCompose());
        }
        if (item.getTitulo().equals("Arquivos")) {
            activityPrincipal.carregaFragmento("Arquivos", new FragmentoArquivos());
        }
        if (item.getTitulo().equals("Notificações")) {
            activityPrincipal.carregaFragmento("Notificações", new FragmentoNotificacoes());
        }
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
