/*
Created by Arvind iyer
*/

package com.test.arvindiyer.econvoy;
/*
Importing packages
*/

import com.test.arvindiyer.econvoy.App.AppController;
import com.test.arvindiyer.econvoy.App.AppConfig;
import com.test.arvindiyer.econvoy.Helper.SessionManager;
import com.test.arvindiyer.econvoy.Helper.SQLiteHandler;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.String;
import android.support.v7.app.AppCompatActivity;
import java.lang.Character;

public class Registration extends AppCompatActivity {

    private static final String TAG = Registration.class.getSimpleName();
    private AppCompatButton btnRegister;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText confirmPassword;
    private RadioButton btn;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private RadioGroup type;
    private String s;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        inputFullName = (EditText) findViewById(R.id.reg_fullname);
        inputEmail = (EditText) findViewById(R.id.reg_email);
        inputPassword = (EditText) findViewById(R.id.reg_password);
        confirmPassword=(EditText) findViewById(R.id.reg_password1);
        btnRegister = (AppCompatButton) findViewById(R.id.btnRegister);
        type=(RadioGroup) findViewById(R.id.reg_type);
        TextView loginScreen = (TextView) findViewById(R.id.link_to_login);


        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(Registration.this, Dashboard.class);
            startActivity(intent);
            finish();
        }

        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                signup();
            }
        });

        // Listening to Login Screen link
        loginScreen.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Switching to Register screen
                Intent i = new Intent(getApplicationContext(), Login.class);
                startActivity(i);
                finish();
            }
        });

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    //radio button
    public void radio(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        int selectedId = type.getCheckedRadioButtonId();
        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radioButton1:
                if (checked)
                    btn = (RadioButton) findViewById(selectedId);
                break;
            case R.id.radioButton2:
                if (checked)
                    btn = (RadioButton) findViewById(selectedId);
                break;
            case R.id.radioButton3:
                if (checked)
                    btn = (RadioButton) findViewById(selectedId);
                break;
            case R.id.radioButton4:
                if (checked)
                    btn = (RadioButton) findViewById(selectedId);
                break;
        }
    }


    void signup()
    {
        if (!validate()) {
            onSignupFailed("Invalid Inputs" );
            return;
        }

        btnRegister.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(Registration.this,
                R.style.MyMaterialTheme_Base_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        final String name = inputFullName.getText().toString();
        final String email = inputEmail.getText().toString();
        final String password = inputPassword.getText().toString();
        String cpassword=confirmPassword.getText().toString();
        final String user=btn.getText().toString();


        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess(name,email,user,password);
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }
    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email,users, password) to register url
     * */


    public void onSignupSuccess(final String name, final String email,final String users, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";
        final ProgressDialog progressDialog = new ProgressDialog(Registration.this, R.style.MyMaterialTheme_Base_Dialog);
        final StringRequest strReq = new StringRequest(Method.POST, AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                progressDialog.dismiss();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Launch login activity
                        btnRegister.setEnabled(true);
                        Toast.makeText(getApplicationContext(),
                                "Registred", Toast.LENGTH_LONG).show();
                        Intent i=new Intent(Registration.this,Login.class);
                        startActivity(i);
                        finish();

                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                      onSignupFailed(errorMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                onSignupFailed(error.getMessage());
                progressDialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "register");
                params.put("name", name);
                params.put("email", email);
                params.put("users", users);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }
    public void onSignupFailed(String msg) {
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
        btnRegister.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = inputFullName.getText().toString();
        String email = inputEmail.getText().toString();
        String passwd = inputPassword.getText().toString();
        String cpasswd = confirmPassword.getText().toString();


        if (name.isEmpty() || name.length() < 3 || isNumericString(name)){
            inputFullName.setError("At least 3 characters and not numbers");
            valid = false;
        } else {
            inputFullName.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("Enter a valid email address");
            valid = false;
        } else {
            inputEmail.setError(null);
        }

        if (passwd.isEmpty() || passwd.length() < 4 || passwd.length() > 20) {
            inputPassword.setError("between 4 and 20 alphanumeric characters");
            valid = false;
        } else {
            inputPassword.setError(null);
        }

        if (cpasswd.isEmpty() || cpasswd.length() < 4 || cpasswd.length() > 20) {
            confirmPassword.setError("Between 4 and 20 alphanumeric characters");
            valid = false;
        } else {
            confirmPassword.setError(null);
        }
        if(!passwd.equals(cpasswd))
        {
            inputPassword.setError("Password not matching");
        }else {
            inputPassword.setError(null);
        }
        if(!cpasswd.equals(passwd))
        {
            confirmPassword.setError("Password not matching");
        }else {
            confirmPassword.setError(null);
        }

        return valid;
    }
    public static boolean isNumericString(String input) {
        boolean result = false;

        if(input != null && input.length() > 0) {
            char[] charArray = input.toCharArray();

            for(char c : charArray) {
                if(c >= '0' && c <= '9') {
                    // it is a digit
                    result = true;
                } else {
                    result = false;
                    break;
                }
            }
        }

        return result;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }
}
