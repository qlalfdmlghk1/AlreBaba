package com.ssafy.alrebaba.member.domain;



import com.ssafy.alrebaba.interest.domain.Interest;
import com.ssafy.alrebaba.language.domain.Language;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table(name = "members")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
@EntityListeners(AuditingEntityListener.class) // 필
public class Member implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false, unique = true)
    private String username;

    @Column
    private String password;

    @Column(nullable = false, length = 32)
    private String nickname;

    @Column(unique = true)
    private String uniqueId;

    @Enumerated(EnumType.STRING)
    @Column(name="ROLE", nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(name = "profile_image", columnDefinition = "TEXT")
    private String profileImage; // 기존의 Image 엔터티 대신 프로필 이미지 URL 저장

    @Builder.Default
    @Column(nullable = false)
    private Boolean isAlarmOn = true; // 기본값 설정

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Interest> interestList = new ArrayList<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Language> languageList = new ArrayList<>();


    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.getKey()));
    }

    @Override
    public String getUsername() {
        return username;
    }
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return memberId.equals(member.memberId) && username.equals(member.username) && nickname.equals(member.nickname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId, username, nickname);
    }

    @PostPersist
    private void setUniqueIdAfterPersist() {
        this.uniqueId = this.nickname + "@" + this.memberId;
    }


}