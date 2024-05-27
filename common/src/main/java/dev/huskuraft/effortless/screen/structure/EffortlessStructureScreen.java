package dev.huskuraft.effortless.screen.structure;

import java.util.ArrayList;
import java.util.Arrays;

import dev.huskuraft.effortless.EffortlessClient;
import dev.huskuraft.effortless.api.core.Player;
import dev.huskuraft.effortless.api.gui.AbstractWidget;
import dev.huskuraft.effortless.api.gui.text.TextWidget;
import dev.huskuraft.effortless.api.input.Key;
import dev.huskuraft.effortless.api.platform.Entrance;
import dev.huskuraft.effortless.api.text.ChatFormatting;
import dev.huskuraft.effortless.api.text.Text;
import dev.huskuraft.effortless.building.Feature;
import dev.huskuraft.effortless.building.Option;
import dev.huskuraft.effortless.building.config.PassiveMode;
import dev.huskuraft.effortless.building.history.UndoRedo;
import dev.huskuraft.effortless.building.pattern.Patterns;
import dev.huskuraft.effortless.building.replace.ReplaceMode;
import dev.huskuraft.effortless.building.settings.Settings;
import dev.huskuraft.effortless.building.structure.BuildFeature;
import dev.huskuraft.effortless.building.structure.builder.Structure;
import dev.huskuraft.effortless.screen.pattern.EffortlessPatternScreen;
import dev.huskuraft.effortless.screen.settings.EffortlessSettingsScreen;
import dev.huskuraft.effortless.screen.wheel.AbstractWheelScreen;

public class EffortlessStructureScreen extends AbstractWheelScreen<Structure, Option> {

    private static final Button<Option> UNDO_OPTION = button(UndoRedo.UNDO, false);
    private static final Button<Option> REDO_OPTION = button(UndoRedo.REDO);
    private static final Button<Option> SETTING_OPTION = button(Settings.SETTINGS);
    private static final Button<Option> PATTERN_DISABLED_OPTION = button(Patterns.DISABLED, false);
    private static final Button<Option> PATTERN_ENABLED_OPTION = button(Patterns.ENABLED, true);
    private static final Button<Option> PATTERN_OPTION = lazyButton(() -> {
        var entrance = EffortlessClient.getInstance();
        var context = entrance.getStructureBuilder().getContext(entrance.getClient().getPlayer());
        if (context.pattern().enabled()) {
            var name = Patterns.ENABLED.getNameText().append(" " + context.pattern().transformers().size() + " Transformers");
            var descriptions = new ArrayList<Text>();
            if (!context.pattern().transformers().isEmpty()) {
                descriptions.add(Text.empty());
            }
            for (var transformer : context.pattern().transformers()) {
                descriptions.add(Text.text("").append(transformer.getName().withStyle(ChatFormatting.GRAY)).append("").withStyle(ChatFormatting.GRAY));
                for (var description : transformer.getDescriptions()) {
                    descriptions.add(Text.text(" ").append(description.withStyle(ChatFormatting.DARK_GRAY)));
                }
            }
            return button(Patterns.ENABLED, name, descriptions, true);
        } else {
            return PATTERN_DISABLED_OPTION;
        }
    });

    private static final Button<Option> REPLACE_DISABLED_OPTION = button(ReplaceMode.DISABLED, false);
    private static final Button<Option> REPLACE_BLOCKS_AND_AIR_OPTION = button(ReplaceMode.BLOCKS_AND_AIR, true);
    private static final Button<Option> REPLACE_BLOCKS_ONLY_OPTION = button(ReplaceMode.BLOCKS_ONLY, true);
    private static final Button<Option> REPLACE_OFFHAND_ONLY_OPTION = button(ReplaceMode.OFFHAND_ONLY, true);
//    private static final Button<Option> REPLACE_CUSTOM_LIST_ONLY_OPTION = button(ReplaceMode.CUSTOM_LIST_ONLY, true);

    private static final Button<Option> REPLACE_OPTION = lazyButton(() -> {
        var entrance = EffortlessClient.getInstance();
        var context = entrance.getStructureBuilder().getContext(entrance.getClient().getPlayer());
        return switch (context.replaceMode()) {
            case DISABLED -> REPLACE_DISABLED_OPTION;
            case BLOCKS_AND_AIR -> REPLACE_BLOCKS_AND_AIR_OPTION;
            case BLOCKS_ONLY -> REPLACE_BLOCKS_ONLY_OPTION;
            case OFFHAND_ONLY -> REPLACE_OFFHAND_ONLY_OPTION;
//            case CUSTOM_LIST_ONLY -> REPLACE_CUSTOM_LIST_ONLY_OPTION;
        };
    });


