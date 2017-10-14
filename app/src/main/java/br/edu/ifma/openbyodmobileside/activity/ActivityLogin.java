package br.edu.ifma.openbyodmobileside.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import br.edu.ifma.openbyodmobileside.R;
import br.edu.ifma.openbyodmobileside.request.UsuarioRequest;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Windows on 06/05/2017.
 */

public class ActivityLogin extends Activity {
    @Bind(R.id.user)
    EditText txtEmail;
    @Bind(R.id.password)
    EditText txtSenha;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        this.checaTokenUsuario();
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
        this.checaTokenUsuario();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        this.checaTokenUsuario();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        intent = new Intent(this, ActivityLogin.class);
        this.startActivity(intent);
    }

    @OnClick(R.id.btn_login)
    public void login(View view) {
        String email = txtEmail.getText().toString();
        String senha = txtSenha.getText().toString();

        txtEmail.setError(null);
        txtSenha.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(senha) && !checaSenhaValida(senha)) {
            txtSenha.setError(getString(R.string.senha_curta));
            focusView = txtSenha;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            txtEmail.setError(getString(R.string.campo_requerido));
            focusView = txtEmail;
            cancel = true;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txtEmail.setError(getString(R.string.email_invalido));
            focusView = txtEmail;
            cancel = true;
        }

        if (TextUtils.isEmpty(senha)) {
            txtSenha.setError(getString(R.string.campo_requerido));
            focusView = txtSenha;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();

        } else {
            // Call restAPI, method to authenticate in the application
            UsuarioRequest request = new UsuarioRequest(this);
            request.efetuaRequisicao(email, senha);
        }
    }

    private boolean checaSenhaValida(String password) {
        return password.length() > 7;
    }

    private void checaTokenUsuario() {
        String tokenUsuario = System.getProperty("token");
        if (tokenUsuario != null) {
            intent = new Intent(this, ActivityPrincipal.class);
            this.startActivity(intent);
        }
    }
}
