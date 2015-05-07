package jebl01.wally.controls;

import com.atlassian.fugue.Option;

import java.util.List;

import jebl01.wally.DataProvider;
import jebl01.wally.controls.layout.Layout;

public abstract class DataPanel extends Panel implements DataProvider.DataProviderListener {
    private final List<DataProvider> dataProviders;

    protected DataPanel(EventListener eventListener, List<DataProvider> dataProviders, Option<String> label) {
        super(Layout.NONE, eventListener, label);

        this.dataProviders = dataProviders;

        for(DataProvider dataProvider : this.dataProviders) {
            dataProvider.setListener(this);
        }
    }

}
