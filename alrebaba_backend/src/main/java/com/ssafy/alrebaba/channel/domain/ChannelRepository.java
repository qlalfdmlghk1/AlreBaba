package com.ssafy.alrebaba.channel.domain;

import com.ssafy.alrebaba.study.domain.Study;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChannelRepository extends JpaRepository<Channel, Long> {
    List<Channel> findAllByStudy(Study study);
}
