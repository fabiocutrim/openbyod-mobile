package br.edu.ifma.openbyodmobileside.fragmento;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.edu.ifma.openbyodmobileside.R;
import br.edu.ifma.openbyodmobileside.activity.ActivityPrincipal;
import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Windows on 19/07/2017.
 */

public class FragmentoVisualizacaoEmail extends Fragment implements OnBackPressedListener {
    @Bind(R.id.remetente_read_email)
    TextView txtRemetente;
    @Bind(R.id.destinatario_read_email)
    TextView txtDestinatario;
    @Bind(R.id.assunto_read_email)
    TextView txtAssunto;
    @Bind(R.id.corpo_read_email)
    TextView txtConteudo;
    @Bind(R.id.data_read_email)
    TextView txtData;
    private String remetente;
    private String destinatario;
    private String assunto;
    private String conteudo;
    private String data;
    private ActivityPrincipal activityPrincipal;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragmentoVisualizacaoEmail.OnFragmentInteractionListener mListener;

    public FragmentoVisualizacaoEmail() {
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
    public static FragmentoVisualizacaoEmail newInstance(String param1, String param2) {
        FragmentoVisualizacaoEmail fragment = new FragmentoVisualizacaoEmail();
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
        View view = inflater.inflate(R.layout.fragmento_visualizacao_email, container, false);
        ButterKnife.bind(this, view);
        activityPrincipal = (ActivityPrincipal) getActivity();
        savedInstanceState = getArguments();
        //Getting Messages from the intent that fired this activity
        remetente = savedInstanceState.getString(FragmentoInbox.EMAIL_REMETENTE);
        destinatario = savedInstanceState.getString(FragmentoInbox.EMAIL_DESTINATARIO);
        assunto = savedInstanceState.getString(FragmentoInbox.EMAIL_ASSUNTO);
        conteudo = savedInstanceState.getString(FragmentoInbox.EMAIL_CONTEUDO);
        data = savedInstanceState.getString(FragmentoInbox.EMAIL_DATA);
        preencheCamposLeitura();
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
        activityPrincipal.carregaFragmento("E-mail", new FragmentoInbox());
    }

    public void preencheCamposLeitura() {
        txtRemetente.setText("De: " + remetente);
        txtDestinatario.setText("Para: " + destinatario);
        txtData.setText("Data: " + data);

        if (assunto.equals("")) {
            txtAssunto.setText("Sem Assunto");
        } else {
            txtAssunto.setText("Assunto: " + assunto);
        }

        if (conteudo.equals("")) {
            txtConteudo.setText("\nSem conteúdo");
        } else {
            txtConteudo.setText("\n" + conteudo);
        }
        // Habilita o ScrollView para a visualização do conteúdo do E-mail
        txtConteudo.setMovementMethod(new ScrollingMovementMethod());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.responder_email, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.responder_email) {
            activityPrincipal.getSupportActionBar().setTitle("Responder E-mail");

            Runnable mPendingRunnable = new Runnable() {
                @Override
                public void run() {
                    remetente = txtRemetente.getText().toString().replace("De: ", "");
                    destinatario = txtDestinatario.getText().toString().replace("Para: ", "");
                    data = txtData.getText().toString().replace("Data: ", "");
                    assunto = txtAssunto.getText().toString().replace("Assunto: ", "");
                    conteudo = txtConteudo.getText().toString().replace("\n\nConteúdo: \n\n", "");

                    Fragment fragmentoRespostaEmail = new FragmentoRespostaEmail();
                    Bundle bundle = new Bundle();
                    bundle.putString(FragmentoInbox.EMAIL_REMETENTE, destinatario);
                    bundle.putString(FragmentoInbox.EMAIL_DESTINATARIO, remetente);
                    bundle.putString(FragmentoInbox.EMAIL_ASSUNTO, "(Re)" + assunto);
                    bundle.putString(FragmentoInbox.EMAIL_CONTEUDO, conteudo);
                    bundle.putString(FragmentoInbox.EMAIL_DATA, data);
                    fragmentoRespostaEmail.setArguments(bundle);
                    FragmentTransaction fragmentTransaction = activityPrincipal.
                            getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                            android.R.anim.fade_out);
                    fragmentTransaction.replace(R.id.frame, fragmentoRespostaEmail);
                    fragmentTransaction.commitAllowingStateLoss();
                }
            };
            // If mPendingRunnable is not null, then add to the message queue
            if (mPendingRunnable != null) {
                Handler mHandler = new Handler();
                mHandler.post(mPendingRunnable);
            }
            //Closing drawer on item click
            DrawerLayout drawer = (DrawerLayout) activityPrincipal.findViewById(R.id.drawer_layout);
            drawer.closeDrawers();
            return true;
        }
        return false;
    }
}
