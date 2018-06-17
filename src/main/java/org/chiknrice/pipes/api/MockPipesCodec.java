package org.chiknrice.pipes.api;

import org.apache.mina.core.buffer.IoBuffer;

/**
 * The {@code MockPipesCodec} provides APIs to encode message of type {@code O} to byte[] and decode bytes to message of
 * type {@code I}.
 *
 * @author <a href="mailto:chiknrice@gmail.com">Ian Bondoc</a>
 */
public interface MockPipesCodec<I, O> {

    /**
     * Encodes the message to byte[]
     *
     * @param message
     * @return
     */
    byte[] encode(O message);

    /**
     * Try to decode the message from the {@code IoBuffer}
     *
     * @param in
     * @return the decoded value or null if the contents of the buffer is not sufficient to decode the message
     */
    I tryToDecode(IoBuffer in);

}
