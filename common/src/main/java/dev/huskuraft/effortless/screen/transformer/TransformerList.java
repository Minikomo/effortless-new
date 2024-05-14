package dev.huskuraft.effortless.screen.transformer;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

import dev.huskuraft.effortless.api.core.Axis;
import dev.huskuraft.effortless.api.gui.AbstractWidget;
import dev.huskuraft.effortless.api.gui.Dimens;
import dev.huskuraft.effortless.api.gui.EntryList;
import dev.huskuraft.effortless.api.gui.container.EditableEntryList;
import dev.huskuraft.effortless.api.gui.slot.SlotContainer;
import dev.huskuraft.effortless.api.gui.slot.SlotData;
import dev.huskuraft.effortless.api.gui.slot.TextSlot;
import dev.huskuraft.effortless.api.gui.text.TextWidget;
import dev.huskuraft.effortless.api.platform.Entrance;
import dev.huskuraft.effortless.api.text.Text;
import dev.huskuraft.effortless.building.pattern.Transformer;
import dev.huskuraft.effortless.building.pattern.array.ArrayTransformer;
import dev.huskuraft.effortless.building.pattern.mirror.MirrorTransformer;
import dev.huskuraft.effortless.building.pattern.raidal.RadialTransformer;
import dev.huskuraft.effortless.building.pattern.randomize.ItemRandomizer;

public final class TransformerList extends EditableEntryList<Transformer> {

    public TransformerList(Entrance entrance, int x, int y, int width, int height) {
        super(entrance, x, y, width, height);
    }

