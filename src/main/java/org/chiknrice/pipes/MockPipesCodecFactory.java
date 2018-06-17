package org.chiknrice.pipes;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.*;
import org.chiknrice.pipes.api.MockPipesCodec;

class MockPipesCodecFactory<I, O> implements ProtocolCodecFactory {

    private final ProtocolEncoder encoder;
    private final ProtocolDecoder decoder;

    MockPipesCodecFactory(MockPipesCodec<I, O> mockPipesCodec) {
        encoder = new ProtocolEncoderAdapter() {
            @Override
            public void encode(IoSession session, Object message, ProtocolEncoderOutput out) {
                out.write(IoBuffer.wrap(mockPipesCodec.encode((O) message)));
            }
        };
        decoder = new CumulativeProtocolDecoder() {
            @Override
            protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) {
                int position = in.position();
                I message = mockPipesCodec.tryToDecode(in);
                if (message != null) {
                    out.write(message);
                    return in.hasRemaining();
                } else {
                    // reset
                    in.position(position);
                    return false;
                }
            }
        };
    }

    @Override
    public final ProtocolEncoder getEncoder(IoSession session) {
        return encoder;
    }

    @Override
    public final ProtocolDecoder getDecoder(IoSession session) {
        return decoder;
    }

}
