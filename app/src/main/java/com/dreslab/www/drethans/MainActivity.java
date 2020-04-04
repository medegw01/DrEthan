package com.dreslab.www.drethans;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ViewFlipper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ViewFlipper viewFlipper;
    Button instructor, student;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        instructor = (Button) findViewById(R.id.intructor);
        student =  (Button) findViewById(R.id.student);
        instructor.setOnClickListener(this);
        student.setOnClickListener(this);
        viewFlipper.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        viewFlipper.startFlipping();
        viewFlipper.setFlipInterval(3000);

        switch (v.getId()) {
            case R.id.student:
                startActivity(new Intent(this, studentActivity.class));
                break;
            case R.id.intructor:
                startActivity(new Intent(this, instructorActivity.class));
                break;
        }
    }
   }
