package com.flaptor.util.parser.http;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.cyberneko.html.parsers.DOMParser;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.QName;
import org.dom4j.Text;
import org.dom4j.io.DOMReader;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import com.flaptor.util.Pair;
import com.flaptor.util.parser.ParseException;
import com.flaptor.util.parser.ParseOutput;
import com.flaptor.util.parser.http.HyperLinkDocumentFetcher.DocumentParser;
import com.google.common.collect.Lists;

public class HtmlParser implements DocumentParser {
	private static final String CYBERNEKO_DEFAULT_DEFAULT_ENCODING = "Windows-1252";
	private static final String CYBERNEKO_DEFAULT_ENCODING_PROPERTY = "http://cyberneko.org/html/properties/default-encoding";

	private BlockingQueue<DOMParser> parsers;

	public HtmlParser() {
		buildParsers();
	}
	
	@Override
	public FetchResult parse(String url, InputStream inputStream, String contentType, String encoding, FetchResult currentResult) throws ParseException {
		if (!contentType.equals("text/html")) {
			// Not the right parser
			return null;
		}
		
		String specifiedEncoding = null;
		if (encoding != null) {
			specifiedEncoding = encoding;
		}
		try {
			Document htmlDocument = getHtmlDocument(url, inputStream, specifiedEncoding);
			
			removeNamespace(htmlDocument.getRootElement());
			
			URI baseUri = getBaseURI(htmlDocument);
			if (baseUri != null) {
				currentResult.setBaseUrl(baseUri);
			}
			removeNonPrintableTags(htmlDocument);
			String text = extractText(htmlDocument.getRootElement());
			List<Pair<String, String>> links = getLinks(htmlDocument, currentResult.getBaseUri());
			
			currentResult.setTitle(getTitle(htmlDocument));
			currentResult.setLinks(links);
			currentResult.setText(text);
			

		} catch (Exception e) {
			throw new ParseException(e);
		}
		
		return currentResult;
	}

	private void buildParsers() {
		int processors = Runtime.getRuntime().availableProcessors();
		parsers = new ArrayBlockingQueue<DOMParser>(processors);

		for (int i = 0; i < processors; i++) {
			DOMParser parser = new org.cyberneko.html.parsers.DOMParser();
			parsers.add(parser);
		}
	}

	private List<Pair<String, String>> getLinks(Document document, URI baseUri) {
		List<Pair<String, String>> result = Lists.newArrayList();
		
		List links = document.selectNodes("//A|//a");
		for (Iterator iter = links.iterator(); iter.hasNext();) {
			Element link = (Element) iter.next();
			Attribute href = link.attribute("href");
			if (null != href) {
				try {
					String url = href.getValue();
					String text = link.getText();
					
			        URI target = HyperLinkDocumentFetcher.getURI(url);
			        if (null != target) {
			            if (null != baseUri) {
			                if (baseUri.getPath() == null || baseUri.getPath().length() == 0) {
			                    baseUri = baseUri.resolve(URI.create("/"));
			                }
			                target = baseUri.resolve(target);
			            }
			            result.add(new Pair<String,String>(target.toString(), text.trim()));
			        }

					
				} catch (URISyntaxException e) {
					HyperLinkDocumentFetcher.logger.debug("Exception occurred, ignoring link "
							+ link.getText() + " at " + href.getValue(), e);
				}
			}
		}
		
		return result;

	}
	
    // Removes the namespace from the given element and its children.
    private void removeNamespace(Element elem) {
        if (null != elem) {
            elem.remove(elem.getNamespace());
            elem.setQName(QName.get(elem.getName(),Namespace.NO_NAMESPACE));
            removeNamespace(elem.content());
        }
    }

    // Removes the namespace from the given elements and their children.
    @SuppressWarnings("unchecked")
    private void removeNamespace(List list) {
        if (null != list) {
            for (Node node : (List<Node>)list) {
                if (node.getNodeType() == Node.ATTRIBUTE_NODE) {
                    ((Attribute)node).setNamespace(Namespace.NO_NAMESPACE);
                } else if (node.getNodeType() == Node.ELEMENT_NODE) {
                    removeNamespace((Element)node);
                }
            }
        }
    }

