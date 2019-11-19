package es.upm.miw.smarthome;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import es.upm.miw.smarthome.es.upm.miw.smarthome.weather.Weather;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private RecordAdapter recordAdapter;
    private ListView recordListView;

    private static final String LOG_TAG = "SmartHome";
    public static final String ANONYMOUS = "anonymous";
    private static final String API_BASE_URL = "http://api.openweathermap.org";
    private TextView tvRespuesta;
    private WeatherRESTAPIService apiService;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private String mUsername;
    private static final int RC_SIGN_IN = 2018;

    // btb Firebase database variables
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseStorage mFirebaseStorage;
    private DatabaseReference mMessagesDatabaseReference;
    private ChildEventListener mChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvRespuesta = (TextView) findViewById(R.id.tvRespuesta);
        findViewById(R.id.logoutButton).setOnClickListener(this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(WeatherRESTAPIService.class);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("weather");


        recordListView = (ListView) findViewById(R.id.lvListadoHistorico);
        List<Weather> weatherList = new ArrayList<>();
        recordAdapter = new RecordAdapter(this, weatherList, R.layout.record_list);
        recordListView.setAdapter(recordAdapter);

        mChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                // Deserialize data from DB into our Weather object
                Weather weather = dataSnapshot.getValue(Weather.class);
                recordAdapter.add(weather);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        mMessagesDatabaseReference.addChildEventListener(mChildEventListener);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // user is signed in
                    CharSequence username = user.getDisplayName();
                    Toast.makeText(MainActivity.this, getString(R.string.firebase_user_fmt, username), Toast.LENGTH_LONG).show();
                    onSignedInInitialize(user.getDisplayName());
                } else {
                    // user is signed out
                    onSignedOutCleanup();
                    startActivityForResult(
                            // Get an instance of AuthUI based on the default app
                            AuthUI.getInstance().
                                    createSignInIntentBuilder().
                                    setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build()
                                    )).
                                    setIsSmartLockEnabled(!BuildConfig.DEBUG /* credentials */, true /* hints */).
                                    build(),
                            RC_SIGN_IN
                    );
                }
            }
        };
    }

    public void obtenerInfoCiudad(View v) {

        tvRespuesta.setText("");

        Call<Weather> call_async = apiService.getCityByName();

        call_async.enqueue(new Callback<Weather>() {

            @Override
            public void onResponse(Call<Weather> call, Response<Weather> response) {
                Weather weather = response.body();
                if (null != weather) {
                    tvRespuesta.append("Smart Home  " + mUsername + "\n\n");
                    SimpleDateFormat sdf = new SimpleDateFormat();
                    sdf.applyPattern("yyyy-MM-dd HH:mm:ss a");
                    Date date = new Date();
                    tvRespuesta.append(sdf.format(date)+"\n\n");
                    tvRespuesta.append("Wind Speed: "+weather.getWind().getSpeed().toString()+"\n\n");
//                    tvRespuesta.append("Rain: "+weather.getRain()+"\n\n");

                    mMessagesDatabaseReference.push().setValue(weather);
                } else {
                    tvRespuesta.setText("No se han recuperado datos");
                }
            }

            @Override
            public void onFailure(Call<Weather> call, Throwable t) {
                Toast.makeText(
                        getApplicationContext(),
                        "ERROR: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
                Log.e(LOG_TAG, t.getMessage());
            }
        });
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.opciones_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.opcSignOut:
                //sign out
                AuthUI.getInstance().signOut(this);
                return true;
            case R.id.opcVerHistorico:
                startActivity(new Intent(this, Record.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

        if(mAuthStateListener!=null){
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        dettachDatabseReadListener();

        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    private void onSignedInInitialize(String username){
        mUsername = username;
        attachDatabseReadListener();
    }

    private void onSignedOutCleanup(){
        mUsername = ANONYMOUS;
    }

    private void attachDatabseReadListener(){
        if(mChildEventListener==null){
            // btb this same code is executed onCreate
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {}
            };
            mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void dettachDatabseReadListener(){
        if(mChildEventListener!=null){
            mMessagesDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, R.string.signed_in, Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, R.string.signed_cancelled, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onClick(View v) {
        mFirebaseAuth.signOut();
    }
}
