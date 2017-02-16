package com.example.webview;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpClientConnection;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.handkoo.smartvideophonev520.threads.HK_Web_URL_Thread;
import com.handkoo.smartvideophonev520.utils.HK_File_util;
import com.handkoo.smartvideophonev520.utils.HK_LOG;
import com.handkoo.smartvideophonev520.utils.HK_SP_Base_Util;
import com.handkoo.smartvideophonev520.xml.HK_XmlUtils;

/********************************************************
 * 日期： 2015年12月1日 文件名： UI_WebInfo.java 类名： SmartVideoPhoneV5_20151113 作者：
 * Sun_skin 标记： 无 作用： TODO
 ********************************************************/
public class UI_WebInfo extends Activity {
	private WebView m_web_detail;
	private String m_str_title = "";
	private String m_str_ulr = "";
	private ProgressDialog pd = null;
	private MediaPlayer mediaPlayer;
	private Uri uri;
	String type;
	HttpResponse response;
//	private Handler handler=new Handler(){
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//			case 100:
//				Log.i("UI_WEB", "dddd");
//	        	
//	        	Intent intentPhote = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//				File out = new File(getPhotopath());
//				Uri uri = Uri.fromFile(out);
//				//
//				intentPhote.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//				intentPhote.putExtra(MediaStore.Images.Media.ORIENTATION, 180);
//				startActivityForResult(intentPhote, 2000);
//				break;
//
//			default:
//				break;
//			}
//		};
//	};
//	private SwipeRefreshLayout swipeLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.ui_webinfo);
		
