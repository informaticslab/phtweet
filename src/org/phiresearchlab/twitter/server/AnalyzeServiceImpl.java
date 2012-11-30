package org.phiresearchlab.twitter.server;


import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import opennlp.tools.chunker.Chunker;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerModel;

import org.apache.log4j.Logger;
import org.phiresearchlab.twitter.client.AnalyzeService;
import org.phiresearchlab.twitter.dao.AssociationsDAO;
import org.phiresearchlab.twitter.dao.FilterTermDAO;
import org.phiresearchlab.twitter.dao.TweetDAO;
import org.phiresearchlab.twitter.domain.Associations;
import org.phiresearchlab.twitter.domain.FilterTerm;
import org.phiresearchlab.twitter.domain.Tweet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

@Service("analyzeService")
public class AnalyzeServiceImpl implements AnalyzeService {
	
	private static Logger LOG = Logger.getLogger(AnalyzeServiceImpl.class);
	
	private Chunker chunker;
	private POSTagger tagger;
	private SentenceDetector detector;
	private Tokenizer tokenizer;
	private boolean analyzing = false;
	private List<FilterTerm> filterTerms;
	
	@Autowired private TweetDAO tweetDAO;
	@Autowired private FilterTermDAO filterTermDAO;
	@Autowired private AssociationsDAO associationsDAO;
	
	@Autowired private PlatformTransactionManager transactionManager;
	private TransactionTemplate transactionTemplate = null;
	
	@PostConstruct
	public void init() {
//		chunker = new ChunkerME(createChunkerModel("english/en-chunker.bin"));
//		tagger = new POSTaggerME(createPOSModel("english/en-pos-maxent.bin"));
//		detector = new SentenceDetectorME(createSentenceModel("english/en-sent.bin"));
//		tokenizer = new TokenizerME(createTokenizerModel("english/en-token.bin"));	
	}

	private static final long WAIT = 1000 * 60 * 3; // 3 minutes
	
	public void startAnalysis() {
		filterTerms = filterTermDAO.findAll();
		analyzing = true;
		
		while (analyzing) {
			List<Tweet> tweets = tweetDAO.findNextUnanalyzed(20);
			
			if (tweets.size() == 0) {
				try {
					Thread.sleep(WAIT);
				} catch (InterruptedException e) {
					LOG.info("Sleep interupted");
				}
			}
			
			if (!analyzing)
				break;
			
			for (Tweet tweet: tweets) {
				analyzeTweet(tweet);
				tweet.setAnalyzed(true);
				updateTweet(tweet);
			}
		}
	}
	
	public void stopAnalysis() {
		analyzing = false;
	}
	
	
	private void analyzeTweet(Tweet tweet) {
		String text = tweet.getText();
		String[] sentences = detector.sentDetect(text);
		
	
		for (String sentence: sentences) {
			String[] tokens = tokenizer.tokenize(sentence);
			tokens = filterTokens(tokens);
			String[] tags = tagger.tag(tokens);
//			String[] chunks = chunker.chunk(tokens, tags);
			List<String> terms = extractTerms(tokens, tags);
			List<String> filters = new ArrayList<String>();

			for (String term: terms)
				if (isFilterTerm(term))
					filters.add(term);
			
			for (String filter: filters) {
				StringBuffer buffer = new StringBuffer();
				for (String term: terms) {
					if (!term.equals(filter))
						buffer.append(term + ",");
				}
				String associations = buffer.toString();
				if (associations.length() > 0) {
					Associations assoc = new Associations(filter, buffer.toString(), tweet.getCreatedAt());
					persistAssociations(assoc);
				}
			}
		}
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
	
	private List<String> extractTerms(String[] tokens, String[] tags) {
		List<String> terms = new ArrayList<String>();
		
		for (int i = 0; i < tokens.length; i++) {
			String tag = tags[i];
			if (tag.equals("NN") || tag.equals("NNS") || isFilterTerm(tokens[i]))
				terms.add(tokens[i]);
		}
		
		return terms;
	}
	
	private boolean isFilterTerm(String term) {
		for (FilterTerm filterTerm: filterTerms) {
			if (term.equalsIgnoreCase(filterTerm.getName()))
				return true;
		}
		return false;
	}
	
	private String[] filterTokens(String[] tokens) {
		List<String> list = new ArrayList<String>();
		
		for (String token: tokens) {
			if (token.equals("RT"))
				continue;
			if (token.startsWith("@"))
				continue;
			if (token.endsWith(":"))
				continue;
			if (token.startsWith("http"))
				continue;
			if (token.startsWith("://"))
				continue;
			if (token.startsWith("#"))
				continue;
			list.add(token);
		}
		
		String[] filtered = new String[list.size()];
		return list.toArray(filtered);
	}

	private TransactionTemplate getTransactionTemplate() {
		if (null == transactionTemplate)
			transactionTemplate = new TransactionTemplate(transactionManager);
		return transactionTemplate;
	}
		
	private void persistAssociations(final Associations assoc) {
		TransactionTemplate transaction = getTransactionTemplate();			
		transaction.execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus arg0) {
				associationsDAO.persist(assoc);
			}			
		});
	}
	
	private void updateTweet(final Tweet tweet) {
		TransactionTemplate transaction = getTransactionTemplate();			
		transaction.execute(new TransactionCallbackWithoutResult() {
			protected void doInTransactionWithoutResult(TransactionStatus arg0) {
				tweetDAO.merge(tweet);
			}			
		});
	}
	
}
