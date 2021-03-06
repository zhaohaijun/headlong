package com.esaulpaugh.headlong.rlp;

import com.esaulpaugh.headlong.rlp.util.RLPIterator;

/**
 * For iterating over sequentially encoded RLP items.
 */
public class SequenceIterator implements RLPIterator {

    private final RLPDecoder decoder;
    private final byte[] rlp;
    private int index;
    private final int end;

    SequenceIterator(RLPDecoder decoder, byte[] rlp, int start, int end) {
        this.decoder = decoder;
        this.rlp = rlp;
        this.index = start;
        this.end = end;
    }

    @Override
    public boolean hasNext() {
        return index < end;
    }

    @Override
    public RLPItem next() throws DecodeException {
        RLPItem item = decoder.wrap(rlp, index);
        this.index = item.endIndex;
        return item;
    }
}
