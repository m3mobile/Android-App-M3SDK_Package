// 2015-10-06	v.1.0.1	전재영		최초 릴리즈
// 2016-02-15	v.1.0.2	전재영		바코드 리딩 시 코드 타입 알 수 있도록 수정
// 2016-09-28	v.1.2.0	전재영		Scanner Intent SDK 추가
// 2016-12-29	v.1.2.2  전재영		KeySDK 추가
// 2017-01-18	v.1.2.3	전재영		Scanner Intent SDK 에 Barcode Raw Data 받을 수 있도록 수정 - ScanEmul 1.3.0 부터 가능
// 2018-03-14   v.2.0.4 한재윤		BarcodeManager sendBarcode 수정 - SM15 통합스캔에뮬 변동사항 적용
// 2018-05-09	v.2.0.1 전재영		AIDL SDK 추가 ScanEmul 버전 2.0.2 이상 부터 사용 가능
// 2018-05-09	v.2.0.2 한재윤		1D AIDL SDK 추가
// 2018-07-23	v.2.0.3	전재영		N6600 2D SDK 추가
// 2018-10-08	v.2.0.4	한재윤		2D Scanner Intent SDK 에 Decode count 받을 수 있도록 수정 - ScanEmul 2.2.3 부터 가능
// 2019-02-07	v.2.0.5	한재윤		SM10LTE 2D AIDL SDK 추가
// 2019-03-28	v.2.1.1	한재윤		System SDK 추가
// 2019-06-12	v.2.1.2	한재윤		LongRange Scanner SDK 추가
// 2019-07-30	v.2.1.3	전재영		Scanner Open 체크 및 스캐너 사양 확인 추가
// 2019-09-10   v.2.1.4 윤정호      ImageCapture SDK 추가
// 2019-11-14   v.2.1.5 한재윤      one apk 를 지원하도록 수정
// 2019-11-20	v.2.1.6 전재영		Honeywell 예제 추가
// 2019-12-23	v.2.1.7	전재영		2D AIDL 을 SL10(K)에서 사용할 수 있도록 Package Name 추가
// 2020-03-02   v.2.1.8 윤정호      ImageCapture SDK 재추가
// 2020-03-03           윤정호       Preview ADIL 추가.

package com.m3.m3sdk_demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.m3.m3sdk_demo.R;
import com.m3.m3sdk_demo.Scanner2DAidlActivity;
import com.m3.m3sdk_demo.ScannerSM10LTE2DAidlActivity;

public class MainActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button btnScanner = (Button)findViewById(R.id.button_demo_scanner);
		Button btnScannerIntent = (Button)findViewById(R.id.button_demo_scanner_intent);
		Button btnScannerAidl = (Button)findViewById(R.id.button_demo_scanner_aidl);
		Button btn1DScannerAidl = (Button)findViewById(R.id.button_demo_scanner_1d_aidl);
		Button btnScannerHoney = (Button)findViewById(R.id.button_demo_scanner_honey);
		Button btnKey = (Button)findViewById(R.id.button_demo_key);
		Button btnSystemSDK = (Button) findViewById(R.id.button_demo_system_sdk);
		Button btnScannerLR = (Button) findViewById(R.id.button_demo_scanner_lr);
        Button btnImageCapture = (Button)findViewById(R.id.button_demo_image_capture);
        Button btnImagePreview = (Button)findViewById(R.id.button_demo_preview);
	
		btnScannerIntent.setOnClickListener(this);
		btnScanner.setOnClickListener(this);
		btnKey.setOnClickListener(this);
		btnScannerAidl.setOnClickListener(this);
		btn1DScannerAidl.setOnClickListener(this);
		btnScannerHoney.setOnClickListener(this);
		btnSystemSDK.setOnClickListener(this);
		btnScannerLR.setOnClickListener(this);
        btnImageCapture.setOnClickListener(this);
        btnImagePreview.setOnClickListener(this);

		if(Build.MODEL.equals("M3SM10")) {
			btnScannerIntent.setVisibility(View.GONE);
			btnKey.setVisibility(View.GONE);
			btnScannerAidl.setVisibility(View.GONE);
			btn1DScannerAidl.setVisibility(View.GONE);
			btnScannerHoney.setVisibility(View.GONE);
		}

		btnSystemSDK.setVisibility(View.INVISIBLE);
	}




	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id == R.id.button_demo_scanner){
			startActivity(new Intent(MainActivity.this, com.m3.m3sdk_demo.ScannerActivity.class));		
		}else if(id == R.id.button_demo_scanner_intent){
			startActivity(new Intent(MainActivity.this, com.m3.m3sdk_demo.ScannerIntentActivity.class));
		}else if(id == R.id.button_demo_key){
			startActivity(new Intent(MainActivity.this, com.m3.m3sdk_demo.KeyActivity.class));			
		}else if(id == R.id.button_demo_scanner_aidl){
			if(Build.MODEL.equals("M3SM10_LTE"))
				startActivity(new Intent(MainActivity.this, ScannerSM10LTE2DAidlActivity.class));
			else
				startActivity(new Intent(MainActivity.this, Scanner2DAidlActivity.class));
		}else if(id == R.id.button_demo_scanner_1d_aidl){
			startActivity(new Intent(MainActivity.this, com.m3.m3sdk_demo.Scanner1DAidlActivity.class));
		}else if(id == R.id.button_demo_scanner_honey){
			startActivity(new Intent(MainActivity.this, com.m3.m3sdk_demo.ScanHoneyActivity.class));
		}else if(id == R.id.button_demo_system_sdk) {
			startActivity(new Intent(MainActivity.this, com.m3.m3sdk_demo.SystemSDKActivity.class));
		}else if(id == R.id.button_demo_scanner_lr) {
			startActivity(new Intent(MainActivity.this, com.m3.m3sdk_demo.LRScannerActivity.class));
		}else if(id==R.id.button_demo_image_capture){
            startActivity(new Intent(MainActivity.this, com.m3.m3sdk_demo.ImageCaptureIntentActivity.class));
        }else if(id==R.id.button_demo_preview){
		    startActivity(new Intent(MainActivity.this, com.m3.m3sdk_demo.PreviewAidlActivity.class));
        }

	}
}
