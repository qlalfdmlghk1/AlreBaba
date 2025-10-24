package com.ssafy.alrebaba.coding_test.domain;

import com.ssafy.alrebaba.channel.domain.Channel;
import com.ssafy.alrebaba.problem.domain.Problem;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "coding_tests")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodingTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coding_test_id")
    private Long codingTestId;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "channel_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Channel channel;

    @OneToMany(mappedBy = "codingTest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Problem> problemList;

    @Column(name = "start_time", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime endTime;
}
