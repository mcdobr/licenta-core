package me.mircea.licenta.core.crawl.db;

import com.google.common.base.Preconditions;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoWriteException;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import com.mongodb.client.result.UpdateResult;
import me.mircea.licenta.core.crawl.db.impl.JobStatusCodec;
import me.mircea.licenta.core.crawl.db.impl.JobTypeCodec;
import me.mircea.licenta.core.crawl.db.impl.PageTypeCodec;
import me.mircea.licenta.core.crawl.db.impl.SelectorTypeCodec;
import me.mircea.licenta.core.crawl.db.model.*;
import org.bson.BsonValue;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;
import static org.bson.codecs.configuration.CodecRegistries.*;


public class CrawlDatabaseManager {
	public static final CrawlDatabaseManager instance = new CrawlDatabaseManager();
	private static final Logger LOGGER = LoggerFactory.getLogger(CrawlDatabaseManager.class);
	
	private final MongoClient mongoClient;
	private final MongoDatabase crawlDatabase;
	private final MongoCollection<Page> pagesCollection;
	private final MongoCollection<Job> jobsCollection;
	private final MongoCollection<Wrapper> wrappersCollection;

	private CrawlDatabaseManager() {
		final String secretFile = "secret.properties";
		final InputStream secretInputStream = Job.class.getResourceAsStream("/" + secretFile);
		
		Properties secret = new Properties();
		try {
			secret.load(secretInputStream);
		} catch (IOException e) {
			LOGGER.error("Could not find the credentials file for connecting to crawl database {}. EXITING", e);
			System.exit(-1);
		}
		this.mongoClient = MongoClients.create(secret.getProperty("connectionString"));
		
		final CodecRegistry pojoCodecRegistry = fromRegistries(
				MongoClientSettings.getDefaultCodecRegistry(),
				fromCodecs(new PageTypeCodec(),
						new JobTypeCodec(),
						new JobStatusCodec(),
						new SelectorTypeCodec()),
				fromProviders(
						PojoCodecProvider.builder().automatic(true).build())
				);
		this.crawlDatabase = this.mongoClient.getDatabase("crawldb").withCodecRegistry(pojoCodecRegistry);

		this.pagesCollection = this.crawlDatabase.getCollection("pages", Page.class);
		this.pagesCollection.createIndex(Indexes.ascending("url"), new IndexOptions().unique(true));
		this.pagesCollection.createIndex(Indexes.ascending("retrievedTime"));

		this.jobsCollection = this.crawlDatabase.getCollection("jobs", Job.class);
		this.jobsCollection.createIndex(Indexes.ascending("status"));

		this.wrappersCollection = this.crawlDatabase.getCollection("wrappers", Wrapper.class);
		this.wrappersCollection.createIndex(Indexes.ascending("domain"));
	}
	

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
							max("retrievedTime", page.getRetrievedTime()),
                            set("lastJob", page.getLastJob())
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
						max("retrievedTime", page.getRetrievedTime()),
						set("lastJob", page.getLastJob())
				),
				updateOptions);
		
		} catch (MongoWriteException e ) {
			if (e.getError().getCode() == 11000) {
				pagesCollection.deleteOne(eq("_id", page.getId()));
				LOGGER.info("Removed duplicate url from crawl database {}", page.getUrl());
			}
		} catch (Exception e) {
			LOGGER.error("Encountered a MongoDB error: {}", e);
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

		return pagesCollection.find(getProductPagesOfDomain).sort(new BasicDBObject("retrievedTime", 1)).batchSize(10);
	}

	public void upsertJob(Job job)
	{
		UpdateOptions updateOptions = new UpdateOptions().upsert(true);

		UpdateResult updateResult = jobsCollection.updateOne(
				eq("_id", job.getId()),
				combine(
						set("seed", job.getSeed()),
						set("domain", job.getDomain()),
						set("status", job.getStatus()),
						set("type", job.getType()),
						min("start", job.getStart()),
						set("end", job.getEnd())
						//TODO: set("robotRules", job.getRobotRules())
				),
				updateOptions
		);

		BsonValue objectIdValue = updateResult.getUpsertedId();
		if (job.getId() != null && objectIdValue != null)
			job.setId(objectIdValue.asObjectId().getValue());
	}

	public Job getJobById(ObjectId id) {
		return jobsCollection.find(eq("_id", id)).first();
	}

	public Iterable<Job> getActiveJobsByType(JobType jobType) {
		return jobsCollection.find(and(eq("type", jobType),
										eq("status", JobStatus.ACTIVE)));
	}


	public Optional<Wrapper> getWrapperForDomain(String domain) {
		Preconditions.checkNotNull(domain);
		Wrapper queryResult = wrappersCollection.find(eq("domain", domain)).first();
		return Optional.ofNullable(queryResult);
	}
}
