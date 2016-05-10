package com.rayerrt.attributionquery;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "AttributionQuery";
    private Context mContext;
    private static final String DB_NAME = "/data/data/com.rayerrt.attributionquery/databases/number.db";
    //private static final String DB_NAME = "/data/bbkcore/attribution/number.db";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        final DatabaseHelper mDbHelper = new DatabaseHelper(mContext, DB_NAME, null, 2);
        final TextView mTextView = (TextView) findViewById(R.id.textview);
        final EditText mEditText = (EditText) findViewById(R.id.edit_query);
        final AttributionQuery mQuery = new AttributionQuery();
        //mQuery.getNumberPrefixList(mDbHelper);
        mEditText.addTextChangedListener(new TextWatcher() {
            private String input;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                input = mEditText.getText().toString();
                String attribution = mQuery.queryAttributionByNumber(mDbHelper, input);
                Log.d(TAG, "attribution is " + attribution);
                if (null != attribution) {
                    mTextView.setText(attribution);
                } else {
                    mTextView.setText(mContext.getResources().getString(R.string.atttibution_unknown));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
