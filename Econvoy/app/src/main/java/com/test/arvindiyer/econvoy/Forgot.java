package com.test.arvindiyer.econvoy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.test.arvindiyer.econvoy.App.AppConfig;
import com.test.arvindiyer.econvoy.App.AppController;
import com.test.arvindiyer.econvoy.Helper.SQLiteHandler;
import com.test.arvindiyer.econvoy.Helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Forgot extends AppCompatActivity {

    private Toolbar mToolbar;
    private static final String TAG = Forgot.class.getSimpleName();
    private EditText inputEmail;
    private SessionManager session;
    private SQLiteHandler db;
    private android.support.v7.widget.AppCompatButton btnforgot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        TextView loginScreen = (TextView) findViewById(R.id.link_to_login);
        inputEmail = (EditText) findViewById(R.id.femail);
        btnforgot=(AppCompatButton) findViewById(R.id.btnForget);

        loginScreen.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // Closing registration screen
                // Switching to Login Screen/closing register screen
                finish();
            }
        });
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btnforgot.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view)
            {
                forgot();
            }
        });
    }

    /**
     * function to verify email details
     * */
    public void forgot()
    {
        if (!validate()) {
            onforgotFailed("Invalid Input");
            return;
        }
        btnforgot.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(Forgot.this, R.style.MyMaterialTheme_Base_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Checking...");
        progressDialog.show();

        final String email = inputEmail.getText().toString();
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onforgotSuccess(email);
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }
    /**
     * function to verify login details entered
     * */
    public boolean validate() {
        boolean valid = true;

        String email = inputEmail.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("Enter a valid email address");
            valid = false;
        } else {
            inputEmail.setError(null);
        }
        return valid;
    }
    //forgot sucesss
    public void onforgotSuccess(final String email) {
        // Tag used to cancel the request
        String tag_string_req = "req_forgot";
        final ProgressDialog progressDialog = new ProgressDialog(Forgot.this, R.style.MyMaterialTheme_Base_Dialog);
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_Forgot, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Forgot Response: " + response.toString());
                progressDialog.dismiss();

                try {
                    JSONObject jObj =new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Launch login activity
                        btnforgot.setEnabled(true);
                        Toast.makeText(getApplicationContext(), "Password Reset and mailed", Toast.LENGTH_LONG).show();
                        Intent i=new Intent(Forgot.this,Login.class);
                        startActivity(i);
                        finish();

                    } else {
                        // Error occurred in forgot method. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        onforgotFailed(errorMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Forgot Error: " + error.getMessage());
                onforgotFailed(error.getMessage());
                progressDialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to forgot url
                Map<String, String> params = new HashMap<String, String>();
                params.put("tag", "forgot");
                params.put("email",email);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);


    }
    public void onforgotFailed(String msg) {
        Toast.makeText(getBaseContext(),msg, Toast.LENGTH_LONG).show();
        btnforgot.setEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_forgot, menu);
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
