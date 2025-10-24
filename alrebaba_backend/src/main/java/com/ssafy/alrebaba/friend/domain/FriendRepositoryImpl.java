package com.ssafy.alrebaba.friend.domain;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

public class FriendRepositoryImpl implements FriendRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Friend> findFriendListByMemberAndStatus(Long memberId, FriendStatus status, LocalDateTime lastCreatedAt, int pageSize) {
        StringBuilder jpql = new StringBuilder("SELECT f FROM Friend f WHERE (f.requestMember.memberId = :memberId OR f.acceptMember.memberId = :memberId) ");
        jpql.append("AND f.status = :status ");
        if (lastCreatedAt != null) {
            jpql.append("AND f.createdAt < :lastCreatedAt ");
        }
        jpql.append("ORDER BY f.createdAt DESC");
        TypedQuery<Friend> query = em.createQuery(jpql.toString(), Friend.class);
        query.setParameter("memberId", memberId);
        query.setParameter("status", status);
        if (lastCreatedAt != null) {
            query.setParameter("lastCreatedAt", lastCreatedAt);
        }
        query.setMaxResults(pageSize + 1);
        return query.getResultList();
    }
}
