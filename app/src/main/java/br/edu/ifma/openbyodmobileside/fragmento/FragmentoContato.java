package br.edu.ifma.openbyodmobileside.fragmento;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import br.edu.ifma.openbyodmobileside.R;
import br.edu.ifma.openbyodmobileside.activity.ActivityPrincipal;
import br.edu.ifma.openbyodmobileside.modelo.Contato;
import br.edu.ifma.openbyodmobileside.request.ContatoRequest;
import br.edu.ifma.openbyodmobileside.util.Mascara;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Windows on 06/05/2017.
 */

public class FragmentoContato extends Fragment implements OnBackPressedListener {
    // text that holds the name of the contact
    @Bind(R.id.nomeContato)
    EditText txtNome;
    // text that holds the number of the contact
    @Bind(R.id.telefoneContato)
    EditText txtTelefone;
    @Bind(R.id.emailContato)
    EditText txtEmail;
    @Bind(R.id.empresaContato)
    EditText txtEmpresa;
    private ActivityPrincipal activityPrincipal;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentoContato() {
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
    public static FragmentoContato newInstance(String param1, String param2) {
        FragmentoContato fragment = new FragmentoContato();
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
        View view = inflater.inflate(R.layout.fragmento_contato, container, false);
        ButterKnife.bind(this, view);
        activityPrincipal = (ActivityPrincipal) getActivity();
        // Initialization of the Edit Texts
        txtTelefone.addTextChangedListener(Mascara.insert("(##)####-####", txtTelefone));
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
    public void onBackPressed() {
        activityPrincipal.carregaFragmento("Agenda", new FragmentoAgenda());
    }

    public void salvarContato() {
        String nomeContato = txtNome.getText().toString();
        String telefoneContato = txtTelefone.getText().toString();
        String email = txtEmail.getText().toString();
        String empresa = txtEmpresa.getText().toString();

        txtNome.setError(null);
        txtTelefone.setError(null);
        txtEmpresa.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(nomeContato)) {
            txtNome.setError(getString(R.string.campo_requerido));
            focusView = txtNome;
            cancel = true;
        }

        if (TextUtils.isEmpty(telefoneContato)) {
            txtTelefone.setError(getString(R.string.campo_requerido));
            focusView = txtTelefone;
            cancel = true;
        }

        if (telefoneContato.length() != 13) {
            txtTelefone.setError(getString(R.string.telefone_invalido));
            focusView = txtTelefone;
            cancel = true;
        }

        if (TextUtils.isEmpty(empresa)) {
            txtEmpresa.setError(getString(R.string.campo_requerido));
            focusView = txtEmpresa;
            cancel = true;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txtEmail.setError(getString(R.string.email_invalido));
            focusView = txtEmail;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();

        } else {
            Contato contato = new Contato(nomeContato, telefoneContato,
                    email, empresa);
            // Call restAPI, method to save a new contact in the database
            ActivityPrincipal activityPrincipal = (ActivityPrincipal) getActivity();
            ContatoRequest request = new ContatoRequest(activityPrincipal);
            request.adicionaContato(contato);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.adicionar_contato, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.salvar_contato) {
            salvarContato();
            return true;
        }
        return false;
    }
}