    private String getTitle(Document htmlDoc){
        Node titleNode = htmlDoc.selectSingleNode("//TITLE|//Title|//title");
        if (null != titleNode) {
            return titleNode.getText();
        }
        
        return null;
    }
    
    
	private DOMParser getParser(String specifiedEncoding)
			throws InterruptedException {
		DOMParser parser = parsers.take();
		if (specifiedEncoding != null) {
			try {
				parser.setProperty(CYBERNEKO_DEFAULT_ENCODING_PROPERTY, specifiedEncoding);
			} catch (SAXNotRecognizedException e) {
				e.printStackTrace();
			} catch (SAXNotSupportedException e) {
				e.printStackTrace();
			}
		}

		return parser;
	}

	private void returnParser(DOMParser parser) {
		try {
			parser.setProperty(CYBERNEKO_DEFAULT_ENCODING_PROPERTY, CYBERNEKO_DEFAULT_DEFAULT_ENCODING);
		} catch (SAXNotRecognizedException e) {
			e.printStackTrace();
		} catch (SAXNotSupportedException e) {
			e.printStackTrace();
		}

		parsers.add(parser);
	}

	public Document getHtmlDocument(String url, InputStream is,
			String specifiedEncoding) throws InterruptedException, Exception {
		Document htmlDoc;
		DOMParser parser = getParser(specifiedEncoding);
		try {
			try {
				// use cyberneko to parse the html documents (even broken ones)
				org.xml.sax.InputSource inputSource = new org.xml.sax.InputSource(
						is);
				parser.parse(inputSource);
			} catch (Exception e) {
				HyperLinkDocumentFetcher.logger.warn("Exception while trying to parse " + url);
				throw e;
			}
			DOMReader reader = new DOMReader();
			try {
				// get the doc that resulted from parsing the text
				org.w3c.dom.Document document = parser.getDocument();
				htmlDoc = reader.read(document);
			} catch (java.lang.StackOverflowError e) {
				HyperLinkDocumentFetcher.logger.warn("Out of stack memory trying to parse " + url);
				throw new Exception(e);
			}
		} finally {
			returnParser(parser);
		}
		return htmlDoc;
	}
	
	public static URI getBaseURI(Document htmlDocument) throws URISyntaxException {
		URI baseUrl = null;
		Node baseNode = htmlDocument.selectSingleNode("//BASE|//Base|//base");
		if (null != baseNode) {
			Attribute href = ((Element) baseNode).attribute("href");
			if (null == href) {
				href = ((Element) baseNode).attribute("HREF");
				if (null == href) {
					href = ((Element) baseNode).attribute("Href");
				}
			}
			if (null != href) {
				String base = href.getValue();
				if (null != base) {
					baseUrl = new URI(base);
				}
			}
		}
		return baseUrl;
	}

    public static void removeNonPrintableTags(Document document) {
    	removeNonPrintableTags(document, document.getRootElement());
    }

    @SuppressWarnings("unchecked")
	public static void removeNonPrintableTags(Document document, Element element) {
    	List children = element.content();
        for (int i = 0; i < children.size(); i++) {
            Node node = (Node)children.get(i);
            if (node instanceof Element) {
                Element inner = (Element)node;
                if (inner.getName().equalsIgnoreCase("script") ||
                        inner.getName().equalsIgnoreCase("style")) {
                    element.remove(inner);
                    i--;
                    continue;
                }
                removeNonPrintableTags(document, inner);
            }
        }
    }

    public static String extractText(final Element e) {
    	StringBuffer buffer = new StringBuffer();
    	extractText(e, buffer);
    	return buffer.toString();
    }
    
    public static void extractText(final Element e, StringBuffer buffer) {
        //String nodeName = e.getName();
        if (!(e.getNodeType() == Node.COMMENT_NODE)) {
            int size = e.nodeCount();
            for (int i = 0; i < size; i++) {
                Node node = e.node(i);                
                if (node instanceof Element) {
                    extractText((Element) node, buffer);
                } else if (node instanceof Text) {
                    String t = node.getText();
                    buffer.append(t);                    
                }
            }
        }
    }

}