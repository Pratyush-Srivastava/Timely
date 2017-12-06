package com.example.sridatta.timely.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sridatta.timely.R;
import com.example.sridatta.timely.objects.Faculty;
import com.example.sridatta.timely.objects.LectureSlot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class Register extends AppCompatActivity implements AdapterView.OnItemSelectedListener{


    private static final String TAG = "TAG" ;
    private TextInputEditText tiFirstName;
    private TextInputEditText tiLastName;
    private EditText etEmailId;
    private EditText etPassword;
    private EditText etPhoneNumber;
    private Spinner spDepartment;
    private Spinner spDesignation;
    private Button btnSignUp;
    private TextView title;

    private ConstraintLayout parentConstraint;
    private ConstraintLayout childConstraint;


    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseFirestore db;

    //get the user data
    private String firstName;
    private String lastName;
    private String emailID;
    private String password;
    private String phoneNumber;
    private String department;
    private String designation;

    private int flagLogin=0;
    private int flagUser=0;
    private int flagTimetable=0;


    HashMap<String, String> day ;
    HashMap<String, String> hour ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        //ui
        parentConstraint = (ConstraintLayout) findViewById(R.id.registerConstraint);
        childConstraint = (ConstraintLayout) findViewById(R.id.loadRegister);
        childConstraint.setVisibility(View.INVISIBLE);

        //registration
        title = (TextView) findViewById(R.id.tv_Registration);

        tiFirstName=(TextInputEditText) findViewById(R.id.ti_first);
        tiLastName=(TextInputEditText) findViewById(R.id.ti_last);
        etEmailId =(EditText) findViewById(R.id.et_email);
        etPassword =(EditText) findViewById(R.id.et_password);

        etPhoneNumber=(EditText) findViewById(R.id.et_phoneNumber);

        //spinner for department
        spDepartment = (Spinner) findViewById(R.id.sp_department);
        spDesignation= (Spinner) findViewById(R.id.sp_designation);

        //spinner click listener
        spDepartment.setOnItemSelectedListener(this);
        spDesignation.setOnItemSelectedListener(this);

        //spinner elements department and designation
        //creating adapters for the spinner

        Resources res = getResources();
        String[] departmentList = res.getStringArray(R.array.DepartmentArray);
        String[] designationList = res.getStringArray(R.array.DesignationArray);

        ArrayAdapter<String> adapterDepartment = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,departmentList);

        ArrayAdapter<String> adapterDesignation = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,designationList);

        // Drop down layout style - list view with radio button
        adapterDepartment.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterDesignation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spDesignation.setAdapter(adapterDesignation);
        spDepartment.setAdapter(adapterDepartment);

        btnSignUp = (Button) findViewById(R.id.btn_SignUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoad();
                signUp();

                //add new faculty object with the data




                //take to next layer of the app
            }
        });

        //auth

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser()!=null){

                    String userID = firebaseAuth.getCurrentUser().getUid();

                    flagLogin=1;
                    startActivity(new Intent(Register.this,Portal.class).putExtra("userID",userID));
                    goToNextActivity(userID);

                }

                else{
                    flagLogin=0;
                }
            }
        };


        // hashmap declarations for timetable
        day = new HashMap<>();
        hour = new HashMap<>();


        // Adding values to HashMap as ("keys", "values")
        day.put("1", "Mon");
        day.put("2", "Tue");
        day.put("3", "Wed");
        day.put("4", "Thu");
        day.put("5", "Fri");
        day.put("6", "Sat");
        day.put("7", "Sun");

        hour.put("1", "1");
        hour.put("2", "2");
        hour.put("3", "3");
        hour.put("4", "4");
        hour.put("5", "5");
        hour.put("6", "6");
        hour.put("7", "Ext");
    }


    private void goToNextActivity(String userID) {

        if(flagTimetable==49 && flagUser==1) {
            startActivity(new Intent(Register.this, Portal.class).putExtra("userID", userID));
        }
        else{

            Toast.makeText(Register.this,"Data not completely uploaded to cloud",Toast.LENGTH_SHORT).show();


        }
    }

    //auth

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    public void signUp() {

        //get the user data
        firstName= tiFirstName.getText().toString();
        lastName= tiLastName.getText().toString();
        emailID= etEmailId.getText().toString();
        password= etPassword.getText().toString();
        phoneNumber= etPhoneNumber.getText().toString();
        department= spDepartment.getSelectedItem().toString();
        designation= spDesignation.getSelectedItem().toString();

        // check if the fields are left blank
        if(TextUtils.isEmpty(firstName)||TextUtils.isEmpty(lastName)||
                TextUtils.isEmpty(emailID)||TextUtils.isEmpty(password)||TextUtils.isEmpty(phoneNumber)
                ||TextUtils.isEmpty(department)||TextUtils.isEmpty((designation))){
            Toast.makeText(Register.this,"Enter valid input to login",Toast.LENGTH_SHORT).show();
            stopLoad();
        }
        //if not blank
        else{

            //passing into auth function, and checking if the login is completed
            mAuth.signInWithEmailAndPassword(emailID,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    //checking if the login is completed successfully or not
                    if(!task.isSuccessful()){
//                        Toast.makeText(Login.this,"unable to login",Toast.LENGTH_SHORT).show();
                        Toast.makeText(Register.this,"unable to login",Toast.LENGTH_SHORT).show();
                        stopLoad();
                    }
                    else{

                        Toast.makeText(Register.this,"Registered",Toast.LENGTH_SHORT).show();
                        addNewUser();
//                        addTimeTable();


                    }
                }
            });
        }
    }

    public void addNewUser() {


        Faculty newFaculty = new Faculty(firstName,lastName,department,phoneNumber,emailID,designation);


        String userID = mAuth.getCurrentUser().getUid();

        db.collection("Faculty").document(userID)
                .set(newFaculty)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Faculty DocumentSnapshot successfully written!");
                        flagUser=1;


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        flagUser=0;

                    }
                });
    }

    private void addTimeTable() {

        String userID = mAuth.getCurrentUser().getUid();

        flagTimetable=0;
        for(int i=1;i<8;i++) {
            for(int j=1;j<8;j++) {
                LectureSlot lectureSlot =new LectureSlot();

                String a=Integer.toString(i);
                String b=Integer.toString(j);

                        //push with custom id
                String collectionName = day.get(a)+" "+hour.get(a);

                Log.d(TAG, day.get(a)+hour.get(a));


                db.collection("Faculty").document(userID).collection("TimeTable").document(collectionName)
                        .set(lectureSlot)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "Time Table DocumentSnapshot written with ID: ");
                                flagTimetable=flagTimetable+1;
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });

            }
        }

        goToNextActivity(userID);
    }

    //loading animation
    public void startLoad(){

        parentConstraint.setVisibility(View.INVISIBLE);
        childConstraint.setVisibility(View.VISIBLE);
    }

    public void stopLoad(){

        parentConstraint.setVisibility(View.VISIBLE);
        childConstraint.setVisibility(View.INVISIBLE);
    }

    //spinner
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        // On selecting a spinner item
        String item = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

}


