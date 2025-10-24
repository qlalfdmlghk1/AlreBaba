package com.ssafy.alrebaba.friend.domain;

import java.io.Serializable;
import java.util.Objects;

public class FriendId implements Serializable {

    private Long acceptMember;
    private Long requestMember;

    public FriendId() {}

    public FriendId(Long acceptMember, Long requestMember) {
        this.acceptMember = acceptMember;
        this.requestMember = requestMember;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FriendId)) return false;
        FriendId friendId = (FriendId) o;
        return Objects.equals(acceptMember, friendId.acceptMember) &&
                Objects.equals(requestMember, friendId.requestMember);
    }

    @Override
    public int hashCode() {
        return Objects.hash(acceptMember, requestMember);
    }
}
