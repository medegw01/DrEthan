package com.dreslab.www.drethans;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class instructorActivity extends AppCompatActivity implements View.OnClickListener {
    final int PICK_IMAGE_REQUEST = 1;
    final int CAMERA_REQUEST = 0;
    Button gallery,send, correct, wrong;
    ImageView camera, photo;
    EditText instruction;

    private Bitmap bitmap;
    Uri filePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instructor_main);

        //buttons
        gallery = (Button) findViewById(R.id.gallery);
        send = (Button) findViewById(R.id.send);
        correct = (Button) findViewById(R.id.correct);
        wrong = (Button) findViewById(R.id.wrong);
        gallery.setOnClickListener(this);
        send.setOnClickListener(this);
        correct.setOnClickListener(this);
        wrong.setOnClickListener(this);

        instruction = (EditText) findViewById(R.id.instruction);

        camera = (ImageView) findViewById(R.id.camera);
        photo = (ImageView) findViewById(R.id.photo);
        }
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    public void Camera(View v){//takes phot with camera

        Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //File file = getfile();
        //camera_intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(camera_intent,CAMERA_REQUEST);
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 0);

    }
    private void send_manual(View v) {
        //send logic manual
    }
    private void send_solution(View v, String s) {
        //send logic solutions
    }

    @Override
   public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {

            filePath = data.getData();
            try {
             bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), filePath);
             photo.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (requestCode == CAMERA_REQUEST) {
            String path = "sdcard/camera_app/cam_image.jpg";
            bitmap = (Bitmap) data.getExtras().get(path);
            photo.setImageBitmap(bitmap);
            //photo.setImageDrawable(Drawable.createFromPath(path));
        }
        else {
            Toast.makeText(getApplicationContext(),
                    "Something Wrong while loading photos", Toast.LENGTH_SHORT).show();
        }
    }
        private File getfile() {//gets filepath

        File folder = new File("sdcard/camera_app");
        if (!folder.exists()) {
            folder.mkdir();
        }
        File image_file = new File(folder, "cam_image.jpg");
        return image_file;
    }

    public String getStringImage(Bitmap bmp){//converts Image to string(text)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    /* to use image and instruction at the end, use
    String instruction = instruction.getText().toString();
    String sendImage = getStringImage(bitmap);

    */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gallery:
                showFileChooser();
                break;
            case R.id.camera:
                Camera(v);
                break;
            case R.id.send:
                send_manual(v);
                break;
            case R.id.correct:
                send_solution(v, "right");
                break;
            case R.id.wrong:
                send_solution(v, "wrong");
                break;
        }
    }

   }
