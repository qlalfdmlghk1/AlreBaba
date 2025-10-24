package com.ssafy.alrebaba.chat.domain;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatRepository extends MongoRepository<Chat, String> {

    List<Chat> findByChannelId(Long channelId);

}
