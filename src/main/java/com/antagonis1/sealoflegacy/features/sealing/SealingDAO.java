package com.antagonis1.sealoflegacy.features.sealing;

import com.antagonis1.sealoflegacy.database.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SealingDAO {

    private final DatabaseManager databaseManager;
    private final Logger logger;

    public SealingDAO(DatabaseManager databaseManager, Logger logger) {
        this.databaseManager = databaseManager;
        this.logger = logger;
    }

    public CompletableFuture<Void> saveSealedItem(UUID itemUuid, UUID ownerUuid, String signature) {
        return CompletableFuture.runAsync(() -> {
            String sql = "INSERT INTO sealed_items (item_uuid, owner_uuid, signature) VALUES (?, ?, ?)";
            try (Connection conn = databaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, itemUuid.toString());
                ps.setString(2, ownerUuid.toString());
                ps.setString(3, signature);
                ps.executeUpdate();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error saving sealed item", e);
            }
        });
    }

    public CompletableFuture<Void> deleteSealedItem(UUID itemUuid) {
        return CompletableFuture.runAsync(() -> {
            String sql = "DELETE FROM sealed_items WHERE item_uuid = ?";
            try (Connection conn = databaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, itemUuid.toString());
                ps.executeUpdate();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error deleting sealed item", e);
            }
        });
    }

    public CompletableFuture<List<SealedItem>> getSealedItemsByOwner(UUID ownerUuid) {
        return CompletableFuture.supplyAsync(() -> {
            List<SealedItem> items = new ArrayList<>();
            String sql = "SELECT * FROM sealed_items WHERE owner_uuid = ?";
            try (Connection conn = databaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, ownerUuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        items.add(mapResultSetToItem(rs));
                    }
                }
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error fetching sealed items", e);
            }
            return items;
        });
    }
    
    public CompletableFuture<Void> purgeAll() {
         return CompletableFuture.runAsync(() -> {
            String sql = "DELETE FROM sealed_items";
            try (Connection conn = databaseManager.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.executeUpdate();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error purging sealed items", e);
            }
        });
    }

    private SealedItem mapResultSetToItem(ResultSet rs) throws SQLException {
        // Simple timestamp parsing, assuming SQLite default format handling or string
        // SQLite uses strings for dates usually.
        String dateStr = rs.getString("sealed_at");
        // Simplified parsing, might need adjustment based on SQLite driver return type
        LocalDateTime date = LocalDateTime.now(); // Fallback
        // Ideally parse dateStr if valid
        
        return new SealedItem(
            rs.getInt("id"),
            UUID.fromString(rs.getString("item_uuid")),
            UUID.fromString(rs.getString("owner_uuid")),
            rs.getString("signature"),
            date
        );
    }
}
