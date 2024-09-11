package caps.android.mobilehrisapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextRegisterEmail, editTextRegisterPwd, editTextRegisterFullName,
            editTextRegisterMobile, editTextRegisterDoB;
    private ProgressBar progressBar;
    private RadioGroup radioGroupRegisterGender;
    private RadioButton radioButtonRegisterGenderSelected;
    private FirebaseAuth auth;
    private DatePickerDialog picker;
    private static final String TAG = "RegisterActivity";           //For error handling
    private SwipeRefreshLayout swipeContainer;
    private CheckBox checkboxAcceptToS;
    private int day, month, year;               //For DatePicker and Saving the date of register of user
    private ImageView imageViewDatePicker;
    private boolean toSAccepted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        swipeToRefresh();
        findViews();

        radioGroupRegisterGender.clearCheck();

        //Setting up DatePicker
        setUpDatePicker();

        //Accept Terms of Service
        checkboxAcceptToS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                toSAccepted = isChecked;
            }
        });

        //Show Hide Password using Eye Icon
        ImageView imageViewShowHidePwd = findViewById(R.id.imageView_show_hide_pwd);
        imageViewShowHidePwd.setImageResource(R.drawable.ic_show_pwd);
        imageViewShowHidePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextRegisterPwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    //If password is visible then Hide it
                    editTextRegisterPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //Change Icon
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_show_pwd);
                } else {
                    editTextRegisterPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
                }
            }
        });

        Button ButtonRegister = findViewById(R.id.button_register);
        ButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectedGenderId = radioGroupRegisterGender.getCheckedRadioButtonId();
                radioButtonRegisterGenderSelected = findViewById(selectedGenderId);

                String textEmail = editTextRegisterEmail.getText().toString();
                String textPassword = editTextRegisterPwd.getText().toString();
                String textFullName = editTextRegisterFullName.getText().toString();
                String textDoB = editTextRegisterDoB.getText().toString();
                String textMobile = editTextRegisterMobile.getText().toString();
                String textGender;          //Can't obtain the value before checking if any button was selected or not

                //Validate Mobile Number using Regular Expression
                String mobileRegex = "09[0-9]{9}";
                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile(mobileRegex);
                mobileMatcher = mobilePattern.matcher(textMobile);

                if (TextUtils.isEmpty(textFullName)){
                    Toast.makeText(RegisterActivity.this, "Please enter your full name", Toast.LENGTH_SHORT).show();
                    editTextRegisterFullName.setError("Full Name is required!");
                    editTextRegisterFullName.requestFocus();
                } else if (TextUtils.isEmpty(textEmail)){
                    Toast.makeText(RegisterActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();
                    editTextRegisterEmail.setError("Email is required!");
                    editTextRegisterEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(RegisterActivity.this, "Please re-enter email", Toast.LENGTH_SHORT).show();
                    editTextRegisterEmail.setError("Please provide valid Email!");
                    editTextRegisterEmail.requestFocus();
                } else if (TextUtils.isEmpty(textDoB)) {
                    Toast.makeText(RegisterActivity.this, "Please enter your date of birth", Toast.LENGTH_SHORT).show();
                    editTextRegisterDoB.setError("Date of Birth is required!");
                    editTextRegisterDoB.requestFocus();
                } else if (radioGroupRegisterGender.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(RegisterActivity.this, "Please select your gender", Toast.LENGTH_SHORT).show();
                    radioButtonRegisterGenderSelected.setError("Gender is required!");
                    radioButtonRegisterGenderSelected.requestFocus();
                } else if (TextUtils.isEmpty(textMobile)){
                    Toast.makeText(RegisterActivity.this, "Please enter mobile no.", Toast.LENGTH_SHORT).show();
                    editTextRegisterMobile.setError("Mobile No. is required!");
                    editTextRegisterMobile.requestFocus();
                } else if (textMobile.length() != 11){
                    Toast.makeText(RegisterActivity.this, "Please re-enter mobile no.", Toast.LENGTH_SHORT).show();
                    editTextRegisterMobile.setError("Mobile No. should be 11 digits!");
                    editTextRegisterMobile.requestFocus();
                } else if (!mobileMatcher.find()){
                    Toast.makeText(RegisterActivity.this, "Please re-enter mobile no.", Toast.LENGTH_SHORT).show();
                    editTextRegisterMobile.setError("Mobile No. is not valid!");
                    editTextRegisterMobile.requestFocus();
                } else if (TextUtils.isEmpty(textPassword)){
                    Toast.makeText(RegisterActivity.this, "Please enter password", Toast.LENGTH_SHORT).show();
                    editTextRegisterPwd.setError("Password is required!");
                    editTextRegisterPwd.requestFocus();
                } else if (textPassword.length() < 6){
                    Toast.makeText(RegisterActivity.this, "Password too short!", Toast.LENGTH_SHORT).show();
                    editTextRegisterPwd.setError("Password is too weak!");
                    editTextRegisterPwd.requestFocus();
                } else if(!toSAccepted) {
                    checkboxAcceptToS.setError("Kindly, accept Terms of Service and Privacy Policy");
                    checkboxAcceptToS.requestFocus();
                    Toast.makeText(RegisterActivity.this,
                            "Can't register until you accept our Terms of Service and Privacy Policy.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    textGender = radioButtonRegisterGenderSelected.getText().toString();    //Can't obtain the value before checking if any button was selected or not
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(textEmail, textPassword, textFullName, textDoB, textGender, textMobile);
                }
            }
        });
    }

    private void findViews() {
        editTextRegisterEmail = findViewById(R.id.editText_register_email);
        editTextRegisterPwd = findViewById(R.id.editText_register_password);
        editTextRegisterFullName = findViewById(R.id.editText_register_full_name);
        editTextRegisterDoB = findViewById(R.id.editText_register_dob);
        editTextRegisterMobile = findViewById(R.id.editText_register_mobile);
        progressBar = findViewById(R.id.progressBar);
        checkboxAcceptToS = findViewById(R.id.checkBox_terms_conditions);
        imageViewDatePicker = findViewById(R.id.imageView_date_picker);
        radioGroupRegisterGender = findViewById(R.id.radio_group_register_gender);
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

    private void setUpDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);

        imageViewDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // date picker dialog
                picker = new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        editTextRegisterDoB.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, year, month, day);
                picker.show();
            }
        });
    }

    //Register and Create User Credentials
    private void registerUser(String email, String pwd, String fullName, String doB, String gender, String mobile){
        auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    String userID = firebaseUser.getUid();

                    //Update Display Name of User
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(fullName).build();
                    firebaseUser.updateProfile(profileUpdates);

                    String registerDate = day + "/" + (month+1) + "/" + year;

                    //Enter User Data into the Firebase Realtime Database. Set up dependencies
                    ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(doB, gender, mobile, registerDate);

                    // Extracting User reference from Database for "Registered Users"
                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

                    referenceProfile.child(userID).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                //For Email Verification of User
                                firebaseUser.sendEmailVerification();
                                Toast.makeText(RegisterActivity.this, "Registration Successful. Please verify your email.", Toast.LENGTH_LONG).show();

                                //Open UserProfileActivity after User is created
                                Intent intent = new Intent(RegisterActivity.this, UserProfileActivity.class);

                                //To stop User from going back to RegisterActivity after User is created
                                intent.putExtra("finish", true); // if you are checking for this in your other Activities
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                try{
                                    throw task.getException();
                                } catch (Exception e){
                                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                } else {
                    try {
                        throw task.getException();
                    } catch(Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                    Toast.makeText(RegisterActivity.this, "User Creation Failed!", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
    //Creating ActionBarMenu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate menu items
        getMenuInflater().inflate(R.menu.signed_out_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(RegisterActivity.this);
        } else if (id == R.id.menu_refresh) {
            //Refresh Activity on pressing Refresh Button
            startActivity(getIntent());
            finish();
            overridePendingTransition(0, 0);
        } else if (id == R.id.menu_login) {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_register) {
            Intent intent = new Intent(RegisterActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_settings) {
            Toast.makeText(RegisterActivity.this, "menu_settings", Toast.LENGTH_LONG).show();
        } else if (id == R.id.menu_forgot_password) {
            Intent intent = new Intent(RegisterActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.menu_exit_app) {
            finishAffinity();
        } else {
            Toast.makeText(RegisterActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}