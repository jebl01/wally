package jebl01.wally;

import java.net.URL;
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
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;

import com.atlassian.fugue.Option;

import jebl01.wally.panels.Panel;
import jebl01.wally.panels.parsers.Parser;
import jebl01.wally.utils.JsonRequestHelper;


public class WallyActivity extends Activity implements OnSharedPreferenceChangeListener{
	private ScheduledThreadPoolExecutor animatorExecutor;
	private ScheduledThreadPoolExecutor dataFetcherExecutor;
	private RootSurfaceView rootSurfaceView = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);

    	PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);

        rootSurfaceView = new RootSurfaceView(this);
        rootSurfaceView.setVisibility(View.VISIBLE);
        rootSurfaceView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(rootSurfaceView);

    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    	preferences.registerOnSharedPreferenceChangeListener(this);
    	loadPanels(preferences);
    }

    @Override
    protected void onDestroy() {
        if(this.animatorExecutor != null) this.animatorExecutor.shutdownNow();
        if(this.dataFetcherExecutor != null) this.dataFetcherExecutor.shutdownNow();

        super.onDestroy();
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
    		DownloadPanelsTask downloadPanelsTask = new DownloadPanelsTask(rootSurfaceView, server, rootSurfaceView, dataFetcherExecutor, animatorExecutor);
    		downloadPanelsTask.execute(server + "/dashboards/dogfight");    		
    	}
    }
    
    private void refreshExecutors() {
    	if(this.animatorExecutor != null) this.animatorExecutor.shutdownNow();
    	if(this.dataFetcherExecutor != null) this.dataFetcherExecutor.shutdownNow();
    	
    	this.animatorExecutor = new ScheduledThreadPoolExecutor(10);
    	this.dataFetcherExecutor = new ScheduledThreadPoolExecutor(10);
    }
    
    private static class DownloadPanelsTask extends AsyncTask<String, Void, Option<Panel>> {
    	private final RootSurfaceView view;
		private final String server;
		private final RedrawListener redrawListener;
		private final ScheduledThreadPoolExecutor dataFetcherExecutor;
		private final ScheduledThreadPoolExecutor animatorExecutor;
    	
		public DownloadPanelsTask(RootSurfaceView view, String server, RedrawListener redrawListener, ScheduledThreadPoolExecutor dataFetcherExecutor, ScheduledThreadPoolExecutor animatorExecutor) {
			this.view = view;
			this.server = server;
			this.redrawListener = redrawListener;
			this.dataFetcherExecutor = dataFetcherExecutor;
			this.animatorExecutor = animatorExecutor;
    	}
		
		@Override
		protected Option<Panel> doInBackground(String... url) {
            DataFetcherService dataFetcherService = null;
            try {
				dataFetcherService = new DataFetcherService(dataFetcherExecutor, server);

                return Parser.parsePanelsConfiguration(
                        JsonRequestHelper.getJson(new URL(url[0])),
                        redrawListener,
                        dataFetcherService,
                        animatorExecutor
                );

			} catch(Exception ex) {
				//TODO: messagebox?
				ex.printStackTrace();
			} finally {
                if(dataFetcherService != null) {
                    dataFetcherService.start();
                }
            }
            return Option.none();
		}
    	
		@Override
		protected void onPostExecute(Option<Panel> optionRootPanel) {
            if(optionRootPanel.isEmpty()) {
                System.out.println("failed to load root panel!");
                //TODO: messagebox?
            } else {
                for(Panel rootPanel : optionRootPanel) {
                    System.out.println("loaded panel: " + rootPanel);
                    view.setRootPanel(rootPanel);
                    rootPanel.start();
                }
            }
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
