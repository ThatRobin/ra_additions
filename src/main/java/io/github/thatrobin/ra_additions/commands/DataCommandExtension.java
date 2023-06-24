package io.github.thatrobin.ra_additions.commands;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import io.github.thatrobin.ra_additions.util.PowerDataObject;
import net.minecraft.command.BlockDataObject;
import net.minecraft.command.EntityDataObject;
import net.minecraft.command.StorageDataObject;
import net.minecraft.command.argument.NbtCompoundArgumentType;
import net.minecraft.command.argument.NbtElementArgumentType;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.DataCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class DataCommandExtension {
    private static final DynamicCommandExceptionType MODIFY_EXPECTED_OBJECT_EXCEPTION = new DynamicCommandExceptionType(nbt -> Text.translatable("commands.data.modify.expected_object", nbt));

    public static final List<Function<String, DataCommand.ObjectType>> OBJECT_TYPE_FACTORIES = ImmutableList.of(EntityDataObject.TYPE_FACTORY, BlockDataObject.TYPE_FACTORY, StorageDataObject.TYPE_FACTORY, PowerDataObject.TYPE_FACTORY);
    public static final List<DataCommand.ObjectType> TARGET_OBJECT_TYPES = OBJECT_TYPE_FACTORIES.stream().map(factory -> factory.apply("target")).collect(ImmutableList.toImmutableList());
    public static final List<DataCommand.ObjectType> SOURCE_OBJECT_TYPES = OBJECT_TYPE_FACTORIES.stream().map(factory -> factory.apply("source")).collect(ImmutableList.toImmutableList());

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = CommandManager.literal("data").requires(source -> source.hasPermissionLevel(2));
        for (DataCommand.ObjectType objectType : TARGET_OBJECT_TYPES) {
            literalArgumentBuilder.then(objectType.addArgumentsToBuilder(CommandManager.literal("merge"), builder -> builder.then(CommandManager.argument("nbt", NbtCompoundArgumentType.nbtCompound()).executes(context -> DataCommand.executeMerge(context.getSource(), objectType.getObject(context), NbtCompoundArgumentType.getNbtCompound(context, "nbt")))))).then(objectType.addArgumentsToBuilder(CommandManager.literal("get"), builder -> (builder.executes(context -> DataCommand.executeGet(context.getSource(), objectType.getObject(context)))).then((CommandManager.argument("path", NbtPathArgumentType.nbtPath()).executes(context -> DataCommand.executeGet(context.getSource(), objectType.getObject(context), NbtPathArgumentType.getNbtPath(context, "path")))).then(CommandManager.argument("scale", DoubleArgumentType.doubleArg()).executes(context -> DataCommand.executeGet(context.getSource(), objectType.getObject(context), NbtPathArgumentType.getNbtPath(context, "path"), DoubleArgumentType.getDouble(context, "scale"))))))).then(objectType.addArgumentsToBuilder(CommandManager.literal("remove"), builder -> builder.then(CommandManager.argument("path", NbtPathArgumentType.nbtPath()).executes(context -> DataCommand.executeRemove(context.getSource(), objectType.getObject(context), NbtPathArgumentType.getNbtPath(context, "path")))))).then(DataCommandExtension.addModifyArgument((builder, modifier) -> ((((builder.then(CommandManager.literal("insert").then(CommandManager.argument("index", IntegerArgumentType.integer()).then(modifier.create((context, sourceNbt, path, elements) -> path.insert(IntegerArgumentType.getInteger(context, "index"), sourceNbt, elements)))))).then(CommandManager.literal("prepend").then(modifier.create((context, nbtCompound, path, elements) -> path.insert(0, nbtCompound, elements))))).then(CommandManager.literal("append").then(modifier.create((context, nbtCompound, path, elements) -> path.insert(-1, nbtCompound, elements))))).then(CommandManager.literal("set").then(modifier.create((context, sourceNbt, path, elements) -> path.put(sourceNbt, Iterables.getLast(elements)))))).then(CommandManager.literal("merge").then(modifier.create((context, element, path, elements) -> {
                NbtCompound nbtCompound = new NbtCompound();
                for (NbtElement nbtElement : elements) {
                    if (NbtPathArgumentType.NbtPath.isTooDeep(nbtElement, 0)) {
                        throw NbtPathArgumentType.TOO_DEEP_EXCEPTION.create();
                    }
                    if (nbtElement instanceof NbtCompound nbtCompound2) {
                        nbtCompound.copyFrom(nbtCompound2);
                        continue;
                    }
                    throw MODIFY_EXPECTED_OBJECT_EXCEPTION.create(nbtElement);
                }
                List<NbtElement> collection = path.getOrInit(element, NbtCompound::new);
                int i = 0;
                for (NbtElement nbtElement2 : collection) {
                    if (nbtElement2 instanceof NbtCompound nbtCompound3) {
                        NbtCompound nbtCompound4 = nbtCompound3.copy();
                        nbtCompound3.copyFrom(nbtCompound);
                        i += nbtCompound4.equals(nbtCompound3) ? 0 : 1;
                    } else {
                        throw MODIFY_EXPECTED_OBJECT_EXCEPTION.create(nbtElement2);
                    }
                }
                return i;
            })))));
        }
        dispatcher.register(literalArgumentBuilder);
    }

    public static ArgumentBuilder<ServerCommandSource, ?> addModifyArgument(BiConsumer<ArgumentBuilder<ServerCommandSource, ?>, DataCommand.ModifyArgumentCreator> subArgumentAdder) {
        LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = CommandManager.literal("modify");
        for (DataCommand.ObjectType objectType : TARGET_OBJECT_TYPES) {
            objectType.addArgumentsToBuilder(literalArgumentBuilder, builder -> {
                RequiredArgumentBuilder<ServerCommandSource, NbtPathArgumentType.NbtPath> argumentBuilder = CommandManager.argument("targetPath", NbtPathArgumentType.nbtPath());
                for (DataCommand.ObjectType objectType2 : SOURCE_OBJECT_TYPES) {
                    subArgumentAdder.accept(argumentBuilder, operation -> objectType2.addArgumentsToBuilder(CommandManager.literal("from"), builder2 -> ((ArgumentBuilder<ServerCommandSource, ?>)builder2.executes(context -> DataCommand.executeModify(context, objectType, operation, DataCommand.getValues(context, objectType2)))).then(CommandManager.argument("sourcePath", NbtPathArgumentType.nbtPath()).executes(context -> DataCommand.executeModify(context, objectType, operation, DataCommand.getValuesByPath(context, objectType2))))));
                    subArgumentAdder.accept(argumentBuilder, operation -> objectType2.addArgumentsToBuilder(CommandManager.literal("string"), builder2 -> (builder2.executes(context -> DataCommand.executeModify(context, objectType, operation, DataCommand.mapValues(DataCommand.getValues(context, objectType2), value -> value)))).then((CommandManager.argument("sourcePath", NbtPathArgumentType.nbtPath()).executes(context -> DataCommand.executeModify(context, objectType, operation, DataCommand.mapValues(DataCommand.getValuesByPath(context, objectType2), value -> value)))).then((CommandManager.argument("start", IntegerArgumentType.integer(0)).executes(context -> DataCommand.executeModify(context, objectType, operation, DataCommand.mapValues(DataCommand.getValuesByPath(context, objectType2), value -> value.substring(IntegerArgumentType.getInteger(context, "start")))))).then(CommandManager.argument("end", IntegerArgumentType.integer(0)).executes(context -> DataCommand.executeModify(context, objectType, operation, DataCommand.mapValues(DataCommand.getValuesByPath(context, objectType2), value -> value.substring(IntegerArgumentType.getInteger(context, "start"), IntegerArgumentType.getInteger(context, "end"))))))))));
                }
                subArgumentAdder.accept(argumentBuilder, modifier -> CommandManager.literal("value").then(CommandManager.argument("value", NbtElementArgumentType.nbtElement()).executes(context -> {
                    List<NbtElement> list = Collections.singletonList(NbtElementArgumentType.getNbtElement(context, "value"));
                    return DataCommand.executeModify(context, objectType, modifier, list);
                })));
                return builder.then(argumentBuilder);
            });
        }
        return literalArgumentBuilder;
    }

}
