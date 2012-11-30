/**
 * 
 */
package org.phiresearchlab.twitter.nlp;

import java.io.FileInputStream;
import java.io.InputStream;

import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import org.apache.log4j.Logger;

/**
 *
 * @author Joel M. Rives
 * Jun 2, 2011
 */
public class Train {
	
	private static Logger LOG = Logger.getLogger(Train.class);
	
	public void detectChunks(String text) {
		String[] sentences = toSentences(text);
		
		for (String sentence: sentences) {
			say(sentence);
			String[] tokens = toTokens(sentence);
			String[] tags = toPartsOfSpeech(tokens);
			String[] chunks = toChunks(tokens, tags);

			for (int i = 0; i < tokens.length; i++) {
				String token = tokens[i];
				String tag = tags[i];
				say(token + " [" + tag + "] " + "[" + chunks[i] + "]");
			}
		}
	}
	
	public void detectPartsOfSpeech(String text) {
		String[] sentences = toSentences(text);
		
		for (String sentence: sentences) {
			say(sentence);
			String[] tokens = toTokens(sentence);
			String[] tags = toPartsOfSpeech(tokens);

			for (int i = 0; i < tokens.length; i++) {
				String token = tokens[i];
				String tag = tags[i];
				say(token + " [" + tag + "]");
			}
		}
		
	}
	
	public void detectSentences(String text) {
		String[] sentences = toSentences(text);
		
		for (String sentence: sentences)
			say(sentence);
	}

	public void detectTokens(String text) {
		String[] sentences = toSentences(text);
		
		for (String sentence: sentences) {
			say(sentence);
			String[] tokens = toTokens(sentence);
			
			for (String token: tokens)
				say(token);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Train trainer = new Train();
//		trainer.detectSentences(" First sentence. Second sentence. ");
//		trainer.detectTokens(" First sentence. Second sentence. ");
//		trainer.detectPartsOfSpeech("Most large cities in the US had morning and afternoon newspapers");
		trainer.detectChunks("Most large cities in the US had morning and afternoon newspapers");
	}
	
	private ChunkerModel createChunkerModel(String filepath) {
		ChunkerModel model;
		try {
			InputStream modelIn = new FileInputStream(filepath);
			model = new ChunkerModel(modelIn);
		} catch (Exception e) {
			LOG.error("Failed to create ChunkerModel", e);
			throw new RuntimeException("Burp!");
		} 
		
		return model;
	}

	private POSModel createPOSModel(String filepath) {
		POSModel model;
		try {
			InputStream modelIn = new FileInputStream(filepath);
			model = new POSModel(modelIn);
		} catch (Exception e) {
			LOG.error("Failed to create POSModel", e);
			throw new RuntimeException("Burp!");
		} 
		
		return model;
	}

	private SentenceModel createSentenceModel(String filepath) {
		SentenceModel model;
		try {
			InputStream modelIn = new FileInputStream(filepath);
			model = new SentenceModel(modelIn);
		} catch (Exception e) {
			LOG.error("Failed to create SentenceModel", e);
			throw new RuntimeException("Burp!");
		} 
		
		return model;
	} 
	
	private TokenizerModel createTokenizerModel(String filepath) {
		TokenizerModel model;
		try {
			InputStream modelIn = new FileInputStream(filepath);
			model = new TokenizerModel(modelIn);
		} catch (Exception e) {
			LOG.error("Failed to create TokenizerModel", e);
			throw new RuntimeException("Burp!");
		} 
		
		return model;
	}
	
	private String[] toChunks(String[] tokens, String[] tags) {
		ChunkerModel model = createChunkerModel("english/en-chunker.bin");
		ChunkerME chunker = new ChunkerME(model);
		return chunker.chunk(tokens, tags);
	}
	
	private String[] toPartsOfSpeech(String[] tokens) {
		POSModel model = createPOSModel("english/en-pos-maxent.bin");
		POSTaggerME tagger = new POSTaggerME(model);
		return tagger.tag(tokens);
	}
	
	private String[] toSentences(String text) {
		SentenceModel model = createSentenceModel("english/en-sent.bin");
		SentenceDetectorME detector = new SentenceDetectorME(model);
		return detector.sentDetect(text);
	}
	
	private String[] toTokens(String sentence) {
		TokenizerModel model = createTokenizerModel("english/en-token.bin");
		Tokenizer tokenizer = new TokenizerME(model);
		return tokenizer.tokenize(sentence);
	}
	
	private void say(String message) {
		System.out.println(message);
	}
}
