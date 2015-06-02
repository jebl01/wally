package jebl01.wally.panels.parsers;

import com.atlassian.fugue.Option;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import jebl01.wally.DataFetcherService;
import jebl01.wally.RedrawListener;
import jebl01.wally.panels.Panel;

import static jebl01.wally.utils.JsonUtils.getValue;

public abstract class ParserBase extends Parser {

    @Override
    public Option<Panel> parse(JSONObject object, RedrawListener redrawListener, DataFetcherService dataFetcherService, ScheduledThreadPoolExecutor animatorExecutor) {
        for(Panel panel : createPanel(object, redrawListener, dataFetcherService, animatorExecutor, getValue(object, "label", String.class))) {
            for(JSONArray childPanels : getValue(object, "panels", JSONArray.class)) {
                for(int i = 0; i < childPanels.length(); i++) {
                    for(JSONObject childPanel : Option.option(childPanels.optJSONObject(i))) {
                        for(Panel child : super.parse(childPanel, redrawListener, dataFetcherService, animatorExecutor)) {
                            panel.addChild(child);
                        }
                    }
                }
            }
            return Option.some(panel);
        }
        return Option.none();
    }

    protected abstract Option<? extends Panel> createPanel(JSONObject object, RedrawListener redrawListener, DataFetcherService dataFetcherService, ScheduledThreadPoolExecutor animatorExecutor, Option<String> label);
}
