package com.dreslab.www.drethans;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class studentActivity extends AppCompatActivity implements View.OnClickListener {
  Button r,w;
      @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_main);
        w = (Button) findViewById(R.id.w);
        w.setOnClickListener(this);
        r = (Button) findViewById(R.id.r);
        r.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.w:
                setContentView(R.layout.student_sad);
                break;
            case R.id.r:
                setContentView(R.layout.student_happy);
                break;   
                    }
    }
    public void back(View view)
    {
        setContentView(R.layout.student_main);
    }
}
