package se.jeppetest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;

import utils.JsonRequestHelper;

public class DataFetcherService {
	private final Map<Long, DataFetcher> fetchers = new HashMap<>();
	private final ScheduledThreadPoolExecutor executor;
	private URL serverUrl;

	public DataFetcherService(ScheduledThreadPoolExecutor executor, String serverUrl) throws MalformedURLException {
		this.executor = executor;
		this.serverUrl = new URL(serverUrl + "/metrics");
	}

	public void register(DataProvider dataProvider) {
		long frequencyMillisec = dataProvider.frequencyTimeUnit.toMillis(dataProvider.frequency);
		
		if (!fetchers.containsKey(frequencyMillisec)) {
			DataFetcher dataFetcher = new DataFetcher(serverUrl);
			fetchers.put(frequencyMillisec, dataFetcher);
		}

		fetchers.get(frequencyMillisec).addProvider(dataProvider);
	}
	
	public void start() {
		for(Entry<Long, DataFetcher> entry : this.fetchers.entrySet()) {
			long frequencyMillisec = entry.getKey();
			DataFetcher dataFetcher = entry.getValue();
			
			dataFetcher.initBuffers();
			this.executor.scheduleAtFixedRate(dataFetcher, frequencyMillisec, frequencyMillisec, TimeUnit.MILLISECONDS);
		}
	}
	
	public URL getServerUrl() {
		return serverUrl;
	}
	
	public static class DataFetcher implements Runnable {
		private final Map<String, DataProvider> dataProviders = new HashMap<>();
		private final URL serverUrl;
		private JSONArray payload = null;
		
		public DataFetcher(URL serverUrl) {
			this.serverUrl = serverUrl;
		}

		public void initBuffers() {
			if(payload == null) {
				createPayload();
			}
			
			try {
				URL bufferUrl = new URL(serverUrl + "/buffers");
				
				System.out.println("querying for data(POST): " + serverUrl + " payload: " +  payload.toString());
				JSONArray result = JsonRequestHelper.getJson(bufferUrl, payload);
				System.out.println("result: " + result);
				
				for(int i = 0; i < result.length(); i++) {
					JSONObject object = result.getJSONObject(i);
					
					String name = object.getString("name");
					JSONArray buffer = object.getJSONArray("data");
					
					int[] values = new int[buffer.length()];
					for(int bufferIndex = 0; bufferIndex < buffer.length(); bufferIndex++) {
						values[bufferIndex] = buffer.getInt(bufferIndex);
					}
					dataProviders.get(name).onBufferInit(values);						
					dataProviders.get(name).setValue(values[values.length - 1]);						
				}
				
			} catch (Exception e) {
				// swallow, a failed init is not the end of the world...
				e.printStackTrace();
			}
		}

		public void addProvider(DataProvider dataProvider) {
			dataProviders.put(dataProvider.key, dataProvider);
			payload = null;
		}

		@Override
		public void run() {
			if(payload == null) {
				createPayload();
			}
			
			try {
//				System.out.println("querying for data(POST): " + serverUrl + " payload: " +  payload.toString());
				JSONArray result = JsonRequestHelper.getJson(serverUrl, payload);
//				System.out.println("result: " + result);
				for(int i = 0; i < result.length(); i++) {
					JSONObject object = result.getJSONObject(i);
					String dataKey = object.names().getString(0);
					int value = (int)object.getDouble(dataKey);
					dataProviders.get(dataKey).setValue(value);
				}
				
			} catch (Exception e) {
				for(DataProvider dataProvider : dataProviders.values()) {
					dataProvider.setValue(-1);
				}
			}
		}

		private void createPayload() {
			JSONArray array = new JSONArray();
			
			for(String dataKey : dataProviders.keySet()) {
				array.put(dataKey);
			}
			this.payload = array;
		}
	}
}
