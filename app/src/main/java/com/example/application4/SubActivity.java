package com.example.application4;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class SubActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1002;
    private ImageView imageView;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        imageView = findViewById(R.id.imageView);
        Button buttonTakePicture = findViewById(R.id.button_take_picture);
        Button buttonPickImage = findViewById(R.id.button_pick_image);
        Button buttonBack = findViewById(R.id.button_back);

        // カメラ起動のためのランチャーを登録
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri imageUri = result.getData().getData();
                imageView.setImageURI(imageUri);
            }
        });

        // 画像ピッカーのためのランチャーを登録
        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                imageView.setImageURI(uri);
            }
        });

        // ボタンのクリックリスナー設定
        buttonTakePicture.setOnClickListener(v -> checkCameraPermission());

        buttonPickImage.setOnClickListener(v -> checkStoragePermission());

        buttonBack.setOnClickListener(v -> finish());


        // ViewPager2 と TabLayout を取得
        ViewPager2 viewPager = findViewById(R.id.my_view_pager);
        TabLayout tabLayout = findViewById(R.id.my_tab_layout);

        // ViewPager2 にアダプタを設定
        viewPager.setAdapter(new MyVPAdapter(this));

        // TabLayoutMediator を設定
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("メニュー");
                    break;
                case 1:
                    tab.setText("カレンダー");
                    break;
            }
        }).attach();
    }



    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            launchCamera();
        }
    }

    private void checkStoragePermission() {
        // ストレージパーミッションは実際には必要ない場合が多い
        // ただし、Android 11以降では、Scoped Storageが導入されているため、ファイルの選択が制限されることがあります
        // 画像ピッカーの機能には特に必要ない場合が多い
        launchImagePicker();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchCamera();
            } else {
                // パーミッションが拒否された場合の処理
            }
        }
        // ストレージのパーミッション結果は省略（必要ない場合が多い）
    }

    private void launchCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(cameraIntent);
    }

    private void launchImagePicker() {
        // 画像の選択を開始
        imagePickerLauncher.launch("image/*");
    }
}
