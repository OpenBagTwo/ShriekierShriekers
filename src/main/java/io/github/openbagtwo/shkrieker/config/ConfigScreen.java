package io.github.openbagtwo.shkrieker.config;

import io.github.openbagtwo.shkrieker.ShriekerMod;
import io.github.openbagtwo.shkrieker.config.Config.ConfigException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.OptionListWidget;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

public class ConfigScreen extends GameOptionsScreen {

  private OptionListWidget widgets;
  private Config config;

  public ConfigScreen(Screen previous) {
    super(previous, MinecraftClient.getInstance().options, Text.of(ShriekerMod.MOD_NAME));
    this.config = Config.loadConfiguration();
  }


  @Override
  protected void init() {
    this.widgets = this.addDrawableChild(new OptionListWidget(this.client, this.width, this.height, this));
    this.widgets.addSingleOptionEntry(SimpleOption.ofBoolean("Cause Darkness", this.config.causeDarkness, (value) -> {this.config.causeDarkness = value; }));
    this.widgets.addSingleOptionEntry(SimpleOption.ofBoolean("Apply to Natural Shriekers", this.config.applyToNatural, (value) -> {this.config.applyToNatural = value;}));
    super.init();
  }

  @Override
  protected void initTabNavigation() {
    super.initTabNavigation();
    this.widgets.position(this.width, this.layout);
  }

  @Override
  public void render(DrawContext DrawContext, int mouseX, int mouseY, float delta) {
    super.render(DrawContext, mouseX, mouseY, delta);
    this.widgets.render(DrawContext, mouseX, mouseY, delta);
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
