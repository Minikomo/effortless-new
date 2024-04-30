package dev.huskuraft.effortless.screen.structure;

import java.util.Arrays;

import dev.huskuraft.effortless.EffortlessClient;
import dev.huskuraft.effortless.api.input.Key;
import dev.huskuraft.effortless.api.platform.Entrance;
import dev.huskuraft.effortless.api.text.Text;
import dev.huskuraft.effortless.building.MultiSelectFeature;
import dev.huskuraft.effortless.building.Option;
import dev.huskuraft.effortless.building.SingleSelectFeature;
import dev.huskuraft.effortless.building.history.UndoRedo;
import dev.huskuraft.effortless.building.replace.ReplaceMode;
import dev.huskuraft.effortless.building.settings.Settings;
import dev.huskuraft.effortless.building.structure.BuildMode;
import dev.huskuraft.effortless.screen.radial.AbstractWheelScreen;
import dev.huskuraft.effortless.screen.settings.EffortlessSettingsScreen;

public class EffortlessBuildModeWheelScreen extends AbstractWheelScreen<BuildMode, Option> {

    private static final Button<Option> UNDO_OPTION = button(UndoRedo.UNDO);
    private static final Button<Option> REDO_OPTION = button(UndoRedo.REDO);
    private static final Button<Option> SETTING_OPTION = button(Settings.GENERAL);

    private final Key assignedKey;

    public EffortlessBuildModeWheelScreen(Entrance entrance, Key assignedKey) {
        super(entrance, Text.translate("effortless.building.radial.title"));
        this.assignedKey = assignedKey;
    }

    public static Slot<BuildMode> slot(BuildMode mode) {
        return slot(
                mode,
                mode.getDisplayName(),
                mode.getIcon(),
                mode.getTintColor(),
                mode);
    }

    @Override
    protected EffortlessClient getEntrance() {
        return (EffortlessClient) super.getEntrance();
    }

    public Key getAssignedKey() {
        return assignedKey;
    }

    @Override
    public void tick() {
        super.tick();
        Key key = getAssignedKey();
        if (!key.getBinding().isKeyDown()) {
            detach();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        setRadialSlots(
                Arrays.stream(BuildMode.values()).map(EffortlessBuildModeWheelScreen::slot).toList()
        );
        setRadialSelectResponder(slot -> {
            getEntrance().getStructureBuilder().setBuildMode(getEntrance().getClient().getPlayer(), slot.getContent());
        });
        setRadialOptionSelectResponder(entry -> {
            if (entry.getContent() instanceof Settings settings) {
                switch (settings) {
                    case GENERAL -> {
                        new EffortlessSettingsScreen(getEntrance()).attach();
                        detach();
                    }
                }
                return;
            }
            if (entry.getContent() instanceof UndoRedo undoRedo) {
                switch (undoRedo) {
                    case UNDO -> {
                        getEntrance().getStructureBuilder().undo(getEntrance().getClient().getPlayer());
                    }
                    case REDO -> {
                        getEntrance().getStructureBuilder().redo(getEntrance().getClient().getPlayer());
                    }
                }
                return;
            }
            if (entry.getContent() instanceof ReplaceMode replaceMode) {
                getEntrance().getStructureBuilder().setBuildFeature(getEntrance().getClient().getPlayer(), replaceMode.next());
                return;
            }
            if (entry.getContent() instanceof SingleSelectFeature singleSelectFeature) {
                getEntrance().getStructureBuilder().setBuildFeature(getEntrance().getClient().getPlayer(), singleSelectFeature);
                return;
            }
            if (entry.getContent() instanceof MultiSelectFeature multiSelectFeature) {
                getEntrance().getStructureBuilder().setBuildFeature(getEntrance().getClient().getPlayer(), multiSelectFeature);
                return;
            }
        });
    }

    @Override
    public void onReload() {
        var context = getEntrance().getStructureBuilder().getContext(getEntrance().getClient().getPlayer());

        setSelectedSlots(slot(context.buildMode()));
        setLeftButtons(
                buttonSet(REDO_OPTION, UNDO_OPTION),
                buttonSet(SETTING_OPTION, button(context.replaceMode()))
        );
        setRightButtons(
                Arrays.stream(context.buildMode().getSupportedFeatures()).map(feature -> buttonSet(Arrays.stream(feature.getEntries()).map(EffortlessBuildModeWheelScreen::<Option>button).toList())).toList()
        );
        setSelectedButtons(
                context.buildFeatures().stream().map(EffortlessBuildModeWheelScreen::<Option>button).toList()
        );
    }

}

