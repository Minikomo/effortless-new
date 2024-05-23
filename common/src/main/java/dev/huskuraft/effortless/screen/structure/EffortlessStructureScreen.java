package dev.huskuraft.effortless.screen.structure;

import java.util.Arrays;

import dev.huskuraft.effortless.EffortlessClient;
import dev.huskuraft.effortless.api.core.Player;
import dev.huskuraft.effortless.api.input.Key;
import dev.huskuraft.effortless.api.platform.Entrance;
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
            return PATTERN_ENABLED_OPTION;
        } else {
            return PATTERN_DISABLED_OPTION;
        }
    });

    private static final Button<Option> REPLACE_DISABLED_OPTION = button(ReplaceMode.DISABLED, false);
    private static final Button<Option> REPLACE_NORMAL_OPTION = button(ReplaceMode.NORMAL, true);
    private static final Button<Option> REPLACE_QUICK_OPTION = button(ReplaceMode.QUICK, true);

    private static final Button<Option> REPLACE_OPTION = lazyButton(() -> {
        var entrance = EffortlessClient.getInstance();
        var context = entrance.getStructureBuilder().getContext(entrance.getClient().getPlayer());
        return switch (context.replaceMode()) {
            case DISABLED -> REPLACE_DISABLED_OPTION;
            case NORMAL -> REPLACE_NORMAL_OPTION;
            case QUICK -> REPLACE_QUICK_OPTION;
        };
    });


    private static final Button<Option> PASSIVE_MODE_DISABLED_OPTION = button(PassiveMode.DISABLED, false);
    private static final Button<Option> PASSIVE_MODE_ENABLED_OPTION = button(PassiveMode.ENABLED, true);

    private static final Button<Option> PASSIVE_MODE_OPTION = lazyButton(() -> {
        var entrance = EffortlessClient.getInstance();
        if (entrance.getConfigStorage().get().passiveMode()) {
            return PASSIVE_MODE_ENABLED_OPTION;
        } else {
            return PASSIVE_MODE_DISABLED_OPTION;
        }
    });

    private final Key assignedKey;

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

        setRadialSelectResponder(slot -> {
            getEntrance().getStructureBuilder().setStructure(getPlayer(), slot.getContent());
        });
        setRadialOptionSelectResponder(entry -> {
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
                if (getEntrance().getStructureBuilder().checkPermission(getPlayer())) {
                    detach();
                    new EffortlessPatternScreen(getEntrance()).attach();
                }
                return;
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
                getEntrance().getStructureBuilder().setReplaceMode(getPlayer(), replaceMode.next());
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
    }

    @Override
    public void onReload() {

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
