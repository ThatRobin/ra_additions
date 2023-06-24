package io.github.thatrobin.ra_additions.util;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.github.apace100.apoli.command.PowerTypeArgumentType;
import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.power.InventoryPower;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import io.github.thatrobin.ra_additions.RA_Additions;
import io.github.thatrobin.ra_additions.powers.NbtPower;
import net.minecraft.command.CommandSource;
import net.minecraft.command.DataCommandObject;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.DataCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public class PowerDataObject implements DataCommandObject {

    @SuppressWarnings("unused")
    public static final SuggestionProvider<ServerCommandSource> SUGGESTION_PROVIDER = (context, builder) -> {
        List<Identifier> nbtPowers = new LinkedList<>();
        PowerTypeRegistry.entries().forEach((entry) -> {
            //if(entry.getValue().getFactory().getFactory().getSerializerId().equals(RA_Additions.identifier("nbt"))) {
                nbtPowers.add(entry.getKey());
            //}
        });
        return CommandSource.suggestIdentifiers(nbtPowers, builder);
    };

    private static final SimpleCommandExceptionType INVALID_ENTITY_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.data.entity.invalid"));

    record PowerObjectType(String argumentName) implements DataCommand.ObjectType {
        @Override
        public DataCommandObject getObject(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
            return new PowerDataObject(EntityArgumentType.getEntity(context, this.argumentName), PowerTypeArgumentType.getPower(context, "power"));
        }

        @Override
        public ArgumentBuilder<ServerCommandSource, ?> addArgumentsToBuilder(ArgumentBuilder<ServerCommandSource, ?> argument, Function<ArgumentBuilder<ServerCommandSource, ?>, ArgumentBuilder<ServerCommandSource, ?>> argumentAdder) {
            return argument.then(CommandManager.literal("power").then(argumentAdder.apply(CommandManager.argument(this.argumentName, EntityArgumentType.entity())).then(argumentAdder.apply(CommandManager.argument("power", PowerTypeArgumentType.power())))));
        }
    }

    public static final Function<String, DataCommand.ObjectType> TYPE_FACTORY = PowerObjectType::new;

    private final Entity entity;
    private final PowerType<?> power;

    public PowerDataObject(Entity entity, PowerType<?> power) {
        this.entity = entity;
        this.power = power;
    }

    @Override
    public void setNbt(NbtCompound nbt) throws CommandSyntaxException {
        if (!(this.entity instanceof LivingEntity)) {
            throw INVALID_ENTITY_EXCEPTION.create();
        }
        PowerHolderComponent component = PowerHolderComponent.KEY.get(entity);
        Power power2 = component.getPower(power);
        if(power2 instanceof NbtPower nbtPower) {
            nbtPower.setCompound(nbt);
        } else if(power2 instanceof InventoryPower inventoryPower) {
            inventoryPower.fromTag(nbt);
        }
        component.sync();
    }

    @Override
    public NbtCompound getNbt() throws CommandSyntaxException {
        if (!(this.entity instanceof LivingEntity)) {
            throw INVALID_ENTITY_EXCEPTION.create();
        }
        try {
            PowerHolderComponent component = PowerHolderComponent.KEY.get(entity);
            Power power2 = component.getPower(power);
            if(power2 instanceof NbtPower nbtPower) {
                return nbtPower.getCompound();
            } else if(power2 instanceof InventoryPower inventoryPower) {
                return inventoryPower.toTag();
            }
        } catch (Exception e) {
            RA_Additions.LOGGER.info("could not get the nbt");
        }
        return new NbtCompound();
    }

    @Override
    public Text feedbackModify() {
        return Text.translatable("commands.data.entity.modified", this.entity.getDisplayName());
    }

    @Override
    public Text feedbackQuery(NbtElement element) {
        return Text.translatable("commands.data.entity.query", this.entity.getDisplayName(), NbtHelper.toPrettyPrintedText(element));
    }

    @Override
    public Text feedbackGet(NbtPathArgumentType.NbtPath path, double scale, int result) {
        return Text.translatable("commands.data.entity.get", path, this.entity.getDisplayName(), String.format(Locale.ROOT, "%.2f", scale), result);
    }
}
