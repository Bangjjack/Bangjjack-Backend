package com.project.bangjjack.domain.post.domain.entity;

import com.project.bangjjack.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "post_shared_lifestyles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PostSharedLifestyle extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false, unique = true)
    private RoommatePost post;

    @Column(nullable = false)
    private boolean roomTrashBinSharing;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Recycling recycling;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private PhoneCall phoneCall;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ItemSharing itemSharing;

    @Column(nullable = false)
    private boolean earphoneUsage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LightsOutTime lightsOutTime;

    public void update(
            boolean roomTrashBinSharing,
            Recycling recycling,
            PhoneCall phoneCall,
            ItemSharing itemSharing,
            boolean earphoneUsage,
            LightsOutTime lightsOutTime
    ) {
        this.roomTrashBinSharing = roomTrashBinSharing;
        this.recycling = recycling;
        this.phoneCall = phoneCall;
        this.itemSharing = itemSharing;
        this.earphoneUsage = earphoneUsage;
        this.lightsOutTime = lightsOutTime;
    }

    public static PostSharedLifestyle create(
            RoommatePost post,
            boolean roomTrashBinSharing,
            Recycling recycling,
            PhoneCall phoneCall,
            ItemSharing itemSharing,
            boolean earphoneUsage,
            LightsOutTime lightsOutTime
    ) {
        return new PostSharedLifestyle(
                post, roomTrashBinSharing, recycling, phoneCall, itemSharing, earphoneUsage, lightsOutTime
        );
    }
}
