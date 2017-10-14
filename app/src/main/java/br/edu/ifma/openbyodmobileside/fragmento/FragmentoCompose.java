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
import br.edu.ifma.openbyodmobileside.modelo.EmailUsuario;
import br.edu.ifma.openbyodmobileside.request.ComposeRequest;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Windows on 06/05/2017.
 */

public class FragmentoCompose extends Fragment implements OnBackPressedListener {
    // text that holds the inbox of the employee
    @Bind(R.id.remetente)
    EditText txtRemetente;
    // text that holds the inbox destination
    @Bind(R.id.destinatario)
    EditText txtDestinatario;
    // text that holds the inbox subject
    @Bind(R.id.assunto)
    EditText txtAssunto;
    // text that holds the inbox body
    @Bind(R.id.conteudo)
    EditText txtConteudo;
    private ActivityPrincipal activityPrincipal;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentoCompose() {
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
    public static FragmentoCompose newInstance(String param1, String param2) {
        FragmentoCompose fragment = new FragmentoCompose();
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
        View view = inflater.inflate(R.layout.fragmento_compose, container, false);
        ButterKnife.bind(this, view);
        activityPrincipal = (ActivityPrincipal) getActivity();
        // Initialization of the Edit Texts
        txtRemetente.setText(System.getProperty("email"));
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
    public void onBackPressed() {
        activityPrincipal.carregaFragmento("E-mail", new FragmentoInbox());
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

    public void enviarEmail() {
        // Prepares the inbox to be sent
        String destinatario = this.txtDestinatario.getText().toString();
        String remetente = this.txtRemetente.getText().toString();
        String assunto = this.txtAssunto.getText().toString();
        String conteudo = this.txtConteudo.getText().toString();

        txtDestinatario.setError(null);
        txtRemetente.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(remetente)) {
            txtRemetente.setError(getString(R.string.campo_requerido));
            focusView = txtRemetente;
            cancel = true;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(remetente).matches()) {
            txtRemetente.setError(getString(R.string.email_invalido));
            focusView = txtRemetente;
            cancel = true;
        }

        if (TextUtils.isEmpty(destinatario)) {
            txtDestinatario.setError(getString(R.string.campo_requerido));
            focusView = txtDestinatario;
            cancel = true;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(destinatario).matches()) {
            txtDestinatario.setError(getString(R.string.email_invalido));
            focusView = txtDestinatario;
            cancel = true;
        }

        if (TextUtils.isEmpty(assunto)) {
            txtAssunto.setError(getString(R.string.campo_requerido));
            focusView = txtAssunto;
            cancel = true;
        }

        if (TextUtils.isEmpty(conteudo)) {
            txtConteudo.setError(getString(R.string.campo_requerido));
            focusView = txtConteudo;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();

        } else {
            EmailUsuario emailUsuario = new EmailUsuario(destinatario,
                    remetente, assunto, conteudo);
            // Call restAPI, method to save a new contact in the database
            ActivityPrincipal activityPrincipal = (ActivityPrincipal) getActivity();
            ComposeRequest request = new ComposeRequest(activityPrincipal);
            request.envia(emailUsuario);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.enviar_email, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.enviar_email) {
            enviarEmail();
            return true;
        }
        return false;
    }
}
