package com.trainitek.backtothefuture.domain;


import com.trainitek.backtothefuture.domain.base.UuidAggregateRoot;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "`user`")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends UuidAggregateRoot {
    @Getter
    private String firstName;

    @Getter
    private String lastName;

    @Builder
    private User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
