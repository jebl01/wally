package jebl01.wally.panels.parsers;

import com.atlassian.fugue.Option;
import com.google.common.base.Function;

import org.json.JSONObject;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import jebl01.wally.DataFetcherService;
import jebl01.wally.RedrawListener;
import jebl01.wally.panels.LabelDecorator;
import jebl01.wally.panels.Panel;
import jebl01.wally.panels.Row;

public class RowParser extends ParserBase {
    @Override
    protected Option<Panel> createPanel(JSONObject object, final RedrawListener redrawListener, DataFetcherService dataFetcherService, ScheduledThreadPoolExecutor animatorExecutor, Option<String> label) {
        return label.map(new Function<String, Panel>() {
            public Panel apply(String label) {
                return new LabelDecorator(redrawListener, label, LabelDecorator.LabelPosition.ABOVE, new Row(redrawListener));
            }
        }).orElse(Option.some(new Row(redrawListener)));
    }
}