		m_str_title = getIntent().getStringExtra("WEB_TITLE");
		m_str_ulr = getIntent().getStringExtra("WEB_URL");
		HK_LOG.getInstance().mLogInfo("UI_WebInfo",
				m_str_title + "-" + m_str_ulr);
		mInitTitle();
		mInitUI();
	}

	private void mInitTitle() {
		// 初始化标题
		TextView textView = (TextView) findViewById(R.id.txt);
		textView.setText(m_str_title);
		Button back = (Button) findViewById(R.id.leftBtn);
		back.setVisibility(View.VISIBLE);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(m_web_detail != null && m_web_detail.getUrl() != null)
				{	
					if(m_str_ulr.startsWith(m_web_detail.getUrl()))
					{
						finish();
						return;
					}
					HK_LOG.getInstance().mLogInfo("OnBack", m_web_detail.getUrl());
					m_web_detail.goBack();
				}else
				{
					finish();
				}
			}
		});
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(m_web_detail != null && m_web_detail.getUrl() != null)
		{	
//			String url = m_web_detail.getUrl().trim();
			if(m_str_ulr.startsWith(m_web_detail.getUrl()))
			{
				finish();
				return;
			}
			HK_LOG.getInstance().mLogInfo("OnBack", m_web_detail.getUrl());
			HK_LOG.getInstance().mLogInfo("OnBack", m_str_ulr);
			m_web_detail.goBack();
		}else
		{
			finish();
		}
	}

	public void mInitUI() {
		m_web_detail = (WebView) findViewById(R.id.webview_detail);
		m_web_detail.getSettings().setJavaScriptEnabled(true);
		m_web_detail.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

		m_web_detail.addJavascriptInterface(new DemoJavaScriptInterface(), "handkoo");//增加接口
		 
		m_web_detail.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				if (pd != null) {
					pd.dismiss();
					pd = null;
					HK_LOG.getInstance().mLogInfo("onPageFinished",
							"hide the page");

				}
				HK_LOG.getInstance().mLogInfo("onPageFinished", "URL:" + url);
				super.onPageFinished(view, url);

			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				// TODO Auto-generated method stub
				if (pd != null) {
					pd.dismiss();
					pd = null;
					HK_LOG.getInstance().mLogInfo("onReceivedError",
							"hide the page");
				}
				mShowMsg("数据加载失败");
				HK_LOG.getInstance().mLogInfo("onReceivedError",
						"URL:" + failingUrl);
				super.onReceivedError(view, errorCode, description, failingUrl);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// TODO Auto-generated method stub
				if (pd != null) {
					pd.dismiss();
					pd = null;
					HK_LOG.getInstance().mLogInfo("onPageStarted",
							"hide the page");
				}

				pd = new ProgressDialog(UI_WebInfo.this);
				pd.setTitle("提示");
				pd.setMessage("正在加载数据");
				pd.setCancelable(true);
				pd.setCanceledOnTouchOutside(true);
				pd.setProgressStyle(ProgressDialog.THEME_HOLO_DARK);
				pd.show();
				HK_LOG.getInstance().mLogInfo("onPageStarted", "URL:" + url);
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				mStartEnterTmpUI(url);
				return super.shouldOverrideUrlLoading(view, url);
			}
		});
		
		m_web_detail.setWebChromeClient(new WebChromeClient());
		try {
			HK_LOG.getInstance().mLogInfo("UI_BillInfo", m_str_ulr);
			m_web_detail.loadUrl(m_str_ulr);
		} catch (NullPointerException e) {
			// TODO: handle exception
			HK_LOG.getInstance().mLogInfo("UI_BillInfo",
					"Error:" + e.toString());
		}
	}

	public void mShowMsg(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}

	public void mStartEnterTmpUI(String url) {
		if (url == null) {
			HK_LOG.getInstance().mLogInfo("VALUE", "return");
			return;
		}
		if (!url.contains("method=selClient")) {
			HK_LOG.getInstance().mLogInfo("VALUE", "return method!=selClient");
			return;
		}
		List<NameValuePair> m_paras = URLEncodedUtils.parse(URI.create(url),
				"UTF-8");
		for (int i = 0; i < m_paras.size(); i++) {
			NameValuePair nvp = m_paras.get(i);
			String name = nvp.getName();
			String value = nvp.getValue();

			HK_LOG.getInstance().mLogInfo("VALUE", name + "-" + value);
			HK_SP_Base_Util util = new HK_SP_Base_Util(getApplicationContext());
			util.mStoreStringValue(name, value);
		}
		
		}
	
	final class DemoJavaScriptInterface {  
        DemoJavaScriptInterface() { 
        	
        }  
        @JavascriptInterface
        public void mGetPhonePhoto(final String typeId){
    		
    		//实现调用摄像头拍照
        			Log.i("UI_WEB", typeId);
        	        type = typeId;	
		        	Intent intentPhote = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					File out = new File(getPhotopath());
					Uri uri = Uri.fromFile(out);
					//
					intentPhote.putExtra(MediaStore.EXTRA_OUTPUT, uri);
					intentPhote.putExtra(MediaStore.Images.Media.ORIENTATION, 180);
					startActivityForResult(intentPhote, 2000);
				}       	
  
}	
	// 获取原图片存储路径
		private String getPhotopath() {
			// 照片全路径
			String fileName = "";
			// 文件夹路径
			String pathUrl = Environment.getExternalStorageDirectory() + "/mymy/";
			String imageName = "imageTest.jpg";
			File file = new File(pathUrl);
			file.mkdirs();// 创建文件夹
			fileName = pathUrl + imageName;
			return fileName;
		}		
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			// TODO Auto-generated method stub
			super.onActivityResult(requestCode, resultCode, data);
			if (requestCode == 2000 && resultCode == Activity.RESULT_OK) {
				File file = new File(getPhotopath());
				String filePath = file.getAbsolutePath();
				 if (!TextUtils.isEmpty(filePath)) 
                 {
                     try 
                     {
                         ImageUtils.resampleImageAndSaveToNewLocation(filePath,filePath,3000,90);
                     } catch (Exception e)
                     {
                     }catch (OutOfMemoryError error)
                     {
                         
                     }
                     WebPostDataTask webPostDataTask = new WebPostDataTask();
     				webPostDataTask.execute(filePath);
                 }

				Log.i("UI_WEB", filePath);											
						}
				}
		//上传图片
		private class WebPostDataTask extends AsyncTask<String,  Void, String> 
		{
			@Override
			protected String doInBackground(String... params) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();				
				byte[] data = new byte[1024];
				int ret = -1;
				FileInputStream fis;
				try {
					fis = new FileInputStream(new File(params[0]));
					while((ret = fis.read(data)) != -1)
					{
						baos.write(data, 0, ret);
					}
					fis.close();
					String base = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
//					HK_LOG.getInstance().mLogInfo("onResume", base);
					base = URLEncoder.encode(base);
					Log.i("httpResponse", base);	
//					HK_LOG.getInstance().mLogInfo("onResume", base);
					
					String url = "http://192.168.4.41:81/pic.do?method=uploadImageFromMobileByHtml5";
					String casenum = HK_SP_Base_Util.getinstance(getApplicationContext()).mGetStringValue("str_anjian", "");
					Log.i("httpResponse", casenum);			
					HK_SP_Base_Util util = new HK_SP_Base_Util(getApplicationContext());
					String phonenum = util.mGetStringValue("PHONE_NUM", "");
					Log.i("httpResponse", phonenum);
					
					
					List<NameValuePair> param_list = new ArrayList<NameValuePair>(); 
					
//					method=uploadImageFromMobileByHtml5
//					param_list.add(new BasicNameValuePair("method", "uploadImageFromMobileByHtml5"));
					param_list.add(new BasicNameValuePair("caseNo", "sss20161210001")); 
					param_list.add(new BasicNameValuePair("phoneNum", "18762651946")); 
					param_list.add(new BasicNameValuePair("typeId", type));
					param_list.add(new BasicNameValuePair("fileLen", base.length()+""));
					param_list.add(new BasicNameValuePair("base64Data", base));
					param_list.add(new BasicNameValuePair("filePath", ""));
					param_list.add(new BasicNameValuePair("isEnd", "1"));
					String result = mPostData(url, param_list);
					Log.i("httpResponse", result);
					return result;
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return "";			
			}

			@Override
			protected void onPostExecute(String result) {
				
				if(result == null ||"".equals(result))
				{
					mShowMsg("上传失败");
					return;
				}
				if(result.contains("\"status\":\"1\""))
				{
					mShowMsg("上传成功");
				}else{
					mShowMsg("上传失败");
				}
				super.onPostExecute(result);
			}
					
		}
	public String mPostData(String url, List<NameValuePair> params)
		{
			
	        try { 
	        	HK_LOG.getInstance().mLogInfo("mPostData", "URL:"+url);
	    		HttpPost httpPost = new HttpPost(url); 
	    		  
	            HttpResponse httpResponse = null; 
	            // 设置httpPost请求参数 
	            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8)); 
	            httpResponse = new DefaultHttpClient().execute(httpPost); 
	            //System.out.println(httpResponse.getStatusLine().getStatusCode()); 
	            if (httpResponse.getStatusLine().getStatusCode() == 200) { 
	                // 第三步，使用getEntity方法活得返回结果 
	                String result = EntityUtils.toString(httpResponse.getEntity()); 
	                return result;
	            } 
	        } catch (ClientProtocolException e) { 
	            e.printStackTrace(); 
	            HK_LOG.getInstance().mLogInfo("mPostData", "ERROR:"+e.toString());
	        } catch (IOException e) { 
	            e.printStackTrace(); 
	            HK_LOG.getInstance().mLogInfo("mPostData", "ERROR:"+e.toString());
	        } catch (NullPointerException e) {
				// TODO: handle exception
	        	HK_LOG.getInstance().mLogInfo("mPostData", "ERROR:"+e.toString());
			}catch (RuntimeException e) {
				// TODO: handle exception
				HK_LOG.getInstance().mLogInfo("mPostData", "ERROR:"+e.toString());
			}
	        return "";

		}

			
		
		
			
	

}
