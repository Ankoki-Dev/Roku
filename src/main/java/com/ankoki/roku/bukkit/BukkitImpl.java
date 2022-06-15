package com.ankoki.roku.bukkit;

import com.ankoki.roku.bukkit.advancements.Advancement;
import com.ankoki.roku.bukkit.advancements.AdvancementTrigger;
import com.ankoki.roku.bukkit.advancements.Background;
import com.ankoki.roku.bukkit.advancements.Frame;
import com.ankoki.roku.bukkit.advancements.exceptions.InvalidAdvancementException;
import com.ankoki.roku.bukkit.boards.Board;
import com.ankoki.roku.bukkit.guis.GUI;
import com.ankoki.roku.bukkit.guis.GUIHandler;
import com.ankoki.roku.bukkit.guis.PaginatedGUI;
import com.ankoki.roku.bukkit.misc.BukkitMisc;
import com.ankoki.roku.bukkit.misc.ItemUtils;
import com.ankoki.roku.misc.ReflectionUtils;
import com.ankoki.roku.misc.Version;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.UnsafeValues;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

/**
 * JavaPlugin class to deal with Bukkit implementation.
 */
public class BukkitImpl extends JavaPlugin implements Listener {

    private static BukkitImpl instance;
    private static Logger logger = Logger.getLogger("Roku");
    private PluginDescriptionFile description;
    private String rokuVersion;
    private boolean dev;
    private static final String COMMAND_PREFIX = "§7§oRoku; §f";
    private final Version serverVersion = Version.of(Bukkit.getServer().getBukkitVersion().split("-")[0]);

    // DEV
    private final NamespacedKey ADVANCEMENT_KEY = new NamespacedKey(this, "roku_test");
    private final GUI TEST_GUI = new GUI("§fROKU§8. .§1그에", 27)
            .setShape("xxxxxxxxx", "xxxxAxxxx", "xxxxxxxxx")
            .setShapeItem('x', ItemUtils.getBlank(Material.BLACK_STAINED_GLASS_PANE))
            .setShapeItem('A', ItemUtils.getSkull("3ec6c6e00a6ad055f250546a8c0da070df4613a5f65517a9933bd5de969d8406", "§f"))
            .addClickEvent(event -> {
                event.setCancelled(true);
                HumanEntity entity = event.getWhoClicked();
                entity.sendMessage(COMMAND_PREFIX + "§cYou have clicked the 로쿠 test 그에");
                entity.sendMessage(COMMAND_PREFIX + BukkitMisc.colourHex("&cTest hex colouring, <#95B759>this should be a nice green."));
            }).setDragEvent(event -> event.setCancelled(true));
    private PaginatedGUI TEST_PAGINATED_GUI = null;

