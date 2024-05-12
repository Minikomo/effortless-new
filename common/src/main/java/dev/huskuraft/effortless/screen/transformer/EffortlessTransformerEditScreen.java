package dev.huskuraft.effortless.screen.transformer;

import java.util.Arrays;
import java.util.function.Consumer;

import dev.huskuraft.effortless.api.core.Axis;
import dev.huskuraft.effortless.api.core.Tuple2;
import dev.huskuraft.effortless.api.gui.AbstractContainerScreen;
import dev.huskuraft.effortless.api.gui.AbstractWidget;
import dev.huskuraft.effortless.api.gui.button.Button;
import dev.huskuraft.effortless.api.gui.slot.TextSlot;
import dev.huskuraft.effortless.api.gui.text.TextWidget;
import dev.huskuraft.effortless.api.math.Vector3d;
import dev.huskuraft.effortless.api.platform.Entrance;
import dev.huskuraft.effortless.api.text.ChatFormatting;
import dev.huskuraft.effortless.api.text.Text;
import dev.huskuraft.effortless.building.pattern.Transformer;
import dev.huskuraft.effortless.building.pattern.array.ArrayTransformer;
import dev.huskuraft.effortless.building.pattern.mirror.MirrorTransformer;
import dev.huskuraft.effortless.building.pattern.raidal.RadialTransformer;
import dev.huskuraft.effortless.screen.settings.SettingOptionsList;

public class EffortlessTransformerEditScreen extends AbstractContainerScreen {

    private final Consumer<Transformer> applySettings;
    private Transformer lastSettings;
    private AbstractWidget titleTextWidget;
    private SettingOptionsList entries;
    private Button saveButton;
    private Button cancelButton;
    private TextSlot textSlot;
//    private EditBox nameEditBox;

    public EffortlessTransformerEditScreen(Entrance entrance, Consumer<Transformer> consumer, Transformer transformer) {
        super(entrance, Text.translate("effortless.gui.transformer_presets.title"), CONTAINER_WIDTH_EXPANDED, CONTAINER_HEIGHT_180);
        this.applySettings = consumer;
        this.lastSettings = transformer;
    }

