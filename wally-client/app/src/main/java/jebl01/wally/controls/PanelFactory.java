package jebl01.wally.controls;

import com.atlassian.fugue.Option;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import jebl01.wally.DataFetcherService;
import jebl01.wally.DataProvider;


public class PanelFactory {
	public static final Parser PARSER = new Parser();
	private final ScheduledThreadPoolExecutor animatorExecutor;
	
	public PanelFactory(ScheduledThreadPoolExecutor animatorExecutor) {
		this.animatorExecutor = animatorExecutor;
		
	}
	
	public Panel parseJson(JSONObject object, EventListener eventListener, DataFetcherService dataFetcherService) {
		
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
		private static final Parser ROW_PARSER = new RowParser();
		private static final Parser COLUMN_PARSER = new ColumnParser();
		private static final Parser GRAPH_PARSER = new GraphParser();
		
		Panel parse(JSONObject object, EventListener eventListener, DataFetcherService dataFetcherService, ScheduledThreadPoolExecutor animatorExecutor) throws JSONException {
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

        public <T> T getValue(JSONObject jsonObject, String label, Class<T> type) {
            try {
                if(String.class.isAssignableFrom(type)) {
                    return (T)(jsonObject.has(label) ? jsonObject.getString(label) : null);
                } else if(Integer.class.isAssignableFrom(type)) {
                    return (T)(jsonObject.has(label) ? jsonObject.getInt(label) : null);
                } else if(Long.class.isAssignableFrom(type)) {
                    return (T)(jsonObject.has(label) ? jsonObject.getLong(label) : null);
                } else if(Double.class.isAssignableFrom(type)) {
                    return (T)(jsonObject.has(label) ? jsonObject.getDouble(label) : null);
                } else if(Boolean.class.isAssignableFrom(type)) {
                    return (T)(jsonObject.has(label) ? jsonObject.getBoolean(label) : null);
                } else {
                    return (T)null;
                }

            } catch (JSONException e) {
                return null;
            }
        }

        public <T> T getValue(JSONObject jsonObject, String label, Class<T> type, T defaultValue) {
            T value = getValue(jsonObject, label, type);
            return value == null ? defaultValue : value;
        }

        public <T> Option<T> getOptionValue(JSONObject jsonObject, String label, Class<T> type) {
            return Option.option(getValue(jsonObject, label, type));
        }
	}
	
	private static class GraphParser extends Parser {
		
		@Override
        Panel parse(JSONObject object, EventListener eventListener, DataFetcherService dataFetcherService, ScheduledThreadPoolExecutor animatorExecutor) throws JSONException {
            List<DataProvider> dataProviders = new ArrayList<>();

            Option<String> label = getOptionValue(object, "label", String.class);

            JSONObject data = object.getJSONObject("data");
            Iterator<String> dataIterator = data.keys();

            while(dataIterator.hasNext()) {
                String dataKey = dataIterator.next();
                String dataFrequency = getValue(data, dataKey, String.class);
                DataProvider.FREQUENCY frequency = DataProvider.FREQUENCY.valueOf(dataFrequency);

			    DataProvider dataProvider = new DataProvider(dataKey, frequency);
			    dataFetcherService.register(dataProvider);
                dataProviders.add(dataProvider);
            }


			return new Graph(eventListener, dataProviders, animatorExecutor, label);
		}
	}
	
	private static abstract class CompositeParser<T extends Panel> extends Parser {
		@Override
		public Panel parse(JSONObject object, EventListener eventListener, DataFetcherService dataFetcherService, ScheduledThreadPoolExecutor animatorExecutor) throws JSONException {
			Option<String> label = object.has("label") ? Option.some(object.getString("label")) : Option.<String>none();
			
			T glyph = createGlyph(eventListener, label);
			
			JSONArray childPanels = object.getJSONArray("panels");
			
			for(int i = 0; i < childPanels.length(); i++) {
				JSONObject childPanel = childPanels.getJSONObject(i);
				glyph.insert(PARSER.parse(childPanel, eventListener, dataFetcherService, animatorExecutor));
			}
			
			return glyph;
		}
		
		protected abstract T createGlyph(EventListener eventListener, Option<String> label);
	}
	
	private static class RowParser extends CompositeParser<Row> {
		@Override
		protected Row createGlyph(EventListener eventListener, Option<String> label) {
			return new Row(eventListener, label);
		}
	}
	
	private static class ColumnParser extends CompositeParser<Column> {
		@Override
		protected Column createGlyph(EventListener eventListener, Option<String> label) {
			return new Column(eventListener, label);
		}
	}
}
