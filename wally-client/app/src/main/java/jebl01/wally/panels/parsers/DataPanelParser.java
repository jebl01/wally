package jebl01.wally.panels.parsers;

import com.atlassian.fugue.Function2;
import com.atlassian.fugue.Option;
import com.atlassian.fugue.Options;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import jebl01.wally.DataFetcherService;
import jebl01.wally.DataProvider;
import jebl01.wally.RedrawListener;
import jebl01.wally.panels.LabelDecorator;
import jebl01.wally.panels.Panel;
import jebl01.wally.panels.signals.COMPARATOR;
import jebl01.wally.panels.signals.LEVEL;
import jebl01.wally.panels.signals.Signal;

import static jebl01.wally.utils.JsonUtils.asIterable;
import static jebl01.wally.utils.JsonUtils.getValue;

public abstract class DataPanelParser extends ParserBase {
    @Override
    protected Option<? extends Panel> createPanel(JSONObject object, final RedrawListener redrawListener, DataFetcherService dataFetcherService, final ScheduledThreadPoolExecutor animatorExecutor, Option<String> label) {
        //get signal / datacollectors etc etc...

        //dataproviders
        List<DataProvider> dataProviders = new ArrayList<>();
        for(JSONObject data : getValue(object, "data", JSONObject.class)) {
            for(String key : asIterable(data.keys())) {
                for(String frequency : getValue(data, key, String.class)) {
                    DataProvider dataProvider = new DataProvider(key, DataProvider.FREQUENCY.valueOf(frequency));
                    dataFetcherService.register(dataProvider);
                    dataProviders.add(dataProvider);
                }
            }
        }

        //signals
        Signal.SignalBuilder signalBuilder = Signal.SignalBuilder.with(Signal.UNKNOWN);
        for(JSONArray signals : getValue(object, "signals", JSONArray.class)) {
            for(int i = 0; i < signals.length() ; i++) {
                for(JSONObject s : Option.option(signals.optJSONObject(i))) {
                    for(String level : getValue(s, "level", String.class)) {
                        for(Long value : getValue(s, "value", Long.class)) {
                            for(String comparator : getValue(s, "comparator", String.class)) {
                                signalBuilder.then(new Signal(LEVEL.valueOf(level), value, COMPARATOR.valueOf(comparator)));
                            }
                        }
                    }
                }
            }
        }
        signalBuilder.then(Signal.OK);

        //yscale
        Option<Long> yScale = getValue(object, "yscale", Long.class);

        //create panel
        Option<Panel> panel = createDataPanel(object, redrawListener, dataProviders, signalBuilder.build(), animatorExecutor, yScale);

        //wrap in label decorator
        return Options.lift2(new Function2<String, Panel, Panel>(){
            public Panel apply(String label, Panel panel) {
                return new LabelDecorator(redrawListener, label, LabelDecorator.LabelPosition.BELOW, panel);
            }
        }).apply(label, panel).orElse(panel);
    }

    protected abstract Option<Panel> createDataPanel(JSONObject object, RedrawListener redrawListener, List<DataProvider> dataProviders, Signal signal, ScheduledThreadPoolExecutor executor, Option<Long> yScale);
}
