package dev.huskuraft.effortless.api.core;

import java.util.List;

import dev.huskuraft.effortless.api.platform.ContentFactory;
import dev.huskuraft.effortless.api.platform.PlatformReference;
import dev.huskuraft.effortless.api.tag.TagRecord;
import dev.huskuraft.effortless.api.text.Text;

public interface ItemStack extends PlatformReference {

    String DAMAGE_TAG = "Damage";
    String UNBREAKABLE_TAG = "Unbreakable";

    static ItemStack empty() {
        return ContentFactory.getInstance().newItemStack();
    }

    static ItemStack of(Item item, int count) {
        return ContentFactory.getInstance().newItemStack(item, count);
    }

    List<Text> getTooltips(Player player, TooltipType flag);

    Item getItem();

    int getCount();

    void setCount(int count);

    int getMaxStackSize();

    Text getHoverName();

    ItemStack copy();

    TagRecord getTag();

    default TagRecord getOrCreateTag() {
        if (getTag() == null) {
            setTag(TagRecord.newRecord());
        }
        return getTag();
    };

    void setTag(TagRecord tagRecord);

    default void increase(int count) {
        setCount(getCount() + count);
    }

    default void decrease(int count) {
        setCount(getCount() - count);
    }

    default boolean isEmpty() {
        return this == empty() || getItem().equals(Items.AIR.item()) || getCount() <= 0;
    }

    default boolean isAir() {
        return getItem().equals(Items.AIR.item());
    }

    default boolean isBlock() {
        return getItem() instanceof BlockItem;
    }

    default boolean isDamaged() {
        return isDamageableItem() && this.getDamageValue() > 0;
    }

    default void setDamageValue(int damage) {
        this.getOrCreateTag().putInt(DAMAGE_TAG, Math.max(0, damage));
    }

    default int getDamageValue() {
        return getOrCreateTag().getIntOrElse(DAMAGE_TAG, 0);
    }

    default int getMaxDamage() {
        return getItem().getMaxDamage();
    }

    default int getRemainingDamage() {
        return getMaxDamage() - getDamageValue();
    }

    default boolean isDamageableItem() {
        if (!isEmpty() && getItem().getMaxDamage() > 0) {
            return !getOrCreateTag().getBooleanOrElse(UNBREAKABLE_TAG, false);
        } else {
            return false;
        }
    }

    boolean damageBy(Player player, int damage);

    enum TooltipType {
        NORMAL,
        NORMAL_CREATIVE,
        ADVANCED,
        ADVANCED_CREATIVE
    }

}
