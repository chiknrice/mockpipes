package org.chiknrice.pipes;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.chiknrice.pipes.api.MockPipesCodec;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public class StringCodec implements MockPipesCodec<String, String> {

    static final String EOL;
    static final byte EOL_BYTE;

    static {
        EOL = "\n";
        EOL_BYTE = EOL.getBytes(StandardCharsets.ISO_8859_1)[0];
    }

    @Override
    public byte[] encode(String message) {
        return message.concat(EOL).getBytes();
    }

    @Override
    public String tryToDecode(IoBuffer in) {
        int eolIndex = in.indexOf(EOL_BYTE);
        if (eolIndex != -1) {
            byte[] bytes = new byte[eolIndex - in.position() + 1];
            in.get(bytes);
            return new String(bytes, StandardCharsets.ISO_8859_1).trim();
        }
        return null;
    }
}
