package com.ssafy.alrebaba.common.configuration.kafka;

import java.io.OutputStream;

import com.ssafy.alrebaba.code.dto.request.CRDTMessage;

import io.jsonwebtoken.io.SerializationException;
import io.jsonwebtoken.io.Serializer;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CRDTMessageSerializer implements Serializer<CRDTMessage> {

    @Override
    public byte[] serialize(CRDTMessage crdtMessage) throws SerializationException {
        return new byte[0];
    }

    @Override
    public void serialize(CRDTMessage crdtMessage, OutputStream out) throws SerializationException {

    }
}