    @Override
    public void onCreate() {
        this.titleTextWidget = addWidget(new TextWidget(getEntrance(), getLeft() + getWidth() / 2, getTop() + TITLE_CONTAINER - 10, getScreenTitle().withStyle(ChatFormatting.DARK_GRAY), TextWidget.Gravity.CENTER));

//        this.textSlot = addWidget(new TextSlot(getEntrance(), getWidth() / 2 - (Dimens.Entry.ROW_WIDTH) / 2, 16 + 2, Dimens.ICON_WIDTH, Dimens.ICON_HEIGHT, Text.empty()));

//        this.nameEditBox = addWidget(
//                new EditBox(getEntrance(), getWidth() / 2 - (Dimens.Entry.ROW_WIDTH) / 2 + 40, Dimens.Screen.TITLE_24, Dimens.Entry.ROW_WIDTH - 40, 20, null)
//        );
//        this.nameEditBox.setMaxLength(ItemRandomizer.MAX_NAME_LENGTH);
//        this.nameEditBox.setHint(Text.translate("effortless.randomizer.edit.name_hint"));

        this.entries = addWidget(new SettingOptionsList(getEntrance(), getLeft() + PADDINGS, getTop() + TITLE_CONTAINER, getWidth() - PADDINGS * 2 - 8, getHeight() - TITLE_CONTAINER - BUTTON_CONTAINER_ROW_1, true, false));
        this.entries.setAlwaysShowScrollbar(true);

        this.cancelButton = addWidget(Button.builder(getEntrance(), Text.translate("effortless.transformer.edit.cancel"), button -> {
            detach();
        }).setBoundsGrid(getLeft(), getTop(), getWidth(), getHeight(), 0f, 0f, 0.5f).build());
        this.saveButton = addWidget(Button.builder(getEntrance(), Text.translate("effortless.transformer.edit.save"), button -> {
            applySettings.accept(lastSettings);
            detach();
        }).setBoundsGrid(getLeft(), getTop(), getWidth(), getHeight(), 0f, 0.5f, 0.5f).build());

        switch (lastSettings.getType()) {
            case ARRAY -> {
                this.entries.addNumberEntry(Text.translate("effortless.transformer.array.offset.x"), Text.text("X"), ((ArrayTransformer) lastSettings).offset().x(), ArrayTransformer.OFFSET_BOUND.getMinX(), ArrayTransformer.OFFSET_BOUND.getMaxX(), value -> this.lastSettings = ((ArrayTransformer) lastSettings).withOffsetX(value));
                this.entries.addNumberEntry(Text.translate("effortless.transformer.array.offset.y"), Text.text("Y"), ((ArrayTransformer) lastSettings).offset().y(), ArrayTransformer.OFFSET_BOUND.getMinY(), ArrayTransformer.OFFSET_BOUND.getMaxY(), value -> this.lastSettings = ((ArrayTransformer) lastSettings).withOffsetY(value));
                this.entries.addNumberEntry(Text.translate("effortless.transformer.array.offset.z"), Text.text("Z"), ((ArrayTransformer) lastSettings).offset().z(), ArrayTransformer.OFFSET_BOUND.getMinZ(), ArrayTransformer.OFFSET_BOUND.getMaxZ(), value -> this.lastSettings = ((ArrayTransformer) lastSettings).withOffsetZ(value));
                this.entries.addIntegerEntry(Text.translate("effortless.transformer.array.count"), Text.text("C"), ((ArrayTransformer) lastSettings).count(), ArrayTransformer.MIN_COUNT, ArrayTransformer.MAX_COUNT, value -> this.lastSettings = ((ArrayTransformer) lastSettings).withCount(value));
            }
            case MIRROR -> {
                var mirrorTransformer = (MirrorTransformer) lastSettings;
                this.entries.addPositionNumberEntry(mirrorTransformer.axis(), new Tuple2<>(mirrorTransformer.getPositionType(mirrorTransformer.axis()), mirrorTransformer.getPosition(mirrorTransformer.axis())), value -> {
                    this.lastSettings = mirrorTransformer.withPositionType(value.value1()).withPosition(new Vector3d(value.value2(), value.value2(), value.value2()));
                });
                this.entries.addSelectorEntry(Text.translate("effortless.transformer.mirror.axis"), Text.text("A"), Arrays.stream(Axis.values()).map(Axis::getDisplayName).toList(), Arrays.stream(Axis.values()).toList(), mirrorTransformer.axis(), value -> {
                    (((SettingOptionsList.PositionNumberEntry) this.entries.getWidget(0))).setAxis(value);
                    this.lastSettings = mirrorTransformer.withAxis(value);
                });
            }
            case RADIAL -> {
                this.entries.addPositionNumberEntry(Axis.X, new Tuple2<>(((RadialTransformer) lastSettings).getPositionTypeX(), ((RadialTransformer) lastSettings).position().x()), value -> this.lastSettings = ((RadialTransformer) lastSettings).withPositionTypeX(value.value1()).withPositionX(value.value2()));
                this.entries.addPositionNumberEntry(Axis.Y, new Tuple2<>(((RadialTransformer) lastSettings).getPositionTypeY(), ((RadialTransformer) lastSettings).position().y()), value -> this.lastSettings = ((RadialTransformer) lastSettings).withPositionTypeY(value.value1()).withPositionY(value.value2()));
                this.entries.addPositionNumberEntry(Axis.Z, new Tuple2<>(((RadialTransformer) lastSettings).getPositionTypeZ(), ((RadialTransformer) lastSettings).position().z()), value -> this.lastSettings = ((RadialTransformer) lastSettings).withPositionTypeZ(value.value1()).withPositionZ(value.value2()));
                this.entries.addIntegerEntry(Text.translate("effortless.transformer.radial.slices"), Text.text("S"), ((RadialTransformer) lastSettings).slices(), RadialTransformer.SLICE_RANGE.min(), RadialTransformer.SLICE_RANGE.max(), value -> this.lastSettings = ((RadialTransformer) lastSettings).withSlice(value));
            }
            case ITEM_RAND -> {
            }
        }

    }

    @Override
    public void onReload() {
//        textSlot.setMessage(TransformerList.Entry.getSymbol(lastSettings));
    }

}
