package caps.android.mobilehrisapp;

import static caps.android.mobilehrisapp.R.*;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class ChangePasswordActivity extends AppCompatActivity {

    private static final String TAG = "ChangePasswordActivity";
    private FirebaseAuth authProfile;
    private EditText editTextPwdCurr, editTextPwdNew;
    private TextView textViewAuthenticated;
    private ImageView imageViewShowHideNewPwd, imageViewShowHideCurrPwd;
    private Button buttonChangePwd, buttonReAuthenticate;
    private ProgressBar progressBar;
    private String userPwdCurr;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Change Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swipeToRefresh();
        findViews();


        editTextPwdNew.setEnabled(false);         //Disable EditText for New Password
        imageViewShowHideNewPwd.setEnabled(false);
        buttonChangePwd.setEnabled(false);        //Make Change Pwd Button Un-clickable in the beginning till user is authenticated

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        showHidePassword();

        if (firebaseUser == null) {
            Toast.makeText(ChangePasswordActivity.this, "Something went wrong. User's detail not available at the moment!", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(ChangePasswordActivity.this, UserProfileActivity.class);
            startActivity(intent);
            finish();
        } else {
            reAuthenticateUser(firebaseUser);
        }
    }

    private void findViews() {
        editTextPwdNew = findViewById(R.id.editText_change_pwd_new);
        textViewAuthenticated = findViewById(R.id.textView_change_pwd_authenticated);
        progressBar = findViewById(R.id.progressBar);
        editTextPwdCurr = findViewById(R.id.editText_change_pwd_current);
        buttonReAuthenticate = findViewById(R.id.button_change_pwd_authenticate);
        buttonChangePwd = findViewById(R.id.button_change_pwd);
        imageViewShowHideNewPwd = findViewById(R.id.imageView_show_hide_new_pwd);
        imageViewShowHideCurrPwd = findViewById(R.id.imageView_show_hide_curr_pwd);
    }

    private void swipeToRefresh() {
        // Lookup the swipe container view
        swipeContainer = findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here. Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                startActivity(getIntent());
                finish();
                overridePendingTransition(0, 0);
                swipeContainer.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void showHidePassword() {
        //Show Hide Password using Eye Icon

        imageViewShowHideNewPwd.setImageResource(drawable.ic_show_pwd);
        imageViewShowHideNewPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextPwdNew.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    //If password is visible then Hide it
                    editTextPwdNew.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //Change Icon
                    imageViewShowHideNewPwd.setImageResource(drawable.ic_show_pwd);
                } else {
                    editTextPwdNew.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowHideNewPwd.setImageResource(drawable.ic_hide_pwd);
                }
            }
        });

        imageViewShowHideCurrPwd.setImageResource(drawable.ic_show_pwd);
        imageViewShowHideCurrPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextPwdCurr.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    //If password is visible then Hide it
                    editTextPwdCurr.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //Change Icon
                    imageViewShowHideCurrPwd.setImageResource(drawable.ic_show_pwd);
                } else {
                    editTextPwdCurr.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowHideCurrPwd.setImageResource(drawable.ic_hide_pwd);
                }
            }
        });
    }

    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        buttonReAuthenticate.setOnClickListener(v -> {
            //Obtain password for reAuthentication
            userPwdCurr = editTextPwdCurr.getText().toString();

            if (TextUtils.isEmpty(userPwdCurr)) {
                Toast.makeText(ChangePasswordActivity.this, "Password is needed!", Toast.LENGTH_LONG).show();
                editTextPwdCurr.setError("Please enter your Password for verification!");
                editTextPwdCurr.requestFocus();
            } else {
                progressBar.setVisibility(View.VISIBLE);                //starting progress bar for authentication

                AuthCredential credential = EmailAuthProvider.getCredential(Objects.requireNonNull(firebaseUser.getEmail()), userPwdCurr);

                firebaseUser.reauthenticate(credential).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);       //Stop progressbar if user is authenticated
                        editTextPwdCurr.setEnabled(false);    //Disable EditText for current password after User is Authenticated

                        editTextPwdNew.setEnabled(true);     //Enable EditText for New Password
                        imageViewShowHideNewPwd.setEnabled(true);
                        imageViewShowHideCurrPwd.setEnabled(false);
                        buttonReAuthenticate.setEnabled(false);     //Disable Authenticate Button after User is Authenticated

                        //Set TextView to show User is authenticated
                        textViewAuthenticated.setText(R.string.change_pwd_authenticated);

                        Toast.makeText(ChangePasswordActivity.this, "Password has been verified. You can change Password now.", Toast.LENGTH_LONG).show();

                        buttonChangePwd.setEnabled(true);
                        buttonChangePwd.setBackgroundTintList(ContextCompat.getColorStateList(ChangePasswordActivity.this,
                                R.color.dark_green));                  //Change color of Delete Profile Button
                        buttonChangePwd.setOnClickListener(v1 -> {
                            //Change Pwd
                            changePwd(firebaseUser);
                        });

                    } else {
                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                            Toast.makeText(ChangePasswordActivity.this, e.getMessage(),Toast.LENGTH_LONG).show();
                        }
                        progressBar.setVisibility(View.GONE);       //Stop progressbar if user is not authenticated
                    }
                });
            }
        });
    }

    private void changePwd(FirebaseUser firebaseUser) {
        String userPwdNew = editTextPwdNew.getText().toString();

        if (TextUtils.isEmpty(userPwdNew)) {
            Toast.makeText(ChangePasswordActivity.this, "New Password is needed!", Toast.LENGTH_LONG).show();
            editTextPwdNew.setError("Please enter your new Password!");
            editTextPwdNew.requestFocus();
        } else if (userPwdCurr.matches(userPwdNew)) {
            Toast.makeText(ChangePasswordActivity.this, "New Password cannot be same as old Password!", Toast.LENGTH_SHORT).show();
            editTextPwdNew.setError("Please enter a new Password!");
            editTextPwdNew.requestFocus();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            //Updating Email now
            firebaseUser.updatePassword(userPwdNew).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(ChangePasswordActivity.this, "Password has been changed!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ChangePasswordActivity.this, UserProfileActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        throw Objects.requireNonNull(task.getException());
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(ChangePasswordActivity.this, e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate menu items
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(ChangePasswordActivity.this);
        } else if (id == R.id.menu_refresh) {
            //Refresh Activity on pressing Refresh Button
            startActivity(getIntent());
            finish();
            overridePendingTransition(0, 0);
        } else if (id == R.id.menu_update_profile) {
            Intent intent = new Intent(ChangePasswordActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_update_email) {
            Intent intent = new Intent(ChangePasswordActivity.this, UpdateEmailActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_settings) {
            Toast.makeText(ChangePasswordActivity.this, "menu_settings coming soon!", Toast.LENGTH_LONG).show();
        } else if (id == R.id.menu_change_password) {
            Intent intent = new Intent(ChangePasswordActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_delete_profile) {
            Intent intent = new Intent(ChangePasswordActivity.this, DeleteProfileActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(ChangePasswordActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);

            //clear stack to prevent using back button
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(ChangePasswordActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}