package br.edu.ifma.openbyodmobileside.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import br.edu.ifma.openbyodmobileside.R;
import br.edu.ifma.openbyodmobileside.fragmento.FragmentoAgenda;
import br.edu.ifma.openbyodmobileside.fragmento.FragmentoArquivos;
import br.edu.ifma.openbyodmobileside.fragmento.FragmentoHome;
import br.edu.ifma.openbyodmobileside.fragmento.FragmentoInbox;
import br.edu.ifma.openbyodmobileside.fragmento.FragmentoNotificacoes;
import br.edu.ifma.openbyodmobileside.fragmento.OnBackPressedListener;
import br.edu.ifma.openbyodmobileside.util.CircleTransform;

public class ActivityPrincipal extends AppCompatActivity {
    private TextView txtName, txtEmail;
    private NavigationView menuNavegacao;
    private DrawerLayout desenhoLayout;
    private View viewInfoUsuario;
    private ImageView imagemFundoPerfil, fotoPerfil;
    private Toolbar actionBar;
    private AlertDialog alerta;
    // urls to load navigation header background image
    // and profile image
    private static final String urlImagemFundoPerfil = System.getProperty("urlImagemFundoPerfil");
    private static final String urlFotoPerfil = System.getProperty("urlFotoPerfil");
    // index to identify current nav menu item
    public static int indiceItem = 0;
    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_AGENDA = "agenda";
    private static final String TAG_EMAIL = "e-mail";
    private static final String TAG_ARQUIVOS = "arquivos";
    private static final String TAG_NOTIFICACOES = "notificações";
    public static String TAG_ATUAL = TAG_HOME;
    // actionBar titles respected to selected nav menu item
    private String[] activityTitulos;
    // flag to load home fragment when user presses back key
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        actionBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(actionBar);

        mHandler = new Handler();
        desenhoLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        menuNavegacao = (NavigationView) findViewById(R.id.nav_view);
        // Navigation view header
        viewInfoUsuario = menuNavegacao.getHeaderView(0);
        txtName = (TextView) viewInfoUsuario.findViewById(R.id.nome);
        txtEmail = (TextView) viewInfoUsuario.findViewById(R.id.website);
        imagemFundoPerfil = (ImageView) viewInfoUsuario.findViewById(R.id.img_header_bg);
        fotoPerfil = (ImageView) viewInfoUsuario.findViewById(R.id.img_profile);
        // load actionBar titles from string resources
        activityTitulos = getResources().getStringArray(R.array.nav_item_activity_titles);
        // load nav menu header data
        carregaInfoUsuario();
        // initializing navigation menu
        inicializaMenuNavegacao();

