package com.ssafy.alrebaba.notification.domain;

import com.ssafy.alrebaba.member.domain.Member;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query(value = "SELECT * FROM notifications WHERE receiver_id = :memberId ORDER BY created_at DESC", nativeQuery = true)
    List<Notification> findAllByReceiverId(@Param("memberId") Long memberId);

    @Query("SELECT n.notificationId FROM Notification n " +
            "WHERE n.type = :type " +
            "AND n.receiver.memberId = :receiverId " +
            "AND n.referenceId = :referenceId")
    Long findNotificationId(@Param("type") NotificationType type,
                            @Param("receiverId") Long receiverId,
                            @Param("referenceId") Long referenceId);

    boolean existsByReceiverAndTypeAndReferenceId(Member receiver, NotificationType type, Long referenceId);

}