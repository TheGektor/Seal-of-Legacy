package com.antagonis1.sealoflegacy.features.sealing;

import java.time.LocalDateTime;
import java.util.UUID;

public class SealedItem {
    private final int id;
    private final UUID itemUuid;
    private final UUID ownerUuid;
    private final String signature;
    private final LocalDateTime sealedAt;

    public SealedItem(int id, UUID itemUuid, UUID ownerUuid, String signature, LocalDateTime sealedAt) {
        this.id = id;
        this.itemUuid = itemUuid;
        this.ownerUuid = ownerUuid;
        this.signature = signature;
        this.sealedAt = sealedAt;
    }

    public int getId() {
        return id;
    }

    public UUID getItemUuid() {
        return itemUuid;
    }

    public UUID getOwnerUuid() {
        return ownerUuid;
    }

    public String getSignature() {
        return signature;
    }

    public LocalDateTime getSealedAt() {
        return sealedAt;
    }
}
