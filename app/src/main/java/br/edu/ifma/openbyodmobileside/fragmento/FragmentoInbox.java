package br.edu.ifma.openbyodmobileside.fragmento;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import br.edu.ifma.openbyodmobileside.R;
import br.edu.ifma.openbyodmobileside.activity.ActivityPrincipal;
import br.edu.ifma.openbyodmobileside.request.InboxRequest;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Windows on 06/05/2017.
 */

public class FragmentoInbox extends Fragment {
    //constants to pass the values to the another activity
    public final static String EMAIL_DESTINATARIO = "br.edu.ifma.openbyodmobileside.fragmento.DESTINATARIO";
    public final static String EMAIL_REMETENTE = "br.edu.ifma.openbyodmobileside.fragmento.REMETENTE";
    public final static String EMAIL_ASSUNTO = "br.edu.ifma.openbyodmobileside.fragmento.ASSUNTO";
    public final static String EMAIL_CONTEUDO = "br.edu.ifma.openbyodmobileside.fragmento.CORPO";
    public final static String EMAIL_DATA = "br.edu.ifma.openbyodmobileside.fragmento.DATA";
    private InboxRequest request;
    private ActivityPrincipal activityPrincipal;
    @Bind(R.id.emails_recycler_view)
    RecyclerView recyclerView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentoInbox() {
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
    public static FragmentoInbox newInstance(String param1, String param2) {
        FragmentoInbox fragment = new FragmentoInbox();
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
        View view = inflater.inflate(R.layout.fragmento_inbox, container, false);
        ButterKnife.bind(this, view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        activityPrincipal = (ActivityPrincipal) getActivity();
        // Call restAPI, method to authenticate in the application
        request = new InboxRequest(activityPrincipal, recyclerView);
        request.getCaixaDeEntrada();
        return view;
    }

    @OnClick(R.id.fabEscreverEmail)
    public void carregaFragmentoCompose() {
        activityPrincipal.carregaFragmento("Novo E-mail", new FragmentoCompose());
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.inbox, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.atualizar_inbox) {
            // Call restAPI, method to authenticate in the application
            request = new InboxRequest(activityPrincipal, recyclerView);
            request.getCaixaDeEntrada();
            return true;
        }
        return false;
    }
}
