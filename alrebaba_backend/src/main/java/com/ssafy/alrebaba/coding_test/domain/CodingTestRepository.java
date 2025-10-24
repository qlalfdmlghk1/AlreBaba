package com.ssafy.alrebaba.coding_test.domain;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ssafy.alrebaba.channel.domain.Channel;

public interface CodingTestRepository extends JpaRepository<CodingTest, Long> {
    boolean existsByChannel(Channel channel);
    // @Query("SELECT ct FROM CodingTest ct WHERE ct.channel.id = :channelId")
    // Optional<CodingTest> findByChannelId(@Param("channelId") Long channelId);
    List<CodingTest> findAll(Specification<CodingTest> spec);
}
