package io.github.thatrobin.ra_additions.powers;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.data.ApoliDataTypes;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.factory.PowerFactory;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.docky.utils.SerializableDataExt;
import io.github.thatrobin.ra_additions.RA_Additions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import java.util.function.Predicate;

public class BorderPower extends Power {

    private final Predicate<Entity> entityCondition;
    private final Predicate<Pair<Entity, Entity>> bientityCondition;
    private final Identifier texture;
    private final BorderPower.Area area;
    private double centerX;
    private double centerZ;
    int maxRadius = 29999984;

    public float red, green, blue, alpha;
    public boolean scrollTexture;

    public BorderPower(PowerType<?> type, LivingEntity entity, Identifier texture, Predicate<Entity> entityCondition, Predicate<Pair<Entity, Entity>> bientityCondition, double size, float red, float green, float blue, float alpha, boolean scrollTexture) {
        super(type, entity);
        this.texture = texture;
        this.entityCondition = entityCondition;
        this.bientityCondition = bientityCondition;
        this.area = new BorderPower.StaticArea(size);
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
        this.scrollTexture = scrollTexture;

        this.setTicking(true);
    }

    public void onAdded() {
        this.setCenter(entity.getX(), entity.getZ());
    }

    public void tick() {
        this.setCenter(entity.getX(), entity.getZ());
    }

    public boolean contains(double x, double z, double margin) {
        return x > this.getBoundWest() - margin && x < this.getBoundEast() + margin && z > this.getBoundNorth() - margin && z < this.getBoundSouth() + margin;
    }

    public Identifier getTexture() {
        return texture;
    }

    public boolean doesApply(Entity e) {
        return (entityCondition == null || entityCondition.test(e)) && (bientityCondition == null || bientityCondition.test(new Pair<>(e, entity)));
    }

    public boolean canCollide(Entity entity, Box box) {
        double d = Math.max(MathHelper.absMax(box.getXLength(), box.getZLength()), 1.0D);
        return this.getDistanceToBorder(entity) < d;
    }

    public double getDistanceToBorder(Entity entity) {
        return this.getDistanceToBorder(entity.getX(), entity.getZ());
    }

    public double getDistanceToBorder(double x, double z) {
        return Math.abs(getDistanceToCenter(x, z) - (area.getSize() / 2));
    }

    public double getDistanceToCenter(double x, double z) {
        return Math.max(Math.abs(z - getCenterZ()), Math.abs(x - getCenterX()));
    }

    public VoxelShape getCollidingShape(Entity entity) {
        return this.area.getCollidingShape(entity);
    }

    public double getCenterX() {
        return centerX;
    }

    public double getCenterZ() {
        return centerZ;
    }

    public void setCenter(double x, double z) {
        this.centerX = x;
        this.centerZ = z;
        this.area.recalculateBounds();
        PowerHolderComponent component = PowerHolderComponent.KEY.get(entity);
        component.sync();
    }

    public double getBoundWest() {
        return this.area.getBoundWest();
    }

    public double getBoundNorth() {
        return this.area.getBoundNorth();
    }

    public double getBoundEast() {
        return this.area.getBoundEast();
    }

    public double getBoundSouth() {
        return this.area.getBoundSouth();
    }

    @SuppressWarnings("all")
    class StaticArea implements BorderPower.Area {
        private final double size;
        private double boundWest;
        private double boundNorth;
        private double boundEast;
        private double boundSouth;
        private VoxelShape outsideShape;
        private VoxelShape insideShape;

        public StaticArea(double size) {
            this.size = size;
            this.recalculateBounds();
        }

        public double getBoundWest() {
            return this.boundWest;
        }

        public double getBoundEast() {
            return this.boundEast;
        }

        public double getBoundNorth() {
            return this.boundNorth;
        }

        public double getBoundSouth() {
            return this.boundSouth;
        }

        public double getSize() {
            return this.size;
        }

        public void recalculateBounds() {
            this.boundWest = MathHelper.clamp(BorderPower.this.getCenterX() - this.size / 2.0D, (-BorderPower.this.maxRadius), BorderPower.this.maxRadius);
            this.boundNorth = MathHelper.clamp(BorderPower.this.getCenterZ() - this.size / 2.0D, (-BorderPower.this.maxRadius), BorderPower.this.maxRadius);
            this.boundEast = MathHelper.clamp(BorderPower.this.getCenterX() + this.size / 2.0D, (-BorderPower.this.maxRadius), BorderPower.this.maxRadius);
            this.boundSouth = MathHelper.clamp(BorderPower.this.getCenterZ() + this.size / 2.0D, (-BorderPower.this.maxRadius), BorderPower.this.maxRadius);

            this.outsideShape = VoxelShapes.cuboid(this.boundWest, -1.0D / 0.0, this.boundNorth, this.boundEast, 1.0D / 0.0, this.boundSouth);
            this.insideShape = VoxelShapes.combineAndSimplify(VoxelShapes.UNBOUNDED, this.outsideShape, BooleanBiFunction.ONLY_FIRST);
        }

        public VoxelShape getCollidingShape(Entity entity) {
            if(getDistanceToCenter(entity.getX(), entity.getZ()) - (this.size / 2.0D) < 0) {
                return this.insideShape;
            }
            return this.outsideShape;
        }

    }

    private interface Area {
        double getBoundWest();

        double getBoundEast();

        double getBoundNorth();

        double getBoundSouth();

        double getSize();

        void recalculateBounds();

        VoxelShape getCollidingShape(Entity entity);
    }

    @SuppressWarnings("rawtypes")
    public static PowerFactory createFactory() {
        return new PowerFactory<>(RA_Additions.identifier("border"),
                new SerializableDataExt()
                        .add("border_texture", "The texture used on the border.", SerializableDataTypes.IDENTIFIER, new Identifier("textures/misc/forcefield.png"))
                        .add("entity_condition", "If specified, if the entity colliding with the border fulfils the condition, it can walk through the border.", ApoliDataTypes.ENTITY_CONDITION, null)
                        .add("bientity_condition", "If specified, if the entity colliding with the border, and the entity with this power fulfil the condition, the colliding entity can walk through the border.", ApoliDataTypes.BIENTITY_CONDITION, null)
                        .add("red", "The red value of the border.", SerializableDataTypes.FLOAT, 0.1254901961f)
                        .add("green", "The green value of the border.", SerializableDataTypes.FLOAT, 0.6274509804f)
                        .add("blue", "The blue value of the border.", SerializableDataTypes.FLOAT, 1f)
                        .add("alpha", "The alpha (transparency) value of the border.", SerializableDataTypes.FLOAT, 0.5f)
                        .add("scroll_texture", "Defines whether the border scrolls like the vanila border.", SerializableDataTypes.BOOLEAN, true)
                        .add("size", "The distance to one side of the border from the center.", SerializableDataTypes.DOUBLE, 6d),
                data ->
                        (type, entity) -> new BorderPower(type, entity, data.getId("border_texture"), data.get("entity_condition"), data.get("bientity_condition"), data.get("size"), data.getFloat("red"), data.getFloat("green"), data.getFloat("blue"), data.getFloat("alpha"), data.getBoolean("scroll_texture")))
                .allowCondition();
    }
}
