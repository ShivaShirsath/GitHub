package ss.GitHub;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.design.widget.NavigationView;
import android.view.MenuItem;
import android.view.View;
import android.view.Gravity;
import android.widget.Toast;
import android.webkit.WebView;
import android.net.Uri;
import android.webkit.ValueCallback;
import android.annotation.SuppressLint;
import android.webkit.*;
import android.os.*;
import android.view.Window.*;
import android.content.*;
import java.io.*;
import android.content.res.*;
import android.app.*;
import android.provider.*;
import android.icu.text.*;
import java.util.*;
import android.content.pm.*;
import java.lang.reflect.*;
import android.widget.*;
import android.support.v4.view.*;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawer_layout;
    private NavigationView left_nav,right_nav;
	private WebView webView;
	private WebSettings webSettings;
	private String git = "https://github.com/", user = "ShivaShirsath", tab = "?tab=", link = git + user;
	private String CM;
	private ValueCallback<Uri> UM;  
	private ValueCallback<Uri[]> UMA;
	private long backPressedTime=0;
	private CompoundButton desktopButton;
	
	@SuppressLint({"SetJavaScriptEnabled", "ObsoleteSdkInt"})
	
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);	
			
		
		// if base.apk is not sending at upper versions
		if (Build.VERSION.SDK_INT >= 24) {
			try {
				Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
				m.invoke(null);
			} catch (Exception e) {
				Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}		
		webView = findViewById(R.id.webView);     
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url){
				webView.setVisibility(View.VISIBLE);
			}
		});
        webView.loadUrl(link);
        webSettings = webView.getSettings();

		if(Build.VERSION.SDK_INT >= 21){  
			webView.getSettings().setMixedContentMode(0);  
			webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);  
		}else if(Build.VERSION.SDK_INT >= 19){  
			webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);  
		}else if(Build.VERSION.SDK_INT < 19){  
			webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);  
		}
		
		webView.setWebViewClient(new Callback());  
		
		webView.setWebChromeClient(new WebChromeClient() {  
			//For Android 3.0+  
			public void openFileChooser(ValueCallback<Uri> uploadMsg){
				fileChooser(uploadMsg);
			}  
			// For Android 3.0+, above method not supported in some android 3+ versions, in such case we use this  
			public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType){
				fileChooser(uploadMsg);
			}  
			//For Android 4.1+  
			public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture){
				fileChooser(uploadMsg);
			}  
			//For Android 5.0+  
			public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams){  
				if (UMA != null){  
					UMA.onReceiveValue(null);  
				}  
				UMA = filePathCallback;  
				Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
				if (takePictureIntent.resolveActivity(MainActivity.this.getPackageManager()) != null){
					File photoFile = null;  
					try{
						@SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());  
						String imageFileName = "img_" + timeStamp + "_";  
						File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);  
						photoFile = File.createTempFile(imageFileName, ".jpg", storageDir);  
						takePictureIntent.putExtra("PhotoPath", CM);  
					} catch (Exception e){  
						Toast.makeText(MainActivity.this, e.getMessage().toString(), Toast.LENGTH_LONG).show();
					}
					if (photoFile != null){  
						CM = "file:" + photoFile.getAbsolutePath();  
						takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));  
					}else{  
						takePictureIntent = null;  
					}  
				}   
				Intent[] intentArray;  
				if (takePictureIntent != null){  
					intentArray = new Intent[]{takePictureIntent};  
				} else {  
					intentArray = new Intent[0];  
				}  
				Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);  
				chooserIntent.putExtra(Intent.EXTRA_INTENT, fileChooser(null));  
				chooserIntent.putExtra(Intent.EXTRA_TITLE, "Choose an Action");  
				chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);  
				startActivityForResult(chooserIntent, 1);  
				return true;  
			}  
		});
	
		webView.setDownloadListener(new DownloadListener() {       
			@Override
			public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength){
				setDownload("Downloading...", contentDisposition.replace("attachment; filename=", ""), url);
			}
		});
		
        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);

        left_nav= (NavigationView) findViewById(R.id.left_nav);
        left_nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
			@Override
			public boolean onNavigationItemSelected(MenuItem item){
				switch(item.getItemId()){
					case R.id.item_newRepo  : link = git + 				String.valueOf(	item.getTitle()).toLowerCase();	break;
					case R.id.item_repo     : link = git + user + tab + String.valueOf(	item.getTitle()).toLowerCase();	break;
					case R.id.item_project  : link = git + user + tab + String.valueOf(	item.getTitle()).toLowerCase();	break;
					case R.id.item_package  : link = git + user + tab + String.valueOf(	item.getTitle()).toLowerCase();	break;
					case R.id.item_settings : link = git + 				String.valueOf(	item.getTitle()).toLowerCase();	break;
					
					case R.id.item_shareApp: sendAppItself(MainActivity.this); break;
					case R.id.item_aboutApp: link = "https://sites.google.com/new"; break;
				}
				webView.loadUrl(link);
				drawer_layout.closeDrawers();
				return true;
			}
		});
		
        right_nav= (NavigationView) findViewById(R.id.right_nav);
        right_nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
			@Override
			public boolean onNavigationItemSelected(MenuItem item){
				switch(item.getItemId()){
					case R.id.item_insta        : startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_shiva_shirsath__")).setPackage("com.instagram.android")); break;
					case R.id.item_whatsapp     : startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://wa.me/919130057189")).setPackage("com.whatsapp")); break;  
					case R.id.item_openInChrome : startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(webView.getUrl())).setPackage("com.android.chrome")); break;
					case R.id.item_desktop      : desktopButton.setChecked(!desktopButton.isChecked()); break;
					case R.id.item_websetting   : startActivity(new Intent(MainActivity.this, SetOptionToWeb.class)); break;
				}
				drawer_layout.closeDrawers();
				return true;
			}
		});
		MenuItem desktopItem = right_nav.getMenu().findItem(R.id.item_desktop);
		desktopButton = (CompoundButton) MenuItemCompat.getActionView(desktopItem);
		desktopButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean status){
				setSettingsX(status);
				drawer_layout.closeDrawers();
			}
		});	
		setSettingsX(desktopButton.isChecked());
    }
    @Override  
	protected void onActivityResult(int requestCode, int resultCode, Intent intent){  
		super.onActivityResult(requestCode, resultCode, intent);  
		if (Build.VERSION.SDK_INT >= 21){  
			Uri[] results = null;  
			//Check if response is positive  
			if(resultCode == Activity.RESULT_OK){  
				if(requestCode == 1){  
					if(null == UMA){  
						return;  
					}  
					if(intent == null){  
						//Capture Photo if no image available  
						if(CM != null){  
							results = new Uri[]{Uri.parse(CM)};  
						}  
					}else{  
						String dataString = intent.getDataString();  
						if(dataString != null){  
							results = new Uri[]{Uri.parse(dataString)};  
						}  
					}  
				}  
			}  
			UMA.onReceiveValue(results);  
			UMA = null;  
		}else{  
			if(requestCode == 1){  
				if (null == UM) return;  
				Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();  
				UM.onReceiveValue(result);  
				UM = null;  
			}  
		}  
	}
	public Intent fileChooser(ValueCallback<Uri> uploadMsg){
		UM = uploadMsg;  
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);  
		intent.addCategory(Intent.CATEGORY_OPENABLE);  
		intent.setType("*/*");
		if(uploadMsg != null) MainActivity.this.startActivityForResult(Intent.createChooser(intent, "File Chooser"), 1);
		return intent;
	}
	public void setDownload(String msg,String fileName, String url){
		DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
		request.allowScanningByMediaScanner();
		request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED); //Notify client once download is completed!
		request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
		((DownloadManager) getSystemService(DOWNLOAD_SERVICE)).enqueue(request);
		Toast.makeText(getApplicationContext(), msg + " ( " + fileName + " )", Toast.LENGTH_SHORT).show();
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig){
		super.onConfigurationChanged(newConfig);
	}
	@Override
	protected void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		webView.saveState(outState);
	}
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState){
		super.onRestoreInstanceState(savedInstanceState);
		webView.restoreState(savedInstanceState);
	}
	@Override
	public void onBackPressed(){
		if(drawer_layout.isDrawerOpen(left_nav) || drawer_layout.isDrawerOpen(right_nav)){
			drawer_layout.closeDrawers();
		}else if(webView.canGoBack()){
			webView.goBack();
		}else if (backPressedTime + 2000 > System.currentTimeMillis()) {
			super.onBackPressed();
			finish();
			return;
		} 
		else {
			Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
		}
		backPressedTime = System.currentTimeMillis();
	}
	public class Callback extends WebViewClient{
		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl){   
			Toast.makeText(getApplicationContext(), "Failed loading app!", Toast.LENGTH_SHORT).show();   
		}
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if(url.contains("github")) {
				if(url.contains("raw")){
					setDownload(user+"@GitHub/"+url.substring(url.lastIndexOf("/")+1), user+"@GitHub/"+url.substring(url.lastIndexOf("/")+1), url);
				}else{
					view.loadUrl(url);
				}
			} else {
				view.loadUrl(url);
             //   startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
			}
			return true;
		}
	}
	public void sendAppItself(Activity paramActivity) {
		try {
			paramActivity.startActivity(Intent.createChooser(new Intent(Intent.ACTION_SEND).setType("*/*").putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + paramActivity.getPackageManager().getApplicationInfo(paramActivity.getPackageName(), PackageManager.GET_META_DATA).publicSourceDir)), "Share it using"));
		} catch (Exception e) {
			Toast.makeText(this,e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	public void onClickTitle(View view){
		webView.loadUrl(git+user);
	}
	public void onClickEmail(View view){
		startActivity(Intent.createChooser(new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto","shiva.s.shirsath@gmail.com", null)).putExtra(Intent.EXTRA_SUBJECT, "Type your Subject").putExtra(Intent.EXTRA_TEXT, "Type, What you want to send me!"), "Send Via..."));
	}
	void setSettingsX(boolean enabled){
		webSettings.setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
		webSettings.setDisplayZoomControls(enabled);
		//webSettings.setForceDarkMode(WebSettings.FORCE_DARK_ON);
		webSettings.setJavaScriptEnabled(enabled);
		webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
		
		webSettings.setDatabaseEnabled(enabled);
		webSettings.setDomStorageEnabled(enabled);
		webSettings.setSupportZoom(enabled);
		webSettings.setBuiltInZoomControls(enabled);
		webSettings.setDisplayZoomControls(enabled);
		webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		webView.setScrollbarFadingEnabled(enabled);
		webSettings.setMinimumFontSize(1);
		webSettings.setMinimumLogicalFontSize(1);
		webSettings.setAllowFileAccess(enabled);
		webSettings.setAllowContentAccess(enabled);
		webSettings.setSavePassword(enabled);
		webSettings.setUserAgentString(webSettings.getUserAgentString().replace(webSettings.getUserAgentString().substring(webSettings.getUserAgentString().indexOf("("), webSettings.getUserAgentString().indexOf(")") + 1), enabled ? "(Macintosh; Intel Mac OS X 11_2_3)" /*(X11; Linux x86_64)*//*(Windows NT 10.0; Win64; x64)*/ : "(iPhone; CPU iPhone OS 14_4 like Mac OS X)"/*(Linux; Android 10)*/ ));
		webSettings.setUseWideViewPort(enabled);
		webSettings.setLoadWithOverviewMode(enabled);
		webView.reload();
	}
}
