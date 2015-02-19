package se.jeppetest.controls;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import se.jeppetest.DataFetcherService;
import se.jeppetest.DataProvider;

public class GlyphFactory {
	public static final Parser PARSER = new Parser();
	private final ScheduledThreadPoolExecutor animatorExecutor;
	
	public GlyphFactory(ScheduledThreadPoolExecutor animatorExecutor) {
		this.animatorExecutor = animatorExecutor;
		
	}
	
	public Glyph parseJson(JSONObject object, EventListener eventListener, DataFetcherService dataFetcherService) {
		
		try {
			JSONObject rootPanel = object.getJSONObject("rootpanel");
			return PARSER.parse(rootPanel, eventListener, dataFetcherService, animatorExecutor);
			
		} catch (Exception e) {
			//TODO: messagebox?
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	private static class Parser {
		private static Parser ROW_PARSER = new RowParser();
		private static Parser COLUMN_PARSER = new ColumnParser();
		private static Parser GRAPH_PARSER = new GraphParser();
		
		Glyph parse(JSONObject object, EventListener eventListener, DataFetcherService dataFetcherService, ScheduledThreadPoolExecutor animatorExecutor) throws JSONException {
			String type = object.getString("type");
			
			switch(type) {
			case "row":
				return ROW_PARSER.parse(object, eventListener, dataFetcherService, animatorExecutor);				
			case "column":
				return COLUMN_PARSER.parse(object, eventListener, dataFetcherService, animatorExecutor);
			case "graph":
				return GRAPH_PARSER.parse(object, eventListener, dataFetcherService, animatorExecutor);
			default:
				return null;
			}
		}
	}
	
	private static class GraphParser extends Parser {
		
		@Override
		Glyph parse(JSONObject object, EventListener eventListener, DataFetcherService dataFetcherService, ScheduledThreadPoolExecutor animatorExecutor) throws JSONException {
			String label = object.has("label") ? object.getString("label") : null;
			String dataKey = object.getString("data");
			int yscale = object.has("yscale") ? object.getInt("yscale") : -1;
			int bufferSize = object.getInt("buffersize");
			int frequency = object.getInt("interval");
			
			DataProvider dataProvider = new DataProvider(dataKey, bufferSize, frequency, TimeUnit.SECONDS, yscale);
			
			dataFetcherService.register(dataProvider);
			
			return new Graph(eventListener, dataProvider, animatorExecutor, label);
		}
	}
	
	private static abstract class CompositeParser<T extends Glyph> extends Parser {
		@Override
		public Glyph parse(JSONObject object, EventListener eventListener, DataFetcherService dataFetcherService, ScheduledThreadPoolExecutor animatorExecutor) throws JSONException {
			String label = object.has("label") ? object.getString("label") : null;
			
			T glyph = createGlyph(eventListener, label);
			
			JSONArray childPanels = object.getJSONArray("panels");
			
			for(int i = 0; i < childPanels.length(); i++) {
				JSONObject childPanel = childPanels.getJSONObject(i);
				glyph.insert(PARSER.parse(childPanel, eventListener, dataFetcherService, animatorExecutor));
			}
			
			return glyph;
		}
		
		protected abstract T createGlyph(EventListener eventListener, String label); 
	}
	
	private static class RowParser extends CompositeParser<Row> {
		@Override
		protected Row createGlyph(EventListener eventListener, String label) {
			return new Row(eventListener, label);
		}
	}
	
	private static class ColumnParser extends CompositeParser<Column> {
		@Override
		protected Column createGlyph(EventListener eventListener, String label) {
			return new Column(eventListener, label);
		}
	}
}