    /**
     * This needs to be called if Roku is being shaded, and not put on the bukkit server.
     * This ensures our listeners and all needed fields are being setup correctly.
     * @param owning the plugin that will control roku.
     */
    public static void setupRoku(JavaPlugin owning) {
        instance = new BukkitImpl();
        try {
            instance.description = new PluginDescriptionFile(BukkitImpl.class.getResourceAsStream("plugin.yml"));
        } catch (InvalidDescriptionException ex) {
            BukkitImpl.error("There was an error with the Roku plugin.yml. Please report this.");
            ex.printStackTrace();
            return;
        }
        instance.rokuVersion = instance.description.getVersion();
        instance.dev = instance.rokuVersion.endsWith("-dev");
        ReflectionUtils.bukkitSetup(Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3], instance.serverVersion.isNewerThan(1, 16));
        if (instance.isDev()) {
            BukkitImpl.warning("Development build detected, if this is not intended, please report this on the github.");
            instance.advancementTest();
            GUI.registerGUI(instance.TEST_GUI);
            instance.TEST_PAGINATED_GUI = new PaginatedGUI(new GUI("First page :)", 18)
                    .setShape("---------", "aaaaaaaaa")
                    .setShapeItem('-', ItemUtils.getBlank(Material.CHAIN))
                    .setShapeItem('a', ItemUtils.getBlank(Material.COOKIE))
                    .setSlot(2, PaginatedGUI.makeButton("next", ItemUtils.getBlank(Material.BONE))))
                    .registerPage("next", 1, new GUI("Second page!!!!!!", 9)
                            .setShape("----a----")
                            .setShapeItem('-', ItemUtils.getBlank(Material.BLACK_BED))
                            .addClickEvent(event -> event.setCancelled(true))
                            .setDragEvent(event -> event.setCancelled(true)));
            GUI.registerGUI(instance.TEST_PAGINATED_GUI);
            BukkitImpl.info("Test GUI has been created and registered.");
        }
        Bukkit.getServer().getPluginManager().registerEvents(new GUIHandler(), owning);
        Bukkit.getServer().getPluginCommand("roku").setExecutor(instance);
        BukkitImpl.info("§8- §7ROKU §8- §aENABLED §7-");
    }

    @Override
    public void onEnable() {
        instance = this;
        ReflectionUtils.bukkitSetup(this.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3], serverVersion.isNewerThan(1, 16));
        this.rokuVersion = this.description.getVersion();
        this.dev = rokuVersion.endsWith("-dev");
        if (this.isDev()) {
            BukkitImpl.warning("Development build detected, if this is not intended, please report this on the github.");
            this.advancementTest();
            GUI.registerGUI(TEST_GUI);
            TEST_PAGINATED_GUI = new PaginatedGUI(new GUI("First page :)", 18)
                    .setShape("---------", "aaaaaaaaa")
                    .setShapeItem('-', ItemUtils.getBlank(Material.CHAIN))
                    .setShapeItem('a', ItemUtils.getBlank(Material.COOKIE))
                    .setSlot(2, PaginatedGUI.makeButton("next", ItemUtils.getBlank(Material.BONE))))
                    .registerPage("next", 1, new GUI("Second page!!!!!!", 9)
                            .setShape("----a----")
                            .setShapeItem('-', ItemUtils.getBlank(Material.BLACK_BED))
                            .addClickEvent(event -> event.setCancelled(true))
                            .setDragEvent(event -> event.setCancelled(true)));
            GUI.registerGUI(TEST_PAGINATED_GUI);
            BukkitImpl.info("Test GUI has been created and registered.");
        }
        this.getServer().getPluginManager().registerEvents(new GUIHandler(), this);
        this.getServer().getPluginCommand("roku").setExecutor(this);
        BukkitImpl.info("§8- §7ROKU §8- §aENABLED §7-");
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll((Plugin) this);
        instance = null;
    }

    /**
     * Sends an info level message to the console.
     *
     * @param log the text.
     */
    public static void info(String log) {
        logger.info(log);
    }

    /**
     * Sends a warning level message to the console.
     *
     * @param warning the text.
     */
    public static void warning(String warning) {
        logger.warning(warning);
    }

    /**
     * Sends an error level message to the console.
     *
     * @param error the text.
     */
    public static void error(String error) {
        logger.severe(error);
    }

    /**
     * Gets the BukkitImpl instance.
     *
     * @return the instance.
     */
    public static BukkitImpl getInstance() {
        return instance;
    }

    /**
     * Gets the unsafe values of the bukkit server.
     *
     * @return the unsafe.
     */
    public static UnsafeValues getUnsafe() {
        return instance.getServer().getUnsafe();
    }

    /**
     * Whether Roku is on a development version.
     *
     * @return true if Roku version ends with -dev.
     */
    public boolean isDev() {
        return dev;
    }

    /**
     * Gets the current server version.
     *
     * @return the current server version.
     */
    public Version getServerVersion() {
        return serverVersion;
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

    private void enableTestBoard(Player player) {
        Board board = Board.of(player);
        board.setTitle("§7§lROKU §f• §a§oDEVELOPMENT");
        board.setLine(1, "§c • Bottom •");
        board.setLine(2, "§f");
        board.setLine(3, "§e • Top •");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (this.isDev()) {
            if (sender instanceof Player player && args.length == 1) {
                switch (args[0].toUpperCase()) {
                    case "ADVGIVE" -> Advancement.awardAdvancement(player, Advancement.getAdvancement(ADVANCEMENT_KEY));
                    case "ADVREVOKE" -> Advancement.revokeAdvancement(player, Advancement.getAdvancement(ADVANCEMENT_KEY));
                    case "GUI" -> TEST_GUI.openTo(player);
                    case "PAGINATEDGUI" -> TEST_PAGINATED_GUI.openTo(player);
                    case "SCOREBOARD" -> this.enableTestBoard(player);
                }
                sender.sendMessage(COMMAND_PREFIX + "§7executed the argument §e" + args[0] + "§7 if applicable.");
            }
        } else sender.sendMessage(COMMAND_PREFIX + "Thank you for using Roku v" + rokuVersion);
        return true;
    }
}