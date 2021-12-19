package com.example.magdadmilbat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.ar.core.AugmentedFace;
import com.google.ar.core.CameraConfig;
import com.google.ar.core.CameraConfigFilter;
import com.google.ar.core.Config;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;

import java.nio.FloatBuffer;

import java.nio.ShortBuffer;
import java.util.Collection;

public class CameraActivity extends AppCompatActivity {


    private static final int CAMERA_PERMISSION = 1;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);

        createSession();

        new Thread(new Runnable() {
            public void run() {
                while(true)
                {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ArCoreCamera();
                }
            }
        }).start();

//        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
//        startActivity(intent);

//        FloatBuffer faceMesh =  ArgumentedFace.getMeshVertics();

    }


    public void createSession() {
        // Create a new ARCore session.
        session = null;
        try {
            session = new Session(this);
        } catch (UnavailableArcoreNotInstalledException e) {
            e.printStackTrace();
        } catch (UnavailableApkTooOldException e) {
            e.printStackTrace();
        } catch (UnavailableSdkTooOldException e) {
            e.printStackTrace();
        } catch (UnavailableDeviceNotCompatibleException e) {
            e.printStackTrace();
        }

        CameraConfigFilter filter =
                new CameraConfigFilter(session).setFacingDirection(CameraConfig.FacingDirection.FRONT);
        CameraConfig cameraConfig = session.getSupportedCameraConfigs(filter).get(0);
        session.setCameraConfig(cameraConfig);

        Config config = new Config(session);
        config.setAugmentedFaceMode(Config.AugmentedFaceMode.MESH3D);
        session.configure(config);
    }


    public void ArCoreCamera()
    {

        Collection<AugmentedFace> faces = session.getAllTrackables(AugmentedFace.class);

        for (AugmentedFace face : faces) {
            if (face.getTrackingState() == TrackingState.TRACKING) {
                Log.v("FACE", "Tracking Face :)");
                // UVs and indices can be cached as they do not change during the session.
                FloatBuffer uvs = face.getMeshTextureCoordinates();
                ShortBuffer indices = face.getMeshTriangleIndices();
                // Center and region poses, mesh vertices, and normals are updated each frame.
                Pose facePos3 = face.getCenterPose();
                FloatBuffer faceVertices = face.getMeshVertices();
                FloatBuffer faceNormals = face.getMeshNormals();
                // Render the face using these values with OpenGL.
            }
        }
        Log.v("FACE", "-------------");

    }


}

