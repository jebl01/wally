package se.jeppetest;

import java.net.MalformedURLException;
import java.net.URL;

import se.jeppetest.controls.EventListener;
import se.jeppetest.controls.Glyph;
import se.jeppetest.controls.GlyphFactory;
import utils.JsonRequestHelper;

public class PanelLoader {
	private final GlyphFactory glyphFactory;
	private final URL serverUrl;
	private final DataFetcherService dataFetcherService;
	private final EventListener eventListener;

	public PanelLoader(GlyphFactory glyphFactory, DataFetcherService dataFetcherService, EventListener eventListener, String url) throws MalformedURLException {
		this.glyphFactory = glyphFactory;
		this.dataFetcherService = dataFetcherService;
		this.eventListener = eventListener;
		this.serverUrl = new URL(url);
	}
	
	public Glyph loadPanels() throws Exception {
		return glyphFactory.parseJson(JsonRequestHelper.getJson(this.serverUrl), eventListener, dataFetcherService);
	}
}
