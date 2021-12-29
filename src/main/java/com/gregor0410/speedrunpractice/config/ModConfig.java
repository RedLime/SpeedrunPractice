package com.gregor0410.speedrunpractice.config;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gregor0410.speedrunpractice.SpeedrunPractice;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ScreenTexts;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ModConfig{
    public Boolean stables=true;
    public Boolean bridge=true;
    public Boolean treasure=true;
    public Boolean housing=true;
    public List<List<String>> endPracticeInventories =Lists.newArrayList(new ArrayList<>(),new ArrayList<>(),new ArrayList<>());
    public int endPracticeSlot=0;
    public float netherRegionSize=1;
    public int bastionRarity = 60;

    public static ModConfig load() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve("speedrun-practice.json");
        BufferedReader reader = null;
        try {
            reader = Files.newBufferedReader(path);
            Gson gson = new Gson();
            ModConfig config = gson.fromJson(reader,ModConfig.class);
            reader.close();
            return config;
        } catch (IOException e) {
            return new ModConfig();
        }
    }

    public Screen getScreen(Screen parent){
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setDoesConfirmSave(false)
                .setTransparentBackground(true)
                .setTitle(new TranslatableText("speedrun-practice.options"))
                .setSavingRunnable(()->{
                    try {
                        save();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        ConfigCategory general = builder.getOrCreateCategory(new TranslatableText("speedrun-practice.options.general"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        general.addEntry(entryBuilder.startIntSlider(new TranslatableText("speedrun-practice.options.nether_region_size"),(int)(netherRegionSize*100),7,200)
                .setDefaultValue(100)
                .setSaveConsumer(a->netherRegionSize=(float)a/100)
                .setTextGetter(a->new LiteralText(String.format("%d %%",a)))
                .build());
        general.addEntry(entryBuilder.startIntSlider(new TranslatableText("speedrun-practice.options.bastion_rarity"),bastionRarity,0,100)
                .setDefaultValue(60)
                .setTextGetter(a->new LiteralText(String.format("%d %%",a)))
                .setSaveConsumer(a->bastionRarity=a)
                .setTooltip(new TranslatableText("speedrun-practice.options.bastion_rarity_tooltip"))
                .build());
        general.addEntry(entryBuilder.startSubCategory(new TranslatableText("speedrun-practice.options.bastions"), Lists.newArrayList(
                entryBuilder.startBooleanToggle(new TranslatableText("speedrun-practice.options.bastions.housing"),housing)
                        .setDefaultValue(true)
                        .setYesNoTextSupplier(a->a ? ScreenTexts.ON : ScreenTexts.OFF)
                        .setSaveConsumer(a->housing=a)
                        .build(),
                entryBuilder.startBooleanToggle(new TranslatableText("speedrun-practice.options.bastions.stables"),stables)
                        .setDefaultValue(true)
                        .setYesNoTextSupplier(a->a ? ScreenTexts.ON : ScreenTexts.OFF)
                        .setSaveConsumer(a->stables=a)
                        .build(),
                entryBuilder.startBooleanToggle(new TranslatableText("speedrun-practice.options.bastions.treasure"),treasure)
                        .setDefaultValue(true)
                        .setYesNoTextSupplier(a->a ? ScreenTexts.ON : ScreenTexts.OFF)
                        .setSaveConsumer(a->treasure=a)
                        .build(),
                entryBuilder.startBooleanToggle(new TranslatableText("speedrun-practice.options.bastions.bridge"),bridge)
                        .setDefaultValue(true)
                        .setYesNoTextSupplier(a->a ? ScreenTexts.ON : ScreenTexts.OFF)
                        .setSaveConsumer(a->bridge=a)
                        .build())).build());
        return builder.build();
    }
    public void save() throws IOException {
        SpeedrunPractice.update();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Path path = FabricLoader.getInstance().getConfigDir().resolve("speedrun-practice.json");
        Files.createDirectories(path.getParent());
        BufferedWriter writer = Files.newBufferedWriter(path);
        gson.toJson(this,writer);
        writer.close();
    }
}
