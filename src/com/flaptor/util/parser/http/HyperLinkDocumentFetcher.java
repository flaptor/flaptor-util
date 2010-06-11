package com.flaptor.util.parser.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.flaptor.util.Execute;
import com.flaptor.util.parser.ParseException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class HyperLinkDocumentFetcher extends HttpUrlFetcher<FetchResult> {
	static final Logger logger = Logger.getLogger(Execute.whoAmI());

	public static interface DocumentParser {
		public FetchResult parse(String url, InputStream inputStream, String contentType, String encoding, FetchResult currentResult) throws ParseException;
	}
	
	private static final String CONTENT_TYPE_HEADER = "Content-Type";
	
	public static class ContentType {
		private Map<String, String> params;
		public String type;

		public ContentType(Map<String, String> params, String type) {
			this.params = params;
			this.type = type;
		}

		public String getParam(String key) {
			return params.get(key);
		}
	}

	private List<DocumentParser> responsibleParsers = Lists.newArrayList(); 
	public HyperLinkDocumentFetcher(HttpResponseHandler responseHandler, String userAgent) {
		super(responseHandler, userAgent);
		
		// initialize responsible parsers
		responsibleParsers.add(new HtmlParser());
		responsibleParsers.add(new PdfParser());
	}

	@Override
	protected FetchResult parse(String url, InputStream is, HttpURLConnection connection) throws ParseException {
		if (url.startsWith("http://") || url.startsWith("https://")) {
			FetchResult result;	

			try {
				result = new FetchResult(url);
			} catch (URISyntaxException e) {
				throw new IllegalArgumentException("Malformed URL: " + url, e);
			}
			
			ContentType contentType = parseContentType(connection.getHeaderField(CONTENT_TYPE_HEADER));
			String charset = contentType.getParam("charset");

			result.setDocumentType(contentType.type);
			
			// COR
			FetchResult finalResult = null;
			for (DocumentParser parser : responsibleParsers) {
				finalResult = parser.parse(url, is, contentType.type, charset, result);
				if (finalResult != null) {
					return finalResult;
				}
			}
			
			return null;
		} else {
			throw new IllegalArgumentException("URL must have http or httpmust have http or https protocol: " + url);
		}
	}

    // This method tries to create an URI from a possibly malformed url.
    public static URI getURI(String url) throws URISyntaxException {
    	URI uri = null;
    	url = url.trim();
    	if (url.startsWith("file:") || url.startsWith("javascript:")) {
    		logger.debug("Can't handle url: "+url);
    	} else {
    		int p = url.indexOf('?');
    		if (p < 0) {
    			try {
    				uri = new URI(url.replace(" ", "%20"));
    			} catch (java.net.URISyntaxException e) {
    				logger.debug("Malformed URI: "+url);
    			}
    		} else {
    			String base, query;
    			int q = url.lastIndexOf('#');
    			if (q < 0) q = url.length();
    			if (p < q) { 
    				base = url.substring(0,p+1);
    				query = url.substring(p+1,q);
    			} else {
    				base = url.substring(0,q)+"?";
    				query = url.substring(p+1);
    			}            
    			// Encode any space in the url. Can't use a url encoder because it would encode stuff like '/' and ':'.
    			base = base.replace(" ", "%20");
    			try {
    				// Re-encode the query part, to handle partially encoded urls.
    				query = java.net.URLEncoder.encode(java.net.URLDecoder.decode(query,"UTF-8"),"UTF-8");
    				query = query.replace("%3D","=").replace("%26","&");
    			} catch (java.io.UnsupportedEncodingException e) {
    				logger.debug("encoding a url", e);
    			}
    			url = base + query;
    			uri = new URI(url);
    		}
    	}
        return uri;
    }

	private static ContentType parseContentType(String contentTypeString) {
		String[] parts = contentTypeString.split(";");
		String type = parts[0];
		Map<String, String> params = Maps.newHashMap();
		for (int i = 1; i < parts.length; i++) {
			String[] keyValue = parts[i].split("=");
			if (keyValue.length == 2) {
				params.put(keyValue[0], keyValue[1]);
			}
		}
		return new ContentType(params, type);
	}

	public static void main(String[] args) throws ParseException, IOException {
		HyperLinkDocumentFetcher fetcher = new HyperLinkDocumentFetcher(null, "me");
		FetchResult result = fetcher.fecthAndParse("http://redpoint.com");

		System.out.println("A:" + result.getLinks());
	}
}
