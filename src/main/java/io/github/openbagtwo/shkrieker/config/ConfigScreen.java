package io.github.openbagtwo.shkrieker.config;

import io.github.openbagtwo.shkrieker.ShriekerMod;
import io.github.openbagtwo.shkrieker.config.Config.ConfigException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

public class ConfigScreen extends OptionsSubScreen {

  private OptionsList widgets;
  private Config config;

  public ConfigScreen(Screen previous) {
    super(previous, Minecraft.getInstance().options, Component.nullToEmpty(ShriekerMod.MOD_NAME));
    this.config = Config.loadConfiguration();
  }

  @Override
  protected void addOptions() {
    if (this.list != null) {
      this.list.addBig(OptionInstance.createBoolean("Cause Darkness", this.config.causeDarkness, (value) -> {this.config.causeDarkness = value; }));
      this.list.addBig(OptionInstance.createBoolean("Apply to Natural Shriekers", this.config.applyToNatural, (value) -> {this.config.applyToNatural = value;}));
    }
  }

  @Override
  public void removed() {
    try {
      this.config.writeConfigToFile();
    } catch (ConfigException e) {
      ShriekerMod.LOGGER.error(String.valueOf(e));
    }
  }
}
