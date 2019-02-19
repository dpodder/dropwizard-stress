package com.example.tools.stressclient.resources;

import com.fasterxml.jackson.databind.JsonNode;
import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import lombok.Getter;

/**
 * Simple wrapper class for a JsonNode object and an associated (non-secure) CRC32 checksum.
 */
public class ChecksumContainer
{
    /**
     * The stored JsonNode object.
     */
    @Getter
    private final JsonNode object;

    /**
     * Checksum of the data.
     */
    @Getter
    private final long crc32;

    /**
     * Memoized hashCode.
     */
    private final int hashCode;

    /**
     * Basic constructor.
     * @param object The JsonNode whose reference should be stored in this container.
     */
    public ChecksumContainer(final JsonNode object)
    {
        this.object = object;
        byte[] raw = object.toString().getBytes(StandardCharsets.UTF_8);
        Checksum crc = new CRC32();
        crc.update(raw, 0, raw.length);
        this.crc32 = crc.getValue();
        this.hashCode = computeHashCode();
    }

    @Override
    public boolean equals(final Object o)
    {
        // Generated via delombok
        if (o == this)
        {
            return true;
        }
        if (!(o instanceof ChecksumContainer))
        {
            return false;
        }
        final ChecksumContainer other = (ChecksumContainer) o;
        if (!other.canEqual(this))
        {
            return false;
        }
        final Object thisObject = this.object;
        final Object otherObject = other.object;
        if (thisObject == null ? otherObject != null : !thisObject.equals(otherObject))
        {
            return false;
        }
        return this.crc32 == other.crc32;
    }

    protected boolean canEqual(final Object other)
    {
        // Generated via delombok
        return other instanceof ChecksumContainer;
    }

    @Override
    public int hashCode()
    {
        return hashCode;
    }

    private int computeHashCode()
    {
        // Original code generated via delombok
        final int prime = 59;
        int result = 1;
        final Object thisObject = this.object;
        result = result * prime + (thisObject == null ? 43 : thisObject.hashCode());
        final long thisCrc32 = this.crc32;
        result = result * prime + (int) (thisCrc32 >>> 32 ^ thisCrc32);
        return result;
    }
}