    private static final Button<Option> PASSIVE_MODE_DISABLED_OPTION = button(PassiveMode.DISABLED, false);
    private static final Button<Option> PASSIVE_MODE_ENABLED_OPTION = button(PassiveMode.ENABLED, true);

    private static final Button<Option> PASSIVE_MODE_OPTION = lazyButton(() -> {
        var entrance = EffortlessClient.getInstance();
        if (entrance.getConfigStorage().get().builderConfig().passiveMode()) {
            return PASSIVE_MODE_ENABLED_OPTION;
        } else {
            return PASSIVE_MODE_DISABLED_OPTION;
        }
    });

    private final Key assignedKey;

    private AbstractWidget passiveMode;

    public EffortlessStructureScreen(Entrance entrance, Key assignedKey) {
        super(entrance, Text.translate("effortless.building.radial.title"));
        this.assignedKey = assignedKey;
    }

    public static Slot<Structure> slot(Structure structure) {
        return slot(
                structure.getMode(),
                structure.getMode().getDisplayName(),
                structure.getMode().getIcon(),
                structure.getMode().getTintColor(),
                structure);
    }

    @Override
    protected EffortlessClient getEntrance() {
        return super.getEntrance();
    }

    protected Player getPlayer() {
        return getEntrance().getClient().getPlayer();
    }

    @Override
    public Key getAssignedKey() {
        return assignedKey;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        setRadialSelectResponder((slot, click) -> {
            getEntrance().getStructureBuilder().setStructure(getPlayer(), slot.getContent());
        });
        setRadialOptionSelectResponder((entry, click) -> {
            if (entry.getContent() instanceof Settings settings) {
                switch (settings) {
                    case SETTINGS -> {
                        detach();
                        new EffortlessSettingsScreen(getEntrance()).attach();
                    }
                    case PATTERN_SETTINGS -> {
                        detach();
                        new EffortlessPatternScreen(getEntrance()).attach();
                    }
                }
                return;
            }
            if (entry.getContent() instanceof Patterns patterns) {
//                if (click) {
//                    var pattern = getEntrance().getStructureBuilder().getContext(getPlayer()).pattern();
//                    getEntrance().getStructureBuilder().setPattern(getPlayer(), pattern.withEnabled(!pattern.enabled()));
//                    return;
//                }
                if (getEntrance().getStructureBuilder().checkPermission(getPlayer())) {
                    detach();
                    new EffortlessPatternScreen(getEntrance()).attach();
                    return;
                }
            }
            if (entry.getContent() instanceof UndoRedo undoRedo) {
                switch (undoRedo) {
                    case UNDO -> {
                        getEntrance().getStructureBuilder().undo(getPlayer());
                    }
                    case REDO -> {
                        getEntrance().getStructureBuilder().redo(getPlayer());
                    }
                }
                return;
            }
            if (entry.getContent() instanceof ReplaceMode replaceMode) {
                getEntrance().getStructureBuilder().setReplace(getPlayer(), getEntrance().getStructureBuilder().getContext(getPlayer()).replace().withReplaceMode(replaceMode.next()));
                return;
            }
            if (entry.getContent() instanceof PassiveMode passiveMode) {
                getEntrance().getConfigStorage().update(config -> config.withPassiveMode(passiveMode != PassiveMode.ENABLED));
                return;
            }
            if (entry.getContent() instanceof BuildFeature buildFeature) {
                var structure = getEntrance().getStructureBuilder().getContext(getPlayer()).structure().withFeature(buildFeature);
                if (getEntrance().getStructureBuilder().setStructure(getPlayer(), structure)) {
                    getEntrance().getConfigStorage().setStructure(structure);
                }
            }
        });

        this.passiveMode = addWidget(new TextWidget(getEntrance(), getX() + getWidth() - 10, getY() + getHeight() - 18, Text.translate("effortless.option.passive_mode"), TextWidget.Gravity.END));

    }

    @Override
    public void onReload() {
        passiveMode.setVisible(getEntrance().getConfigStorage().get().builderConfig().passiveMode());

        setRadialSlots(getEntrance().getConfigStorage().get().structureMap().values().stream().map(EffortlessStructureScreen::slot).toList());

        var structure = getEntrance().getStructureBuilder().getContext(getPlayer()).structure();
//        var structure = getEntrance().getStructureBuilder().getContext(getPlayer()).structure();
        setSelectedSlots(slot(structure));
        setLeftButtons(
                buttonSet(REPLACE_OPTION, REDO_OPTION, UNDO_OPTION),
                buttonSet(PASSIVE_MODE_OPTION, PATTERN_OPTION, SETTING_OPTION)
        );
        setRightButtons(
                structure.getSupportedFeatures().stream().map(feature -> buttonSet(Arrays.stream(feature.getEntries()).map((Feature option) -> button((Option) option, structure.getFeatures().contains(option))).toList())).toList()
        );
    }

}

