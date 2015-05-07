package jebl01.wally;

import java.util.concurrent.ScheduledThreadPoolExecutor;

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

import jebl01.wally.controls.EventListener;
import jebl01.wally.controls.Panel;
import jebl01.wally.controls.PanelFactory;

public class WallyActivity extends Activity implements OnSharedPreferenceChangeListener{
	private ScheduledThreadPoolExecutor animatorExecutor;
	private ScheduledThreadPoolExecutor dataFetcherExecutor;
	private RootView rootView = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

    	PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);

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
    
    private static class DownloadPanelsTask extends AsyncTask<String, Void, Panel> {
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
		protected Panel doInBackground(String... url) {
			try {
				final DataFetcherService dataFetcherService = new DataFetcherService(dataFetcherExecutor, server);
				final PanelLoader loader = new PanelLoader(new PanelFactory(animatorExecutor), dataFetcherService, eventListener, url[0]);
				Panel root = loader.loadPanels();
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
		protected void onPostExecute(Panel rootPanel) {
			System.out.println("loaded panel: " + rootPanel);
			view.setRootPanel(rootPanel);
			view.requestLayout();
			view.invalidate();
			rootPanel.start();
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
