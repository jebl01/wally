package jebl01.wally;

import java.net.MalformedURLException;
import java.net.URL;

import jebl01.wally.controls.EventListener;
import jebl01.wally.controls.Panel;
import jebl01.wally.controls.PanelFactory;
import jebl01.wally.utils.JsonRequestHelper;

public class PanelLoader {
	private final PanelFactory panelFactory;
	private final URL serverUrl;
	private final DataFetcherService dataFetcherService;
	private final EventListener eventListener;

	public PanelLoader(PanelFactory panelFactory, DataFetcherService dataFetcherService, EventListener eventListener, String url) throws MalformedURLException {
		this.panelFactory = panelFactory;
		this.dataFetcherService = dataFetcherService;
		this.eventListener = eventListener;
		this.serverUrl = new URL(url);
	}
	
	public Panel loadPanels() throws Exception {
		return panelFactory.parseJson(JsonRequestHelper.getJson(this.serverUrl), eventListener, dataFetcherService);
	}
}
