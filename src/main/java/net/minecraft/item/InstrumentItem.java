package net.minecraft.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.warden.event.GameEvent;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;

public class InstrumentItem
extends Item {
    private static final String TAG_INSTRUMENT = "instrument";

    public InstrumentItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable World level, List<ITextComponent> list, ITooltipFlag tooltipFlag) {
        super.appendHoverText(itemStack, level, list, tooltipFlag);
        Optional<Holder<Instrument>> optional = this.getInstrument(itemStack);
        if (optional.isPresent()) {
            IFormattableTextComponent mutableComponent = new TranslationTextComponent(Util.makeDescriptionId(TAG_INSTRUMENT, Registry.INSTRUMENT.getKey(optional.get().value())));
            list.add(mutableComponent.withStyle(TextFormatting.GRAY));
        }
    }

    public static ItemStack create(Item item, Holder<Instrument> holder) {
        ItemStack itemStack = new ItemStack(item);
        Instruments.saveInstrumentToTag(itemStack, holder.value());
        return itemStack;
    }

    public static ItemStack setRandom(ItemStack itemStack, List<Instrument> collection, Random randomSource) {
        Instrument chosen = collection.get(randomSource.nextInt(collection.size()));
        Instruments.saveInstrumentToTag(itemStack, chosen);
        return itemStack;
    }

    private static void setSoundVariantId(ItemStack itemStack, Holder<Instrument> holder) {
        Instruments.saveInstrumentToTag(itemStack, holder.value());
    }

    @Override
    public ActionResult<ItemStack> use(World level, PlayerEntity player, Hand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        Optional<? extends Holder<Instrument>> optional = this.getInstrument(itemStack);
        if (optional.isPresent()) {
            Instrument instrument = optional.get().value();
            player.startUsingItem(interactionHand);
            InstrumentItem.play(level, player, instrument);
            player.getCooldowns().addCooldown(this, instrument.useDuration);
            player.awardStat(Stats.ITEM_USED.get(this));
            return ActionResult.consume(itemStack);
        }
        return ActionResult.fail(itemStack);
    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        Optional<? extends Holder<Instrument>> optional = this.getInstrument(itemStack);
        return optional.map(holder -> ((Instrument)holder.value()).useDuration).orElse(0);
    }

    private Optional<Holder<Instrument>> getInstrument(ItemStack itemStack) {
        CompoundNBT nbt = itemStack.getOrCreateTag();

        if (nbt.contains("Instrument", 8)) {
            String string = nbt.getString("Instrument");
            Instrument instrument = Instruments.getFromName(string);
            return Optional.of(Holder.of(instrument));
        }
        return Optional.empty();
    }

    @Override
    public UseAction getUseAnimation(ItemStack itemStack) {
        return UseAction.TOOT_HORN;
    }

    public static void play(World level, PlayerEntity player, Instrument instrument) {
        SoundEvent soundEvent = instrument.soundEvent.value();
        float f = instrument.range / 16.0f;
        level.playSound(player, player, soundEvent, SoundCategory.RECORDS, f, 1.0f);
        level.gameEvent(GameEvent.INSTRUMENT_PLAY, player.position(), GameEvent.Context.of(player));
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> list) {
       if (this.allowdedIn(ItemGroup.TAB_MISC) && group == ItemGroup.TAB_MISC || this.allowdedIn(ItemGroup.TAB_SEARCH) && group == ItemGroup.TAB_SEARCH) {
           for (Instrument instrument : Registry.INSTRUMENT) {
               ItemStack horn = new ItemStack(Items.GOAT_HORN);
               Instruments.saveInstrumentToTag(horn, instrument);
               list.add(horn);
           }
       }
    }
}

