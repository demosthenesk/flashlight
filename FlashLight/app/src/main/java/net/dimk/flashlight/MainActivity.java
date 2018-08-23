package net.dimk.flashlight;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    ToggleButton toggleButton;
    Camera camera;
    Boolean hasFlashLight;
    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toggleButton = (ToggleButton) findViewById(R.id.onOffFlashlight);
        Context context = getApplicationContext();
        toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.switch_off));

        if (checkPermission()) {
            toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    try {
                        hasFlashLight = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

                        if (hasFlashLight) {

                            if (checked) {

                                camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                                Camera.Parameters parameters = camera.getParameters();
                                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                                camera.setParameters(parameters);
                                SurfaceTexture mDummy = new SurfaceTexture(1); // any int argument will do
                                camera.setPreviewTexture(mDummy);
                                camera.startPreview();
                                Context context = getApplicationContext();
                                toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.switch_on));
                                Toast.makeText(getApplicationContext(), "On", Toast.LENGTH_LONG).show();

                            } else {

                                camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                                Camera.Parameters parameters = camera.getParameters();
                                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                                camera.setParameters(parameters);
                                camera.stopPreview();
                                camera.release();
                                Context context = getApplicationContext();
                                toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.switch_off));
                                Toast.makeText(getApplicationContext(), "Off", Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(getApplicationContext(), "Can't get flash light!", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
//                    Toast.makeText(getApplicationContext(), hasFlashLight.toString(), Toast.LENGTH_LONG).show();
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            });

        } else {

            requestPermission();
        }


    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();

                    // main logic
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            showMessageOKCancel("You need to allow access permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermission();
                                            }
                                        }
                                    });
                        }
                    }

                }
                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

}
