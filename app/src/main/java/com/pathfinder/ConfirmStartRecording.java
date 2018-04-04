package com.pathfinder;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

//intermediate Actvity to confirm the user wants to start recording an activity
public class ConfirmStartRecording extends AppCompatActivity implements View.OnClickListener{

    Button btn_start,btn_cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_recording_confirmation_screen);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        btn_start = (Button)findViewById(R.id.btn_start_recording);
        btn_cancel=(Button)findViewById(R.id.btn_cancel);

        btn_start.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.btn_start_recording:
                Intent intent = new Intent(ConfirmStartRecording.this,RecordingActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_cancel:
                finish();
        }
    }
}
