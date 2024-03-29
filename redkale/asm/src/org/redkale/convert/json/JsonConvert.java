/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.redkale.convert.json;

import java.io.*;
import java.lang.reflect.*;
import java.nio.*;
import java.nio.charset.*;
import java.util.function.*;
import org.redkale.convert.*;
import org.redkale.util.*;

/**
 *
 * <p>
 * 详情见: https://redkale.org
 *
 * @author zhangjx
 */
@SuppressWarnings("unchecked")
public class JsonConvert extends TextConvert<JsonReader, JsonWriter> {

    public static final Type TYPE_MAP_STRING_STRING = new TypeToken<java.util.HashMap<String, String>>() {
    }.getType();

    private static final ObjectPool<JsonReader> readerPool = JsonReader.createPool(Integer.getInteger("convert.json.pool.size", 16));

    private static final ObjectPool<JsonWriter> writerPool = JsonWriter.createPool(Integer.getInteger("convert.json.pool.size", 16));

    private final boolean tiny;

    protected JsonConvert(JsonFactory factory, boolean tiny) {
        super(factory);
        this.tiny = tiny;
    }

    @Override
    public JsonFactory getFactory() {
        return (JsonFactory) factory;
    }

    public static JsonConvert root() {
        return JsonFactory.root().getConvert();
    }

    @Override
    public JsonConvert newConvert(final BiFunction<Attribute, Object, Object> fieldFunc) {
        return newConvert(fieldFunc, null);
    }

    @Override
    public JsonConvert newConvert(final BiFunction<Attribute, Object, Object> fieldFunc, Function<Object, ConvertField[]> objExtFunc) {
        return new JsonConvert(getFactory(), tiny) {
            @Override
            protected <S extends JsonWriter> S configWrite(S writer) {
                return fieldFunc(writer, fieldFunc, objExtFunc);
            }
        };
    }

    //------------------------------ reader -----------------------------------------------------------
    public JsonReader pollJsonReader(final ByteBuffer... buffers) {
        return new JsonByteBufferReader((ConvertMask) null, buffers);
    }

    public JsonReader pollJsonReader(final InputStream in) {
        return new JsonStreamReader(in);
    }

    public JsonReader pollJsonReader() {
        return readerPool.get();
    }

    public void offerJsonReader(final JsonReader in) {
        if (in != null) readerPool.accept(in);
    }

    //------------------------------ writer -----------------------------------------------------------
    public JsonByteBufferWriter pollJsonWriter(final Supplier<ByteBuffer> supplier) {
        return configWrite(new JsonByteBufferWriter(tiny, supplier));
    }

    public JsonWriter pollJsonWriter(final OutputStream out) {
        return configWrite(new JsonStreamWriter(tiny, out));
    }

    public JsonWriter pollJsonWriter(final Charset charset, final OutputStream out) {
        return configWrite(new JsonStreamWriter(tiny, charset, out));
    }

    public JsonWriter pollJsonWriter() {
        return configWrite(writerPool.get().tiny(tiny));
    }

    public void offerJsonWriter(final JsonWriter writer) {
        if (writer != null) writerPool.accept(writer);
    }

    //------------------------------ convertFrom -----------------------------------------------------------
    @Override
    public <T> T convertFrom(final Type type, final byte[] bytes) {
        if (bytes == null) return null;
        return convertFrom(type, new String(bytes, StandardCharsets.UTF_8));
    }

    public <T> T convertFrom(final Type type, final String text) {
        if (text == null) return null;
        return convertFrom(type, Utility.charArray(text));
    }

    public <T> T convertFrom(final Type type, final char[] text) {
        if (text == null) return null;
        return convertFrom(type, text, 0, text.length);
    }

    public <T> T convertFrom(final Type type, final char[] text, final int start, final int len) {
        if (text == null || type == null) return null;
        final JsonReader in = readerPool.get();
        in.setText(text, start, len);
        T rs = (T) factory.loadDecoder(type).convertFrom(in);
        readerPool.accept(in);
        return rs;
    }

    public <T> T convertFrom(final Type type, final InputStream in) {
        if (type == null || in == null) return null;
        return (T) factory.loadDecoder(type).convertFrom(new JsonStreamReader(in));
    }

    @Override
    public <T> T convertFrom(final Type type, final ByteBuffer... buffers) {
        if (type == null || buffers == null || buffers.length == 0) return null;
        return (T) factory.loadDecoder(type).convertFrom(new JsonByteBufferReader((ConvertMask) null, buffers));
    }

    @Override
    public <T> T convertFrom(final Type type, final ConvertMask mask, final ByteBuffer... buffers) {
        if (type == null || buffers == null || buffers.length == 0) return null;
        return (T) factory.loadDecoder(type).convertFrom(new JsonByteBufferReader(mask, buffers));
    }

    public <T> T convertFrom(final Type type, final JsonReader reader) {
        if (type == null) return null;
        @SuppressWarnings("unchecked")
        T rs = (T) factory.loadDecoder(type).convertFrom(reader);
        return rs;
    }

    //返回非null的值是由String、ArrayList、HashMap任意组合的对象
    public <V> V convertFrom(final String text) {
        if (text == null) return null;
        return (V) convertFrom(Utility.charArray(text));
    }

    //返回非null的值是由String、ArrayList、HashMap任意组合的对象
    public <V> V convertFrom(final char[] text) {
        if (text == null) return null;
        return (V) convertFrom(text, 0, text.length);
    }

    //返回非null的值是由String、ArrayList、HashMap任意组合的对象
    public <V> V convertFrom(final char[] text, final int start, final int len) {
        if (text == null) return null;
        final JsonReader in = readerPool.get();
        in.setText(text, start, len);
        Object rs = new AnyDecoder(factory).convertFrom(in);
        readerPool.accept(in);
        return (V) rs;
    }

