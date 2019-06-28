package me.mircea.licenta.core.crawl.db.impl;

import me.mircea.licenta.core.crawl.db.model.JobStatus;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class JobStatusCodec implements Codec<JobStatus> {
    @Override
    public JobStatus decode(BsonReader reader, DecoderContext decoderContext) {
        return JobStatus.valueOf(reader.readString().toUpperCase());
    }

    @Override
    public void encode(BsonWriter writer, JobStatus value, EncoderContext encoderContext) {
        writer.writeString(value.toString().toLowerCase());
    }

    @Override
    public Class<JobStatus> getEncoderClass() {
        return JobStatus.class;
    }
}
