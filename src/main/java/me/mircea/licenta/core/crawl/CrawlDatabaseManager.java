package me.mircea.licenta.core.crawl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;

public class CrawlDatabaseManager {
	private final MongoClient mongoClient;
	private final MongoDatabase crawlDatabase;
	private final MongoCollection<Document> urls;
	
	private CrawlDatabaseManager() {
		this.mongoClient = MongoClients.create();
		this.crawlDatabase = this.mongoClient.getDatabase("crawldb");
		this.urls = this.crawlDatabase.getCollection("urls");
	}
	
	
	public static final CrawlDatabaseManager instance = new CrawlDatabaseManager();
	
	/**
	 * Upsert the document such that
	 * - the url is the newest value (if there's a need to update to canonical url)
	 * - discoveredTime is the minimum
	 * - referer is the newest value 
	 */
	public void upsertManyUrls(Map<String, Document> urlDocs) {
		UpdateOptions updateOptions = new UpdateOptions().upsert(true);
		
		List<WriteModel<Document>> updateModels = urlDocs.entrySet()
			.stream()
			.map(urlDocPair -> new UpdateOneModel<Document>(new Document("url", urlDocPair.getKey()),
											urlDocPair.getValue(),
											updateOptions))
			.collect(Collectors.toList());
		
		urls.bulkWrite(updateModels, new BulkWriteOptions().ordered(false));
	}
}
