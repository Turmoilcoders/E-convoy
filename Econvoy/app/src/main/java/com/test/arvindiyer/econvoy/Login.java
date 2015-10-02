/*
Created by Arvind iyer
*/

package com.test.arvindiyer.econvoy;

/*packages import*/

import com.test.arvindiyer.econvoy.App.AppConfig;
import com.test.arvindiyer.econvoy.App.AppController;
import com.test.arvindiyer.econvoy.Helper.SQLiteHandler;
import com.test.arvindiyer.econvoy.Helper.SessionManager;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

public class Login extends AppCompatActivity {
    // LogCat tag
        private static final String TAG = Registration.class.getSimpleName();
        private EditText inputEmail;
        private EditText inputPassword;

        private SessionManager session;
        private SQLiteHandler db;
        private android.support.v7.widget.AppCompatButton btnLogin;
        Toolbar mToolbar;


    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (AppCompatButton) findViewById(R.id.btnLogin);
        TextView registerScreen = (TextView) findViewById(R.id.link_to_register);
        TextView forgotScreen = (TextView) findViewById(R.id.link_to_forgot);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(Login.this, Dashboard.class);
            startActivity(intent);
            finish();
        }

        // Listening to register new account link
            registerScreen.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    // Switching to Register screen
                    Intent i = new Intent(getApplicationContext(), Registration.class);
                    startActivity(i);
                    finish();
                }
            });

            // Listening to forgot link
            forgotScreen.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    // Switching to Forgot screen
                    Intent i = new Intent(getApplicationContext(), Forgot.class);
                    startActivity(i);
                }
            });

            mToolbar = (Toolbar) findViewById(R.id.toolbar);

            setSupportActionBar(mToolbar);
        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                login();
            }
        });
    }
    /**
     * function to verify login details
     * */

    public void login() {
        if (!validate()) {
            onLoginFailed("Invalid Input");
            return;
        }
        btnLogin.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(Login.this, R.style.MyMaterialTheme_Base_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        final String email = inputEmail.getText().toString();
        final String password = inputPassword.getText().toString();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess(email, password);
                         //onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 2000);

    }
    /**
     * function to verify login details entered
     * */
    public boolean validate() {
        boolean valid = true;

        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("Enter a valid email address");
            valid = false;
        } else {
            inputEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 20) {
            inputPassword.setError("Between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            inputPassword.setError(null);
        }

        return valid;
    }

    //login fails
    public void onLoginFailed(String msg ) {
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
        btnLogin.setEnabled(true);
    }
    public void onLoginSuccess(final String email, final String password) {
        String tag_string_req = "req_login";
        final ProgressDialog progressDialog = new ProgressDialog(Login.this, R.style.MyMaterialTheme_Base_Dialog);
        StringRequest strReq = new StringRequest(Method.POST, AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                progressDialog.dismiss();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        // Create login session
                        session.setLogin(true);
                        // Now store the user in sqlite
                        JSONObject user = jObj.getJSONObject("user");
                        String uid = jObj.getString("uid");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String created_at= user.getString("created_at");
                        String users = user.getString("type");

                        // Inserting row in users table
                        db.addUser(name,email,uid,created_at,users);

                        // Launch main activity
                        Intent intent = new Intent(Login.this, Login.class);
                        startActivity(intent);
                        btnLogin.setEnabled(true);
                        finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                       onLoginFailed(errorMsg);
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                onLoginFailed(error.getMessage());
                progressDialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "login");
                params.put("email", email);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }


    /**
     * function to verify login details in mysql db
     * */
    //private void Loggingin(final String email, final String password) {
        // Tag used to cancel the request

    //}



        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_login, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            //int id = item.getItemId();

            return super.onOptionsItemSelected(item);
        }
}

