package org.chiknrice.pipes;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import java.nio.charset.StandardCharsets;

/**
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public class StringCodec extends MockPipesCodec<String> {

    static final String EOL;
    static final byte EOL_BYTE;

    static {
        EOL = "\n";
        EOL_BYTE = EOL.getBytes(StandardCharsets.ISO_8859_1)[0];
    }

    @Override
    protected void encode(String message, ProtocolEncoderOutput out) {
        out.write(IoBuffer.wrap(message.concat(EOL).getBytes()));
    }

    @Override
    protected boolean decode(IoBuffer in, ProtocolDecoderOutput out) {
        int eolIndex = in.indexOf(EOL_BYTE);
        if (eolIndex != -1) {
            byte[] bytes = new byte[eolIndex - in.position() + 1];
            in.get(bytes);
            out.write(new String(bytes, StandardCharsets.ISO_8859_1).trim());
            return in.hasRemaining();
        }
        return true;
    }
}
