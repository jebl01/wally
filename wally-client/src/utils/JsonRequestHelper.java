package utils;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

public class JsonRequestHelper {
	private static final int CONNECT_TIMEOUT_MILLISEC = 5000;
	private static final int READ_TIMEOUT_MILLISEC = 5000;
	
	public static JSONObject getJson(URL url) throws Exception {
		HttpURLConnection urlConnection = prepareConnection(url);
        try {
        	String response = StreamUtils.toString(urlConnection.getInputStream());
        	return new JSONObject(response);        	
        } finally {
        	urlConnection.disconnect();
        }
		
	}

	public static JSONArray getJson(URL url, JSONArray payload) throws Exception {
		HttpURLConnection urlConnection = prepareConnection(url);
		urlConnection.setRequestProperty("Content-Type", "application/json");
		
		try {
			urlConnection.setDoOutput(true);
			urlConnection.setChunkedStreamingMode(0);
			
			OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
			out.write(payload.toString().getBytes());
			out.flush();
			out.close();
			
			String response = StreamUtils.toString(urlConnection.getInputStream());
			return new JSONArray(response);			
		} finally {
			urlConnection.disconnect();
		}
	}

	private static HttpURLConnection prepareConnection(URL url) throws IOException {
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setUseCaches(false);
		urlConnection.setAllowUserInteraction(false);
		urlConnection.setConnectTimeout(CONNECT_TIMEOUT_MILLISEC);
		urlConnection.setReadTimeout(READ_TIMEOUT_MILLISEC);
		return urlConnection;
	}
	
}
