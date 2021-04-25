package com.techone.keeper.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.techone.keeper.Constants;
import com.techone.keeper.Dashboard;
import com.techone.keeper.R;

import java.util.HashMap;
import java.util.Map;

public class LogIn extends AppCompatActivity {


    private static final int RC_SIGN_IN = 123 ;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    TextView tandc;

    String uid;

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null)
        {
//            Intent i = new Intent(getApplicationContext(),  MainActivity.class);
//            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        mAuth = FirebaseAuth.getInstance();
        createRequest();
        findViewById(R.id.signinbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        tandc = (TextView) findViewById(R.id.tandc);
        SpannableString spannableString = new SpannableString("by signing in, you are accepting all the Terms & Conditions to use this app.");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {
                Toast.makeText(LogIn.this, "We will be using all the google account details to recognise you. And all the data is secured with us.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.parseColor("#7303c0"));
            }
        };

        spannableString.setSpan(clickableSpan,41,59, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tandc.setText(spannableString);
        tandc.setMovementMethod(LinkMovementMethod.getInstance());


        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorwhite)); //status bar or the time bar at the top
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(null);
                    }
                });
    }
    private void updateUI(Object o) {
    }
    private void createRequest() {
        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        signOut();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                            if(isNew) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                uid = user.getUid();
                                FirebaseFirestore db= FirebaseFirestore.getInstance();
                                Map<String,String> items=new HashMap<>();
                                items.put("name",user.getDisplayName());
                                items.put("mail",user.getEmail());
                                items.put("PhoneNo.",user.getPhoneNumber());
                                db.collection(Constants.COLLECTION_REF).document(uid).set(items).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Intent i = new Intent(getApplicationContext(), Dashboard.class);
                                        startActivity(i);
                                    }
                                });




                            } else {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if(user.getDisplayName() != null) {
                                    Toast.makeText(LogIn.this, "Welcome Back "+user.getDisplayName() + " !", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(LogIn.this, "Welcome Back to Keeper", Toast.LENGTH_SHORT).show();
                                }

                                Intent i = new Intent(getApplicationContext(), Dashboard.class);
                                startActivity(i);
                            }
                            // Sign in success, update UI with the signed-in user's information


                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LogIn.this, "error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}