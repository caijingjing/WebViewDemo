package com.example.webview;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener{
	private Button btn_jump_to;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_jump_to = (Button) findViewById(R.id.btn_jump_to);
        btn_jump_to.setOnClickListener(this);
        
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_jump_to:
			//String url = "http://IP:PORT/pic.do?method=mobileImageModel&caseNo=XXX&phoneNum=XXX&gps=XXX&type=XXX";
			String url="http://192.168.4.41:81/pic.do?method=mobileImageModel&caseNo=sss20161210001&phoneNum=18762651946&gps=111,222&type=1";
			Intent intent = new Intent(MainActivity.this,UI_WebInfo.class);
			intent.putExtra("WEB_TITLE", "实时采集");
			intent.putExtra("WEB_URL", url); 
			startActivity(intent);					
			break;
	
		}
		
		
		
		
		
	}
    
    
}
