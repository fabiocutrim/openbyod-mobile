package br.edu.ifma.openbyodmobileside.fragmento;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.edu.ifma.openbyodmobileside.R;
import br.edu.ifma.openbyodmobileside.activity.ActivityPrincipal;
import br.edu.ifma.openbyodmobileside.request.AgendaRequest;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Windows on 06/05/2017.
 */

public class FragmentoAgenda extends Fragment {
    //constants to pass the values to the another activity
    public final static String ID_CONTATO = "br.edu.ifma.openbyodmobileside.fragmento.ID_CONTATO";
    public final static String NOME_CONTATO = "br.edu.ifma.openbyodmobileside.fragmento.NOME_CONTATO";
    public final static String TELEFONE_CONTATO = "br.edu.ifma.openbyodmobileside.fragmento.TELEFONE_CONTATO";
    public final static String EMAIL_CONTATO = "br.edu.ifma.openbyodmobileside.fragmento.EMAIL_CONTATO";
    public final static String EMPRESA_CONTATO = "br.edu.ifma.openbyodmobileside.fragmento.EMPRESA_CONTATO";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private ActivityPrincipal activityPrincipal;
    @Bind(R.id.contatos_recycler_view)
    RecyclerView recyclerView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentoAgenda() {
        // Required empty publickey constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MoviesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentoAgenda newInstance(String param1, String param2) {
        FragmentoAgenda fragment = new FragmentoAgenda();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragmento_agenda, container, false);
        ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        activityPrincipal = (ActivityPrincipal) getActivity();
        // Call restAPI, method to authenticate in the application
        AgendaRequest request = new AgendaRequest(activityPrincipal, recyclerView);
        request.listaContatos();

        return view;
    }

    @OnClick(R.id.fabAddContato)
    public void carregaFragmentoContato() {
        activityPrincipal.carregaFragmento("Novo Contato", new FragmentoContato());
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
