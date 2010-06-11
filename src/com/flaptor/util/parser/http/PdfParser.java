/**
 * 
 */
package com.flaptor.util.parser.http;

import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import com.flaptor.util.Execute;
import com.flaptor.util.Pair;
import com.flaptor.util.parser.ParseException;
import com.flaptor.util.parser.http.HyperLinkDocumentFetcher.DocumentParser;

public class PdfParser implements DocumentParser {
	@Override
	public FetchResult parse(String url, InputStream inputStream, String contentType, String encoding, FetchResult currentResult) throws ParseException {
		Pair<String, String> textFromPDF;
		try {
			textFromPDF = extractTextFromPDF(inputStream);
		} catch (IOException e) {
			throw new ParseException(e);
		}
		currentResult.setTitle(textFromPDF.first());
		currentResult.setText(textFromPDF.last());
		
		return currentResult;
	}
	
	public static Pair<String, String> extractTextFromPDF(InputStream is) throws IOException {
		PDDocument document = null;
		try {
			document = PDDocument.load(is);
			PDFTextStripper pdfTextStripper = new PDFTextStripper("utf-8");
			return new Pair<String, String>(document.getDocumentInformation()
					.getTitle(), pdfTextStripper.getText(document));
		} finally {
			Execute.close(document);
		}
	}
	
}