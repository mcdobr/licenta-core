package me.mircea.licenta.core.crawl.db.impl;

import me.mircea.licenta.core.crawl.db.model.SelectorType;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class SelectorTypeCodec implements Codec<SelectorType> {

    @Override
    public SelectorType decode(BsonReader reader, DecoderContext decoderContext) {
        return SelectorType.valueOf(reader.readString().toUpperCase());
    }

    @Override
    public void encode(BsonWriter writer, SelectorType value, EncoderContext encoderContext) {
        writer.writeString(value.toString().toLowerCase());
    }

    @Override
    public Class<SelectorType> getEncoderClass() {
        return SelectorType.class;
    }
}
