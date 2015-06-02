package jebl01.wally.panels.parsers;


import com.atlassian.fugue.Option;
import com.google.common.base.Function;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import jebl01.wally.DataFetcherService;
import jebl01.wally.RedrawListener;
import jebl01.wally.panels.Panel;
import jebl01.wally.panels.contentPanels.SideBySideGraph;

import static jebl01.wally.utils.JsonUtils.getValue;

public abstract class Parser {
    private static final Map<String, Parser> parsers = new HashMap<>();

    static {
        parsers.put("column", new ColumnParser());
        parsers.put("row", new RowParser());
        parsers.put("graph", new SideBySideGraph.Parser());
    }

    public static Option<Parser> getParser(JSONObject object) {
        return getValue(object, "type", String.class).flatMap(new Function<String, Option<Parser>>() {
            public Option<Parser> apply(String type) {
                return Option.option(parsers.get(type));
            }
        });
    }

    public static Option<Panel> parsePanelsConfiguration(JSONObject object, RedrawListener redrawListener, DataFetcherService dataFetcherService, ScheduledThreadPoolExecutor animatorExecutor) {

        System.out.println("parsing panels: " + object.toString());
        for(JSONObject rootPanel : getValue(object, "rootpanel", JSONObject.class)) {

            System.out.println("parsing root panel");
            for(Parser parser : getParser(rootPanel)) {
                return parser.parse(rootPanel, redrawListener, dataFetcherService, animatorExecutor);
            }
        }

        //TODO: messagebox?
        return Option.none();
    }

    public Option<Panel> parse(final JSONObject object, final RedrawListener redrawListener, final DataFetcherService dataFetcherService, final ScheduledThreadPoolExecutor animatorExecutor) {
        return getParser(object).flatMap(new Function<Parser, Option<? extends Panel>>() {
            public Option<? extends Panel> apply(Parser parser) {
                return parser.parse(object, redrawListener, dataFetcherService, animatorExecutor);
            }
        });
    }
}