    @SuppressWarnings("unchecked")
    // FIXME: 12/5/24
    @Override
    protected EditableEntryList.Entry<Transformer> createHolder(Transformer transformer) {
        return (EditableEntryList.Entry) switch (transformer.getType()) {
            case ARRAY -> new ArrayEntry(getEntrance(), this, (ArrayTransformer) transformer);
            case MIRROR -> new MirrorEntry(getEntrance(), this, (MirrorTransformer) transformer);
            case RADIAL -> new RadialEntry(getEntrance(), this, (RadialTransformer) transformer);
            case ITEM_RAND -> new ItemRandomizerEntry(getEntrance(), this, (ItemRandomizer) transformer);
        };
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    public abstract static class Entry<T extends Transformer> extends EditableEntryList.Entry<T> {

        protected TextSlot textSlot;
        protected AbstractWidget titleTextWidget;
        protected SlotContainer slotContainer;

        public Entry(Entrance entrance, EntryList entryList, T transformer) {
            super(entrance, entryList, transformer);
        }

        protected static String formatDouble(double number) {
            var decimalFormat = new DecimalFormat("#.#");
            return decimalFormat.format(number);
        }

        public static Text getSymbol(Transformer transformer) {
            return switch (transformer.getType()) {
                case ARRAY -> Text.text("AT");
                case MIRROR -> Text.text("MT");
                case RADIAL -> Text.text("RT");
                case ITEM_RAND -> Text.text("IR");
            };
        }

        @Override
        public void onCreate() {
            this.textSlot = addWidget(new TextSlot(getEntrance(), getX(), getY(), Dimens.ICON_WIDTH, Dimens.ICON_HEIGHT, getSymbol()));
            this.titleTextWidget = addWidget(new TextWidget(getEntrance(), getX() + Dimens.ICON_WIDTH + 2, getY() + 2, getDisplayName()));
            this.slotContainer = addWidget(new SlotContainer(getEntrance(), getX() + Dimens.ICON_WIDTH + 2, getY() + 12, 0, 0));
        }

        @Override
        public void onReload() {
            slotContainer.setWrapLines(getEntryList().getSelected() == this);
            textSlot.setMessage(getSymbol());
            slotContainer.setEntries(getData());
        }

        @Override
        public Text getNarration() {
            return Text.translate("narrator.select", getDisplayName());
        }

        protected abstract List<SlotData> getData();

        protected Text getSymbol() {
            return getSymbol(getItem());
        }

        @Override
        public int getHeight() {
            return Dimens.ICON_HEIGHT + 4;
        }

        protected Text getDisplayName() {
            if (getItem().getName().getString().isEmpty()) {
                if (getItem() instanceof ItemRandomizer itemRandomizer) {
                    switch (itemRandomizer.getSource()) {
                        case INVENTORY -> {
                            switch (itemRandomizer.getOrder()) {
                                case SEQUENCE -> {
                                    return Text.translate("effortless.randomizer.item.sequence_inventory");
                                }
                                case RANDOM -> {
                                    return Text.translate("effortless.randomizer.item.random_inventory");
                                }
                            }
                        }
                        case HOTBAR -> {
                            switch (itemRandomizer.getOrder()) {
                                case SEQUENCE -> {
                                    return Text.translate("effortless.randomizer.item.sequence_hotbar");
                                }
                                case RANDOM -> {
                                    return Text.translate("effortless.randomizer.item.random_hotbar");
                                }
                            }
                        }
                        case HANDS -> {
                            switch (itemRandomizer.getOrder()) {
                                case SEQUENCE -> {
                                    return Text.translate("effortless.randomizer.item.sequence_hands");
                                }
                                case RANDOM -> {
                                    return Text.translate("effortless.randomizer.item.random_hands");
                                }
                            }
                        }
                        case CUSTOMIZE -> {
                            if (itemRandomizer.getChances().isEmpty()) {
                                return Text.translate("effortless.randomizer.item.empty");
                            }
                            switch (itemRandomizer.getOrder()) {
                                case SEQUENCE -> {
                                    return Text.translate("effortless.randomizer.item.sequence_customized");
                                }
                                case RANDOM -> {
                                    return Text.translate("effortless.randomizer.item.random_customized");
                                }
                            }
                        }
                    }
                }

                return switch (getItem().getType()) {
                    case ARRAY -> Text.translate("effortless.transformer.array.no_name");
                    case MIRROR -> Text.translate("effortless.transformer.mirror.no_name");
                    case RADIAL -> Text.translate("effortless.transformer.radial.no_name");
                    case ITEM_RAND -> Text.empty();
                };
            }
            return getItem().getName();
        }
    }


    public static class ArrayEntry extends Entry<ArrayTransformer> {

        public ArrayEntry(Entrance entrance, EntryList entryList, ArrayTransformer arrayTransformer) {
            super(entrance, entryList, arrayTransformer);
        }

        @Override
        protected List<SlotData> getData() {
            return List.of(
                    new SlotData.TextSymbol(Text.text(formatDouble(getItem().offset().x())), Text.translate("effortless.axis.x")),
                    new SlotData.TextSymbol(Text.text(formatDouble(getItem().offset().y())), Text.translate("effortless.axis.y")),
                    new SlotData.TextSymbol(Text.text(formatDouble(getItem().offset().z())), Text.translate("effortless.axis.z")),
                    new SlotData.TextSymbol(Text.text(formatDouble(getItem().count())), Text.translate("effortless.transformer.array.count"))
            );
        }

    }

    public static class ItemRandomizerEntry extends Entry<ItemRandomizer> {

        public ItemRandomizerEntry(Entrance entrance, EntryList entryList, ItemRandomizer randomizer) {
            super(entrance, entryList, randomizer);
        }

        @Override
        protected List<SlotData> getData() {
            if (getItem().getSource() == ItemRandomizer.Source.CUSTOMIZE) {
                return getItem().getChances().stream().map(chance -> new SlotData.ItemStackSymbol(chance.content().getDefaultStack(), Text.text(String.valueOf(chance.chance())))).collect(Collectors.toList());
            }
            return List.of();
        }

    }

    public static class MirrorEntry extends Entry<MirrorTransformer> {

        public MirrorEntry(Entrance entrance, EntryList entryList, MirrorTransformer mirrorTransformer) {
            super(entrance, entryList, mirrorTransformer);
        }

        @Override
        protected List<SlotData> getData() {
            return List.of(
                    new SlotData.TextSymbol(Text.text(formatDouble(getItem().getPosition(getItem().axis()))), Text.text("D")),
                    new SlotData.TextSymbol(getItem().axis().getDisplayName(), Text.translate("effortless.transformer.mirreor.axis"))
            );
        }


    }

    public static class RadialEntry extends Entry<RadialTransformer> {

        public RadialEntry(Entrance entrance, EntryList entryList, RadialTransformer radialTransformer) {
            super(entrance, entryList, radialTransformer);
        }

        @Override
        protected List<SlotData> getData() {
            return List.of(
                    new SlotData.TextSymbol(Text.text(formatDouble(getItem().position().x())), Axis.X.getDisplayName()),
                    new SlotData.TextSymbol(Text.text(formatDouble(getItem().position().y())), Axis.Y.getDisplayName()),
                    new SlotData.TextSymbol(Text.text(formatDouble(getItem().position().z())), Axis.Z.getDisplayName()),
                    new SlotData.TextSymbol(Text.text(formatDouble(getItem().slices())), Text.translate("effortless.transformer.radial.slices"))
            );
        }

    }
}
