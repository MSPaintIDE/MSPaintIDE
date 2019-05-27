package com.uddernetworks.mspaint.ocr;

import com.uddernetworks.mspaint.settings.Setting;
import com.uddernetworks.mspaint.settings.SettingsManager;
import com.uddernetworks.newocr.configuration.FontConfiguration;
import com.uddernetworks.newocr.configuration.HOCONFontConfiguration;
import com.uddernetworks.newocr.database.DatabaseManager;
import com.uddernetworks.newocr.database.OCRDatabaseManager;
import com.uddernetworks.newocr.recognition.*;
import com.uddernetworks.newocr.recognition.mergence.DefaultMergenceManager;
import com.uddernetworks.newocr.recognition.mergence.MergenceManager;
import com.uddernetworks.newocr.recognition.similarity.DefaultSimilarityManager;
import com.uddernetworks.newocr.recognition.similarity.SimilarityManager;
import com.uddernetworks.newocr.train.ComputerTrainGenerator;
import com.uddernetworks.newocr.train.ImageReadMethod;
import com.uddernetworks.newocr.train.TrainGenerator;
import com.uddernetworks.newocr.train.TrainGeneratorOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class FontData {

    private static Logger LOGGER = LoggerFactory.getLogger(FontData.class);

    private OCRManager ocrManager;
    private String fontName;
    private String configPath;
    private FontConfiguration configuration;

    private Scan scan;
    private Train train;
    private Actions actions;

    private TrainGenerator trainGenerator;
    private DatabaseManager databaseManager;
    private SimilarityManager similarityManager;
    private MergenceManager mergenceManager;

    private boolean usingInternal;

    public FontData(OCRManager ocrManager, String fontName, String configPath) {
        this.ocrManager = ocrManager;
        this.fontName = fontName;
        this.configPath = configPath;
    }

    public void initialize() {
        var settingsManager = SettingsManager.getInstance();
        settingsManager.<Boolean>onChangeSetting(Setting.DATABASE_USE_INTERNAL, useInternal -> {
            if (!this.equals(this.ocrManager.getActiveFont())) return;
            try {
                if (useInternal == this.usingInternal) return;
                this.usingInternal = useInternal;

                if (this.databaseManager != null) this.databaseManager.shutdown(TimeUnit.SECONDS, 1);

                if (useInternal) {
                    String location = settingsManager.getSetting(Setting.DATABASE_INTERNAL_LOCATION);
                    File file = location != null && !location.trim().equals("") ? new File(location) : null;

                    if (file == null || (!file.isDirectory() && !file.mkdirs())) {
                        LOGGER.error("Invalid/unset internal database location");
                        return;
                    }

                    this.databaseManager = new OCRDatabaseManager(new File(file, "ocr_db_" + this.fontName.replaceAll("[^a-zA-Z\\d\\s:]", "_")));
                } else {
                    String url = settingsManager.getSetting(Setting.DATABASE_URL);
                    String user = settingsManager.getSetting(Setting.DATABASE_USER);
                    String pass = settingsManager.getSetting(Setting.DATABASE_PASS);

                    if (url == null || user == null || pass == null || url.isEmpty() || user.isEmpty() || pass.isEmpty()) {
                        LOGGER.error("Couldn't set up database manager, partial/missing credentials in settings.");
                        return;
                    }

                    this.databaseManager = new OCRDatabaseManager(url, user, pass);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, true);

        this.similarityManager = new DefaultSimilarityManager();
        this.mergenceManager = new DefaultMergenceManager(this.databaseManager, similarityManager);

        this.configuration = new HOCONFontConfiguration(this.configPath, this.ocrManager.getReflectionCacher(), this.similarityManager);
        this.configuration.fetchAndApplySimilarities();

        updateMergeRules();

        var options = this.configuration.fetchOptions();
        options.setImageReadMethod(ImageReadMethod.IMAGEIO_STREAM);

        var generatorOptions = new TrainGeneratorOptions()
                .setFontFamily(this.fontName)
                .setMinFontSize(settingsManager.getSetting(Setting.TRAIN_LOWER_BOUND))
                .setMaxFontSize(settingsManager.getSetting(Setting.TRAIN_UPPER_BOUND));

        this.actions = new OCRActions(this.databaseManager, options);
        this.scan = new OCRScan(databaseManager, similarityManager, mergenceManager, this.actions);
        this.train = new OCRTrain(this.databaseManager, options, this.actions, generatorOptions);

        this.trainGenerator = new ComputerTrainGenerator(generatorOptions);
    }

    public void updateMergeRules() {
        if (this.databaseManager.isTrainedSync()) {
            this.configuration.fetchAndApplyMergeRules(this.mergenceManager);
        }
    }

    public String getFontName() {
        return fontName;
    }

    public String getConfigPath() {
        return configPath;
    }

    public FontConfiguration getConfiguration() {
        return configuration;
    }

    public Scan getScan() {
        return scan;
    }

    public Train getTrain() {
        return train;
    }

    public Actions getActions() {
        return actions;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public SimilarityManager getSimilarityManager() {
        return similarityManager;
    }

    public MergenceManager getMergenceManager() {
        return mergenceManager;
    }

    public TrainGenerator getTrainGenerator() {
        return trainGenerator;
    }
}
