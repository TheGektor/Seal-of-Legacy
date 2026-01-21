package com.antagonis1.sealoflegacy;

import com.antagonis1.sealoflegacy.database.DatabaseManager;
import com.antagonis1.sealoflegacy.features.sealing.SealedItemManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SealOfLegacy extends JavaPlugin {

    private static SealOfLegacy instance;
    private DatabaseManager databaseManager;
    private SealedItemManager sealedItemManager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        // Initialize Database
        databaseManager = new DatabaseManager(this);
        databaseManager.initialize();

        // Initialize Managers
        sealedItemManager = new SealedItemManager(this);

        // Register Commands
        getCommand("lockitem").setExecutor(new com.antagonis1.sealoflegacy.features.commands.LockCommand(this));
        getCommand("unlockitem").setExecutor(new com.antagonis1.sealoflegacy.features.commands.UnlockCommand(this));
        getCommand("lockcheck").setExecutor(new com.antagonis1.sealoflegacy.features.commands.LockCheckCommand(this));
        getCommand("lockinfo").setExecutor(new com.antagonis1.sealoflegacy.features.commands.LockInfoCommand(this));
        getCommand("lockreload").setExecutor(new com.antagonis1.sealoflegacy.features.commands.LockReloadCommand(this)); 

        // Register Listeners
        getServer().getPluginManager().registerEvents(new com.antagonis1.sealoflegacy.features.protection.AnvilListener(this), this);
        getServer().getPluginManager().registerEvents(new com.antagonis1.sealoflegacy.features.protection.CraftingListener(this), this);
        getServer().getPluginManager().registerEvents(new com.antagonis1.sealoflegacy.features.protection.GrindstoneListener(this), this);
        getServer().getPluginManager().registerEvents(new com.antagonis1.sealoflegacy.features.protection.CartographyListener(this), this);

        getLogger().info("Seal-of-Legacy enabled successfully! Version: " + getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.close();
        }
        getLogger().info("Seal-of-Legacy disabled.");
    }

    public static SealOfLegacy getInstance() {
        return instance;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public SealedItemManager getSealedItemManager() {
        return sealedItemManager;
    }
}
