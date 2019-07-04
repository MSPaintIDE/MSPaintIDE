package com.uddernetworks.mspaint.discord;

import com.uddernetworks.mspaint.code.languages.Language;
import com.uddernetworks.mspaint.project.ProjectManager;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class DiscordRPCManager implements RPCManager {

    private static Logger LOGGER = LoggerFactory.getLogger(DiscordRPCManager.class);

    private static final Map<String, String> LANG_ICONS = Map.of(
            "Go", "icon-go",
            "Java", "icon-java",
            "JavaScript", "icon-js",
            "Python", "icon-python");

    private AtomicBoolean initted = new AtomicBoolean(false);
    private AtomicBoolean ready = new AtomicBoolean(false);
    private ScheduledExecutorService service = Executors.newScheduledThreadPool(2);
    private DiscordRichPresence.Builder currentPresenceBuilder;
    private long startTime = System.currentTimeMillis();
    private Queue<Consumer<DiscordRichPresence.Builder>> beforeReadyEvents = new ConcurrentLinkedQueue<>();
    private static final String IDLE_EDITING = "Painting 🎨"; // Shows in place of the "Editing foo.x.png" message, if nothing is being edited
    private static final String IDLE_PROJECT = "Just chillin"; // Shows if no project has ben opened (Or if RPC doesn't know about one being opened)

    @Override
    public void init() {
        try {
            if (initted.getAndSet(true)) return;
            LOGGER.info("Initializing Discord RPC");

            var handlers = new DiscordEventHandlers.Builder().setReadyEventHandler(user -> {
                this.ready.set(true);
                LOGGER.info("Setting RPC for Discord user " + user.username + "#" + user.discriminator + "!");

                updateCurrentPresence(IDLE_EDITING, presence -> {
                    presence.setDetails(IDLE_PROJECT);
                    presence.setBigImage("big-logo", "Just Chilling");
                    presence.setStartTimestamps(this.startTime);
                });

                for (Consumer<DiscordRichPresence.Builder> task; (task = this.beforeReadyEvents.poll()) != null;) {
                    task.accept(this.currentPresenceBuilder);
                }
            }).build();

            DiscordRPC.discordInitialize("578312704163446784", handlers, true);
            service.scheduleAtFixedRate(DiscordRPC::discordRunCallbacks, 0, 2000, TimeUnit.MILLISECONDS);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                LOGGER.info("Shutting down Discord RPC");
                DiscordRPC.discordShutdown();
            }));
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("Error during initialization of Discord RPC", e);
        }
    }

    @Override
    public void updateProject() {
        updateCurrentPresence(presence -> {
            var currProject = ProjectManager.getPPFProject();
            presence.setDetails(currProject == null ? IDLE_PROJECT : "Working on " + currProject.getName());
        });
    }

    @Override
    public void setFileEditing(String fileName) {
        updateCurrentPresence(presence -> presence.build().state = fileName == null ? IDLE_EDITING : "Editing " + fileName);
    }

    @Override
    public void setLanguage(Language language) {
        updateCurrentPresence(presence -> {
            presence.setBigImage(LANG_ICONS.getOrDefault(language.getName(), "icon-unknown"), "Programming with " + language.getName());
            presence.setSmallImage("small-logo", "MS Paint IDE");
        });
    }

    private void updateCurrentPresence(Consumer<DiscordRichPresence.Builder> presenceConsumer) {
        if (!this.ready.get()) {
            beforeReadyEvents.add(presenceConsumer);
            return;
        }

        if (this.currentPresenceBuilder == null)
            this.currentPresenceBuilder = new DiscordRichPresence.Builder("").setStartTimestamps(startTime);
        presenceConsumer.accept(this.currentPresenceBuilder);
        DiscordRPC.discordUpdatePresence(this.currentPresenceBuilder.build());
    }

    private void updateCurrentPresence(String state, Consumer<DiscordRichPresence.Builder> presenceConsumer) {
        this.currentPresenceBuilder = new DiscordRichPresence.Builder(state).setStartTimestamps(startTime);
        presenceConsumer.accept(this.currentPresenceBuilder);
        DiscordRPC.discordUpdatePresence(this.currentPresenceBuilder.build());
    }

}