    //返回非null的值是由String、ArrayList、HashMap任意组合的对象
    public <V> V convertFrom(final InputStream in) {
        if (in == null) return null;
        return (V) new AnyDecoder(factory).convertFrom(new JsonStreamReader(in));
    }

    //返回非null的值是由String、ArrayList、HashMap任意组合的对象
    public <V> V convertFrom(final ByteBuffer... buffers) {
        if (buffers == null || buffers.length == 0) return null;
        return (V) new AnyDecoder(factory).convertFrom(new JsonByteBufferReader((ConvertMask) null, buffers));
    }

    //返回非null的值是由String、ArrayList、HashMap任意组合的对象
    public <V> V convertFrom(final ConvertMask mask, final ByteBuffer... buffers) {
        if (buffers == null || buffers.length == 0) return null;
        return (V) new AnyDecoder(factory).convertFrom(new JsonByteBufferReader(mask, buffers));
    }

    //返回非null的值是由String、ArrayList、HashMap任意组合的对象
    public <V> V convertFrom(final JsonReader reader) {
        if (reader == null) return null;
        return (V) new AnyDecoder(factory).convertFrom(reader);
    }

    //------------------------------ convertTo -----------------------------------------------------------
    @Override
    public String convertTo(final Object value) {
        if (value == null) return "null";
        return convertTo(value.getClass(), value);
    }

    @Override
    public String convertTo(final Type type, final Object value) {
        if (type == null) return null;
        if (value == null) return "null";
        final JsonWriter writer = pollJsonWriter();
        writer.specify(type);
        factory.loadEncoder(type).convertTo(writer, value);
        String result = writer.toString();
        writerPool.accept(writer);
        return result;
    }

    @Override
    public String convertMapTo(final Object... values) {
        if (values == null) return "null";
        final JsonWriter writer = pollJsonWriter();
        ((AnyEncoder) factory.getAnyEncoder()).convertMapTo(writer, values);
        String result = writer.toString();
        writerPool.accept(writer);
        return result;
    }

    public void convertTo(final OutputStream out, final Object value) {
        if (value == null) {
            pollJsonWriter(out).writeNull();
        } else {
            convertTo(out, value.getClass(), value);
        }
    }

    public void convertTo(final OutputStream out, final Type type, final Object value) {
        if (type == null) return;
        if (value == null) {
            pollJsonWriter(out).writeNull();
        } else {
            final JsonWriter writer = pollJsonWriter();
            writer.specify(type);
            factory.loadEncoder(type).convertTo(writer, value);
            byte[] bs = writer.toBytes();
            writerPool.accept(writer);
            try {
                out.write(bs);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void convertMapTo(final OutputStream out, final Object... values) {
        if (values == null) {
            pollJsonWriter(out).writeNull();
        } else {
            final JsonWriter writer = pollJsonWriter();
            ((AnyEncoder) factory.getAnyEncoder()).convertMapTo(writer, values);
            byte[] bs = writer.toBytes();
            writerPool.accept(writer);
            try {
                out.write(bs);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public ByteBuffer[] convertTo(final Supplier<ByteBuffer> supplier, final Object value) {
        if (supplier == null) return null;
        JsonByteBufferWriter out = pollJsonWriter(supplier);
        if (value == null) {
            out.writeNull();
        } else {
            factory.loadEncoder(value.getClass()).convertTo(out, value);
        }
        return out.toBuffers();
    }

    @Override
    public ByteBuffer[] convertTo(final Supplier<ByteBuffer> supplier, final Type type, final Object value) {
        if (supplier == null || type == null) return null;
        JsonByteBufferWriter out = pollJsonWriter(supplier);
        if (value == null) {
            out.writeNull();
        } else {
            out.specify(type);
            factory.loadEncoder(type).convertTo(out, value);
        }
        return out.toBuffers();
    }

    @Override
    public ByteBuffer[] convertMapTo(final Supplier<ByteBuffer> supplier, final Object... values) {
        if (supplier == null) return null;
        JsonByteBufferWriter out = pollJsonWriter(supplier);
        if (values == null) {
            out.writeNull();
        } else {
            ((AnyEncoder) factory.getAnyEncoder()).convertMapTo(out, values);
        }
        return out.toBuffers();
    }

    public void convertTo(final JsonWriter writer, final Object value) {
        if (value == null) {
            writer.writeNull();
        } else {
            factory.loadEncoder(value.getClass()).convertTo(writer, value);
        }
    }

    public void convertTo(final JsonWriter writer, final Type type, final Object value) {
        if (type == null) return;
        if (value == null) {
            writer.writeNull();
        } else {
            writer.specify(type);
            factory.loadEncoder(type).convertTo(writer, value);
        }
    }

    public void convertMapTo(final JsonWriter writer, final Object... values) {
        if (values == null) {
            writer.writeNull();
        } else {
            ((AnyEncoder) factory.getAnyEncoder()).convertMapTo(writer, values);
        }
    }

    public JsonWriter convertToWriter(final Object value) {
        if (value == null) return null;
        return convertToWriter(value.getClass(), value);
    }

    public JsonWriter convertToWriter(final Type type, final Object value) {
        if (type == null) return null;
        final JsonWriter writer = pollJsonWriter();
        writer.specify(type);
        factory.loadEncoder(type).convertTo(writer, value);
        return writer;
    }

    public JsonWriter convertMapToWriter(final Object... values) {
        final JsonWriter writer = pollJsonWriter();
        ((AnyEncoder) factory.getAnyEncoder()).convertMapTo(writer, values);
        return writer;
    }
}
