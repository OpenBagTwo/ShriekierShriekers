package io.github.openbagtwo.shkrieker.config;

import static io.github.openbagtwo.shkrieker.ShriekerMod.LOGGER;
import static io.github.openbagtwo.shkrieker.ShriekerMod.MOD_ID;
import static io.github.openbagtwo.shkrieker.ShriekerMod.MOD_NAME;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import net.fabricmc.loader.api.FabricLoader;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

/**
 * Shriekier Shriekers' configuration reader / writer
 */
public class Config {

  /**
   * Path to the config file
   */
  private static final Path config_path = FabricLoader.getInstance().getConfigDir()
      .resolve(MOD_ID + ".yaml").toAbsolutePath();

  /**
   * Whether shriekers should cause darkness
   */
  private boolean causeDarkness;

  /**
   * Whether naturally-generated shriekers should be triggered by non-player sources
   * (will not increase warning level)
   */
  private boolean applyToNatural;


  /**
   * Determine whether shriekers should cause darkness
   */
  public boolean getCauseDarknessSetting() {
    return this.causeDarkness;
  }

  /**
   * Determine whether to apply this mod to natually-generated shriekers
   */
  public boolean getApplyToNaturalSetting() {
    return this.applyToNatural;
  }

  /**
   * Default values
   */
  private static final boolean DEFAULT_CAUSE_DARKNESS = false;
  private static final boolean DEFAULT_APPLY_TO_NATURAL = false;

  /**
   * Load the mod configuration, however you have to
   */
  public static Config loadConfiguration() {
    Config config;
    try {
      config = fromConfigFile();
      LOGGER.debug("Loaded " + MOD_NAME + " configuration.");
    } catch (FileNotFoundException e) {
      LOGGER.warn("No " + MOD_NAME + " configuration file found.");
      try {
        writeDefaultConfigFile();
      } catch (ConfigException writee) {
        LOGGER.error("Could not write " + MOD_NAME + " configuration:\n" + writee);
      }
      config = getDefaultConfiguration();
    } catch (ConfigException e) {
      LOGGER.error(MOD_NAME + " configuration is invalid:\n" + e);
      config = getDefaultConfiguration();
    }
    return config;
  }

  private static final DumperOptions configFormat = new DumperOptions() {{
    this.setIndent(2);
    this.setPrettyFlow(true);
    this.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
  }};

  /**
   * If the configuration cannot be read in from file for whatever reason, generate and return the
   * default configuration
   */
  private static Config getDefaultConfiguration() {
    LOGGER.info("Loading default " + MOD_NAME + " configuration");
    Config config = new Config();
    config.causeDarkness = DEFAULT_CAUSE_DARKNESS;
    config.applyToNatural = DEFAULT_APPLY_TO_NATURAL;
    return config;
  }


  /**
   * Write a new configuration file with default options
   *
   * @throws ConfigException If the writer encounters any sort of IO error (permissions?)
   */
  private static void writeDefaultConfigFile() throws ConfigException {
    FileWriter configWriter;
    try {
      configWriter = new FileWriter(config_path.toFile());
    } catch (IOException e) {
      throw new ConfigException(
          "Could not open " + config_path + " for writing.", e
      );
    }
    Map<String, Object> writeme = new LinkedHashMap<>();
    writeme.put("cause_darkness", DEFAULT_CAUSE_DARKNESS);
    writeme.put("apply_to_natural", DEFAULT_APPLY_TO_NATURAL);

    (new Yaml(configFormat)).dump(writeme, configWriter);
    LOGGER.info("Wrote " + MOD_NAME + " configuration file to " + config_path);
  }

  /**
   * Load the settings for the mod, either from file or from defaults
   *
   * @return map of str key to the configuration value
   * @throws ConfigException if the configuration cannot be parsed
   */
  private static Config fromConfigFile() throws FileNotFoundException, ConfigException {

    LOGGER.debug("Reading " + MOD_NAME + " configuration from " + config_path);
    FileInputStream configReader = new FileInputStream(config_path.toFile());
    HashMap<String, Object> settings = new HashMap<>((new Yaml()).load(configReader));

    try {
      // Now we actually construct the thing
      boolean causeDarkness = Boolean.parseBoolean(
          settings.getOrDefault("cause_darkness", DEFAULT_CAUSE_DARKNESS).toString()
      );
      boolean applyToNatural = Boolean.parseBoolean(
          settings.getOrDefault("apply_to_natural", DEFAULT_CAUSE_DARKNESS).toString()
      );

      Config config = new Config();
      config.causeDarkness = causeDarkness;
      config.applyToNatural = applyToNatural;
      return config;

    } catch (Exception e) {
      throw new ConfigException(config_path + " is not a valid " + MOD_NAME + " configuration.", e);
    }
  }

  private static class ConfigException extends Exception {

    ConfigException(String message, Exception e) {
      super(message, e);
    }

    ConfigException(String message) {
      super(message);
    }
  }

}

