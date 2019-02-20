package me.mircea.licenta.core.crawl.db.impl;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import me.mircea.licenta.core.crawl.db.model.PageType;

public class PageTypeCodec implements Codec<PageType>{
	@Override
	public PageType decode(BsonReader reader, DecoderContext decoderContext) {
		return PageType.valueOf(reader.readString());
	}
	
	@Override
	public void encode(BsonWriter writer, PageType value, EncoderContext encoderContext) {
		writer.writeString(value.toString().toLowerCase());
	}

	@Override
	public Class<PageType> getEncoderClass() {
		return PageType.class;
	}
}
