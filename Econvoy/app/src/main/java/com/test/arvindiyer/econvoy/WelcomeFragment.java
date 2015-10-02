package com.test.arvindiyer.econvoy;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.test.arvindiyer.econvoy.Helper.SQLiteHandler;

/**
 * Created by Arvindiyer on 11-09-2015.
 */
public class WelcomeFragment extends Fragment {
    private TextView t;
    private String uname="";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View inflateview = inflater.inflate(R.layout.welcome, container, false);
        t = (TextView) inflateview.findViewById(R.id.w2);
        Bundle  b = new Bundle();
        b = getArguments();
        uname= b.getString("Name");
        t.setText(uname);
        return inflateview;
    }

}
