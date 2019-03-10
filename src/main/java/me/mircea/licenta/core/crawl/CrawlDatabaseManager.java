package me.mircea.licenta.core.crawl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;

import me.mircea.licenta.core.crawl.db.impl.PageTypeCodec;
import me.mircea.licenta.core.crawl.db.model.Page;
import me.mircea.licenta.core.crawl.db.model.PageType;

import static org.bson.codecs.configuration.CodecRegistries.fromCodecs;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;
import static com.mongodb.client.model.Filters.regex;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;
import static com.mongodb.client.model.Updates.min;
import static com.mongodb.client.model.Updates.max;


public class CrawlDatabaseManager {
	private static final Logger logger = LoggerFactory.getLogger(CrawlDatabaseManager.class);
	
	private final MongoClient mongoClient;
	private final MongoDatabase crawlDatabase;
	private final MongoCollection<Page> pagesCollection;
	
	private CrawlDatabaseManager() {
		final String secretFile = "secret.properties";
		final InputStream secretInputStream = CrawlRequest.class.getResourceAsStream("/" + secretFile);
		
		Properties secret = new Properties();
		try {
			secret.load(secretInputStream);
		} catch (IOException e) {
			logger.error("Could not find the credentials file for connecting to crawl database {}. EXITING", e);
			System.exit(-1);
		}
		
		this.mongoClient = MongoClients.create(secret.getProperty("connectionString"));
		
		final CodecRegistry pojoCodecRegistry = fromRegistries(
				MongoClientSettings.getDefaultCodecRegistry(),
				fromCodecs(new PageTypeCodec()),
				fromProviders(
						PojoCodecProvider.builder().automatic(true).build())
				);
		this.crawlDatabase = this.mongoClient.getDatabase("crawldb").withCodecRegistry(pojoCodecRegistry);
		this.pagesCollection = this.crawlDatabase.getCollection("pages", Page.class);
		
		this.pagesCollection.createIndex(Indexes.ascending("url"));
		this.pagesCollection.createIndex(Indexes.ascending("retrievedTime"));
	}
	
	public static final CrawlDatabaseManager instance = new CrawlDatabaseManager();
	

	/**
	 * Upsert the document such that
	 * - the url is the newest value (if there's a need to update to canonical url)
	 * - discoveredTime is the minimum
	 * - referer is the newest value 
	 */
	public void upsertManyPages(List<Page> pages) {
		UpdateOptions updateOptions = new UpdateOptions().upsert(true);
		
		List<WriteModel<Page>> updateModels = pages.stream()
			.map(page -> new UpdateOneModel<Page>(
					or(
							eq("_id", page.getId()),
							eq("url", page.getUrl())
					),
					combine(
							set("url", page.getUrl()),
							set("referer", page.getReferer()),
							set("type", page.getType()),
							set("title", page.getTitle()),
							min("discoveredTime", page.getDiscoveredTime()),
							max("retrievedTime", page.getRetrievedTime())
					),
					updateOptions))
			.collect(Collectors.toList());
		
		pagesCollection.bulkWrite(updateModels);
	}
	
	public void upsertOnePage(Page page) {
		UpdateOptions updateOptions = new UpdateOptions().upsert(true);
		
		try {
		pagesCollection.updateOne(
				or( eq("_id", page.getId()),
					eq("url", page.getUrl()))
			,combine(
					set("url", page.getUrl()),
					set("referer", page.getReferer()),
					set("type", page.getType()),
					set("title", page.getTitle()),
					min("discoveredTime", page.getDiscoveredTime()),
					max("retrievedTime", page.getRetrievedTime())
			),
			updateOptions);
		
		} catch (Exception e) {
			logger.error("Encountered {}", e);
		}
		
	}
	
	/**
	 * Get possible product pages ordered by retrievedTime ascending
	 */
	public FindIterable<Page> getPossibleProductPages(String domain) {
		Bson getProductPagesOfDomain = and(Arrays.asList(
				regex("url", domain),
				or(Arrays.asList(
						eq("type", PageType.PRODUCT),
						eq("type", PageType.UNKNOWN)
				))
		));
		
		return pagesCollection.find(getProductPagesOfDomain).sort(new BasicDBObject("retrievedTime", 1));
	}
}
