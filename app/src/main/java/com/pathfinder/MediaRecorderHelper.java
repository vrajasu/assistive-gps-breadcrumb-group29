package com.pathfinder;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;

/**
 * Created by vrajdelhivala on 3/26/18.
 */
//media recorder to help with audio recording and saving those files.
public class MediaRecorderHelper
{
    String file_path_and_name;
    MediaRecorder mRecorder;
    public MediaRecorderHelper(String file_path_and_name)
    {
        this.file_path_and_name=file_path_and_name;
    }
    public void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(file_path_and_name+".3gp");
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e("Recorder", "prepare() failed");
        }

        mRecorder.start();
    }

    public void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }
}
