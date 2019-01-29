package com.uddernetworks.mspaint.painthook;

import com.uddernetworks.mspaint.gui.window.UserInputWindow;
import com.uddernetworks.mspaint.main.Main;
import com.uddernetworks.mspaint.main.MainGUI;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import static com.uddernetworks.mspaint.painthook.InjectionManager.ClickType.*;

public class InjectionManager {

    private static Logger LOGGER = Logger.getLogger(InjectionManager.class);
    private MainGUI mainGUI;
    private Main main;
    private PaintInjector pInject;

    private Executor executor = Executors.newCachedThreadPool();

    // Yes, I know this is horrible OOP, but JNI is _super_ finicky about this crap and doesn't want non-static objects.
    private static Queue<ClickType> queue = new ConcurrentLinkedQueue<>();

    enum ClickType {
        BUILD(InjectionManager::clickBuild),
        RUN(InjectionManager::clickRun),
        STOP(InjectionManager::clickStop),
        COMMIT(InjectionManager::clickCommit),
        PUSH(InjectionManager::clickPush),
        PULL(InjectionManager::clickPull);

        private Consumer<InjectionManager> callback;

        ClickType(Consumer<InjectionManager> callback) {
            this.callback = callback;
        }

        public void run(InjectionManager injectionManager) {
            callback.accept(injectionManager);
        }
    }

    public InjectionManager(MainGUI mainGUI, Main main) {
        this.mainGUI = mainGUI;
        this.main = main;
        this.pInject = PaintInjector.INSTANCE;
    }

    public void createHooks() {
        pInject.clickBuild(() -> queue.add(BUILD));
        pInject.clickRun(() -> queue.add(RUN));
        pInject.clickStop(() -> queue.add(STOP));
        pInject.clickCommit(() -> queue.add(COMMIT));
        pInject.clickPush(() -> queue.add(PUSH));
        pInject.clickPull(() -> queue.add(PULL));

        executor.execute(() -> {
            while (true) {
                var type = queue.poll();
                if (type == null) continue;
                type.run(this);
            }
        });
    }

    private void clickBuild() {
        try {
            LOGGER.info("Building...");
            if (this.mainGUI.getCurrentLanguage().isInterpreted()) {
                this.mainGUI.setHaveError();
                LOGGER.error("The selected language does not support building.");
                return;
            }
            this.mainGUI.fullCompile(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clickRun() {
        System.out.println("Run");
    }

    private void clickStop() {
        System.out.println("Stop");
    }

    private void clickCommit() {
        try {
            new UserInputWindow(this.mainGUI, "Your commit message", "Commit message", true, commitMessage -> {
                try {
                    this.mainGUI.getGitController().commit(commitMessage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clickPush() {
        this.mainGUI.getGitController().push();
    }

    private void clickPull() {

    }
}
