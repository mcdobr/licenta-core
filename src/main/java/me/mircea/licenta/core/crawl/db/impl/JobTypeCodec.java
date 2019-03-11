package me.mircea.licenta.core.crawl.db.impl;

import me.mircea.licenta.core.crawl.db.model.JobType;
import me.mircea.licenta.core.crawl.db.model.PageType;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class JobTypeCodec implements Codec<JobType>{
	@Override
	public JobType decode(BsonReader reader, DecoderContext decoderContext) {
		return JobType.valueOf(reader.readString().toUpperCase());
	}
	
	@Override
	public void encode(BsonWriter writer, JobType value, EncoderContext encoderContext) {
		writer.writeString(value.toString().toLowerCase());
	}

	@Override
	public Class<JobType> getEncoderClass() {
		return JobType.class;
	}
}
