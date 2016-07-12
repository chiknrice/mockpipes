package org.chiknrice.pipes;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.*;

/**
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public abstract class MockPipesCodec<T> implements ProtocolCodecFactory {

    private final ProtocolEncoder encoder;
    private final ProtocolDecoder decoder;

    public MockPipesCodec() {
        encoder = new ProtocolEncoderAdapter() {
            @Override
            public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
                MockPipesCodec.this.encode((T) message, out);
            }
        };
        decoder = new CumulativeProtocolDecoder() {
            @Override
            protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
                return MockPipesCodec.this.decode(in, out);
            }
        };
    }

    @Override
    public ProtocolEncoder getEncoder(IoSession session) throws Exception {
        return encoder;
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession session) throws Exception {
        return decoder;
    }

    protected abstract void encode(T message, ProtocolEncoderOutput out);

    protected abstract boolean decode(IoBuffer in, ProtocolDecoderOutput out);

}
