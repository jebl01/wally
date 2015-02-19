package se.jeppetest;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import se.jeppetest.controls.EventListener;
import se.jeppetest.controls.Glyph;
import se.jeppetest.controls.GlyphFactory;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;

public class WallyActivity extends Activity implements OnSharedPreferenceChangeListener{
	private ScheduledThreadPoolExecutor animatorExecutor;
	private ScheduledThreadPoolExecutor dataFetcherExecutor;
	private RootView rootView = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	   
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	
    	PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    	
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
    	rootView = new RootView(this);
    	rootView.setVisibility(View.VISIBLE);
    	rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    	setContentView(rootView);
    	
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    	preferences.registerOnSharedPreferenceChangeListener(this);
    	loadPanels(preferences);
    }
    
    private void loadPanels(SharedPreferences preferences) {    	
    	String server = preferences.getString(this.getString(R.string.pref_server_key), "");
    	
    	System.out.println("**************************************");
    	System.out.println("Servername : " + server);
    	System.out.println("**************************************");
    	
    	if(server == null || server == "") {
    		Intent intent = new Intent(this, Settings.class);
    		startActivity(intent);
    	} else {
    		refreshExecutors();
    		DownloadPanelsTask downloadPanelsTask = new DownloadPanelsTask(rootView, server, rootView, dataFetcherExecutor, animatorExecutor);
    		downloadPanelsTask.execute(server + "/dashboards/dogfight");    		
    	}
    }
    
    private void refreshExecutors() {
    	if(this.animatorExecutor != null) this.animatorExecutor.shutdownNow();
    	if(this.dataFetcherExecutor != null) this.dataFetcherExecutor.shutdownNow();
    	
    	this.animatorExecutor = new ScheduledThreadPoolExecutor(10);
    	this.dataFetcherExecutor = new ScheduledThreadPoolExecutor(10);
    }
    
    private static class DownloadPanelsTask extends AsyncTask<String, Void, Glyph> {
    	private final RootView view;
		private final String server;
		private final EventListener eventListener;
		private final ScheduledThreadPoolExecutor dataFetcherExecutor;
		private final ScheduledThreadPoolExecutor animatorExecutor;
    	
		public DownloadPanelsTask(RootView view, String server, EventListener eventListener, ScheduledThreadPoolExecutor dataFetcherExecutor, ScheduledThreadPoolExecutor animatorExecutor) {
			this.view = view;
			this.server = server;
			this.eventListener = eventListener;
			this.dataFetcherExecutor = dataFetcherExecutor;
			this.animatorExecutor = animatorExecutor;
    	}
		
		@Override
		protected Glyph doInBackground(String... url) {
			try {
				final DataFetcherService dataFetcherService = new DataFetcherService(dataFetcherExecutor, server);
				final PanelLoader loader = new PanelLoader(new GlyphFactory(animatorExecutor), dataFetcherService, eventListener, url[0]);
				Glyph root = loader.loadPanels();
				System.out.println("loaded root: " + root);
				System.out.println("starting datafetcher");
				dataFetcherService.start();
				return root;
			} catch(Exception ex) {
				//TODO: messagebox?
				ex.printStackTrace();
			}
			return null;
		}
    	
		@Override
		protected void onPostExecute(Glyph rootGlyph) {
			System.out.println("loaded panel: " + rootGlyph);
			view.setRootPanel(rootGlyph);
			view.requestLayout();
			view.invalidate();
			rootGlyph.start();
		}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
        
        return false;
    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if(key.equals(this.getString(R.string.pref_server_key))) {
			loadPanels(sharedPreferences);
		}
	}
}