        if (savedInstanceState == null) {
            indiceItem = 0;
            TAG_ATUAL = TAG_HOME;
            carregaFragmentoHome();
        }
    }
    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notificacoes action view (dot)
     */
    private void carregaInfoUsuario() {
        // name, website
        txtName.setText(System.getProperty("nome"));
        txtEmail.setText(System.getProperty("email"));
        // loading header background image
        Glide.with(this).load(urlImagemFundoPerfil)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imagemFundoPerfil);
        // Loading profile image
        Glide.with(this).load(urlFotoPerfil)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(fotoPerfil);
        // showing dot next to notificacoes label
        menuNavegacao.getMenu().getItem(4).setActionView(R.layout.menu_dot);
    }
    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void carregaFragmentoHome() {
        // selecting appropriate nav menu item
        selecionaItemNavegacao();
        // set actionBar title
        alteraTituloFragmento();
        // if user select the current navigation menu again, don't do anything
        // just close the navigation desenhoLayout
        if (getSupportFragmentManager().findFragmentByTag(TAG_ATUAL) != null) {
            desenhoLayout.closeDrawers();
            return;
        }
        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the home content by replacing fragments
                Fragment fragment = getFragmentoAtual();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, TAG_ATUAL);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };
        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }
        //Closing desenhoLayout on item click
        desenhoLayout.closeDrawers();
        // refresh actionBar menu
        invalidateOptionsMenu();
    }

    private Fragment getFragmentoAtual() {
        switch (indiceItem) {
            case 0:
                // home
                FragmentoHome fragmentoHome = new FragmentoHome();
                return fragmentoHome;
            case 1:
                // agenda fragment
                FragmentoAgenda fragmentoAgenda = new FragmentoAgenda();
                return fragmentoAgenda;
            case 2:
                // inbox fragment
                FragmentoInbox fragmentoEmail = new FragmentoInbox();
                return fragmentoEmail;
            case 3:
                // notificacoes fragment
                FragmentoArquivos fragmentoArquivos = new FragmentoArquivos();
                return fragmentoArquivos;
            case 4:
                // notificacoes fragment
                FragmentoNotificacoes fragmentoNotificacoes = new FragmentoNotificacoes();
                return fragmentoNotificacoes;
            default:
                return new FragmentoHome();
        }
    }

    private void alteraTituloFragmento() {
        getSupportActionBar().setTitle(activityTitulos[indiceItem]);
    }

    private void selecionaItemNavegacao() {
        menuNavegacao.getMenu().getItem(indiceItem).setChecked(true);
    }

    private void inicializaMenuNavegacao() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        menuNavegacao.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the home content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        indiceItem = 0;
                        TAG_ATUAL = TAG_HOME;
                        break;
                    case R.id.nav_agenda:
                        indiceItem = 1;
                        TAG_ATUAL = TAG_AGENDA;
                        break;
                    case R.id.nav_email:
                        indiceItem = 2;
                        TAG_ATUAL = TAG_EMAIL;
                        break;
                    case R.id.nav_arquivos:
                        indiceItem = 3;
                        TAG_ATUAL = TAG_ARQUIVOS;
                        break;
                    case R.id.nav_notifications:
                        indiceItem = 4;
                        TAG_ATUAL = TAG_NOTIFICACOES;
                        break;
                    case R.id.nav_about_us:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(ActivityPrincipal.this, ActivityQuemSomos.class));
                        desenhoLayout.closeDrawers();
                        return true;
                    case R.id.nav_privacy_policy:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(ActivityPrincipal.this, ActivityPoliticaDePrivacidade.class));
                        desenhoLayout.closeDrawers();
                        return true;
                    case R.id.nav_criptografia:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(ActivityPrincipal.this, ActivityCriptografia.class));
                        desenhoLayout.closeDrawers();
                        return true;
                    case R.id.nav_sair:
                        // Sai da aplicação
                        logout();
                        return true;
                    default:
                        indiceItem = 0;
                }
                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);
                carregaFragmentoHome();
                return true;
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle =
                new ActionBarDrawerToggle(this, desenhoLayout, actionBar, R.string.openDrawer, R.string.closeDrawer) {
                    @Override
                    public void onDrawerClosed(View drawerView) {
                        // Code here will be triggered once the desenhoLayout closes as we dont
                        // want anything to happen so we leave this blank
                        super.onDrawerClosed(drawerView);
                    }
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Code here will be triggered once the desenhoLayout open as we dont
                        // want anything to happen so we leave this blank
                        super.onDrawerOpened(drawerView);
                    }
                };
        //Setting the actionbarToggle to desenhoLayout layout
        desenhoLayout.setDrawerListener(actionBarDrawerToggle);
        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if (fragmentList != null) {
            //TODO: Perform your logic to pass back press here
            for(Fragment fragment : fragmentList){
                if(fragment instanceof OnBackPressedListener){
                    ((OnBackPressedListener)fragment).onBackPressed();
                }
            }
        }
    }

    public void logout() {
        //Cria o gerador do AlertDialog
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_NoActionBar);
        }
        //define o titulo
        builder.setTitle("Sair do Sistema");
        //define a mensagem
        builder.setMessage("Deseja realmente sair do sistema?");
        //define um botão como positivo
        builder.setPositiveButton("SIM", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                logoff();
            }
        });
        //define um botão como negativo.
        builder.setNegativeButton("NÂO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
            }
        });
        //cria o AlertDialog
        alerta = builder.create();
        //Exibe
        alerta.show();
    }

    public void logoff() {
        System.clearProperty("token");
        System.clearProperty("urlFotoPerfil");
        System.clearProperty("urlImagemFundoPerfil");
        System.clearProperty("email");
        System.clearProperty("nome");
        Intent intent = new Intent(this, ActivityLogin.class);
        this.startActivity(intent);
    }

    public void carregaFragmento(String titulo, final Fragment fragmento) {
        getSupportActionBar().setTitle(titulo);
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the home content by replacing fragments
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragmento);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };
        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }
        //Closing desenhoLayout on item click
        desenhoLayout.closeDrawers();
        invalidateOptionsMenu();
    }
}
