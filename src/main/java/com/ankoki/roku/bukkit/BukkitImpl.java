package com.ankoki.roku.bukkit;

import com.ankoki.roku.bukkit.advancements.Advancement;
import com.ankoki.roku.bukkit.advancements.AdvancementTrigger;
import com.ankoki.roku.bukkit.advancements.Background;
import com.ankoki.roku.bukkit.advancements.Frame;
import com.ankoki.roku.bukkit.advancements.exceptions.InvalidAdvancementException;
import com.ankoki.roku.bukkit.guis.GUI;
import com.ankoki.roku.bukkit.guis.GUIHandler;
import com.ankoki.roku.bukkit.guis.PaginatedGUI;
import com.ankoki.roku.bukkit.misc.ItemUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.UnsafeValues;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class BukkitImpl extends JavaPlugin {

    private static BukkitImpl instance;
    private final boolean dev = this.getDescription().getVersion().endsWith("-dev");
    private final String version = this.getDescription().getVersion();
    private static final String COMMAND_PREFIX = "§7§oRoku; §f";

    // DEV
    private final NamespacedKey ADVANCEMENT_KEY = new NamespacedKey(this, "roku_test");
    private final GUI TEST_GUI = new GUI("§eVoltage §7~ §cITZY", 27)
            .setShape("xxxxxxxxx", "xxxxAxxxx", "xxxxxxxxx")
            .setShapeItem('x', ItemUtils.getBlank(Material.BLACK_STAINED_GLASS_PANE))
            .setShapeItem('A', ItemUtils.getSkull("3ec6c6e00a6ad055f250546a8c0da070df4613a5f65517a9933bd5de969d8406", "§f"))
            .addClickEvent(event -> {
                event.setCancelled(true);
                HumanEntity entity = event.getWhoClicked();
                entity.sendMessage(COMMAND_PREFIX + "§9Liquid Smooth §7~ §8Mitski");
            }).setDragEvent(event -> event.setCancelled(true));
    private final PaginatedGUI TEST_PAGINATED_GUI = new PaginatedGUI(TEST_GUI.setSlot(2, PaginatedGUI.makeButton("next", ItemUtils.getBlank(Material.ACACIA_SAPLING))))
            .registerPage("next", 1, new GUI("Second page!!!!!!", 9)
                    .setShape("----a----")
                    .setShapeItem('-', ItemUtils.getBlank(Material.BLACK_BED))
                    .addClickEvent(event -> event.setCancelled(true))
                    .setDragEvent(event -> event.setCancelled(true)));

    @Override
    public void onEnable() {
        instance = this;
        if (this.isDev()) {
            BukkitImpl.warning("Development build detected, if this is not intended, please report this on the github.");
            this.advancementTest();
            GUI.registerGUI(TEST_GUI);
            GUI.registerGUI(TEST_PAGINATED_GUI);
            BukkitImpl.info("Test GUI has been created and registered.");
        }
        this.getServer().getPluginManager().registerEvents(new GUIHandler(), this);
        this.getServer().getPluginCommand("roku").setExecutor(this);
        BukkitImpl.info("§8- §7ROKU §8- §aENABLED §7-");
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    public static void info(String log) {
        instance.getLogger().info(log);
    }

    public static void warning(String warning) {
        instance.getLogger().warning(warning);
    }

    public static void error(String error) {
        instance.getLogger().severe(error);
    }

    public static BukkitImpl getInstance() {
        return instance;
    }

    public static UnsafeValues getUnsafe() {
        return instance.getServer().getUnsafe();
    }

    public boolean isDev() {
        return dev;
    }

    private void advancementTest() {
        try {
            // Advancements are persistent once registered, so only
            // register a new one if it doesn't exist!
            if (!Advancement.advancementExists(ADVANCEMENT_KEY))
                new Advancement(ADVANCEMENT_KEY)
                        .setTitle("§7§oRoku Development Build")
                        .setDescription("§fYou used a development build of §7§oRoku§f!")
                        .setFrame(Frame.CHALLENGE)
                        .setAnnounced(true)
                        .setIcon(Material.DIAMOND)
                        .setBackground(Background.END)
                        .addCriteria("default", AdvancementTrigger.IMPOSSIBLE)
                        .load();
            BukkitImpl.info("Advancement loaded");
        } catch (InvalidAdvancementException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (this.isDev()) {
            if (sender instanceof Player player && args.length == 1) {
                if (args[0].equalsIgnoreCase("advgive"))
                    Advancement.awardAdvancement(player, Advancement.getAdvancement(ADVANCEMENT_KEY));
                else if (args[0].equalsIgnoreCase("advrevoke"))
                    Advancement.revokeAdvancement(player, Advancement.getAdvancement(ADVANCEMENT_KEY));
                else if (args[0].equalsIgnoreCase("gui")) TEST_GUI.openTo(player);
                else if (args[0].equalsIgnoreCase("paginatedgui")) TEST_PAGINATED_GUI.openTo(player);
                sender.sendMessage(COMMAND_PREFIX + "done something with the arg " + args[0]);
            }
        } else sender.sendMessage(COMMAND_PREFIX + "Thank you for using Roku v" + version);
        return true;
    }
}