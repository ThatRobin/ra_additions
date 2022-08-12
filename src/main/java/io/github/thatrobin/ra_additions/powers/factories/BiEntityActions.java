package io.github.thatrobin.ra_additions.powers.factories;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.thatrobin.ra_additions.RA_Additions;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.UUID;

public class BiEntityActions {

    public static void register() {
        register(new ActionFactory<>(RA_Additions.identifier("attack"), new SerializableData()
                .add("source", SerializableDataTypes.DAMAGE_SOURCE)
                .add("allow_enchants", SerializableDataTypes.BOOLEAN, false)
                .add("allow_weapons", SerializableDataTypes.BOOLEAN, false)
                .add("allow_effects", SerializableDataTypes.BOOLEAN, false)
                .add("allow_attributes", SerializableDataTypes.BOOLEAN, false),
                (data, entities) -> {
                    if (entities.getLeft() instanceof LivingEntity livingEntity) {
                        DamageSource providedSource = data.get("source");
                        DamageSource source = new EntityDamageSource(providedSource.getName(), livingEntity);
                        if (providedSource.isExplosive()) {
                            source.setExplosive();
                        }
                        if (providedSource.isProjectile()) {
                            source.setProjectile();
                        }
                        if (providedSource.isFromFalling()) {
                            source.setFromFalling();
                        }
                        if (providedSource.isMagic()) {
                            source.setUsesMagic();
                        }
                        if (providedSource.isNeutral()) {
                            source.setNeutral();
                        }
                        attack(livingEntity, entities.getRight(), source, data.getBoolean("allow_enchants"), data.getBoolean("allow_attributes"), data.getBoolean("allow_weapons"), data.getBoolean("allow_effects"));
                        if(livingEntity instanceof PlayerEntity actorEntity) {
                            actorEntity.resetLastAttackedTicks();
                        }
                    }
                }));

    }

    private static void register(ActionFactory<Pair<Entity, Entity>> actionFactory) {
        Registry.register(ApoliRegistries.BIENTITY_ACTION, actionFactory.getSerializerId(), actionFactory);
    }

    public static void attack(LivingEntity actor, Entity target, DamageSource source, boolean useEnchants, boolean useAttributes, boolean useWeapon, boolean useEffects) {
        if (!target.isAttackable()) {
            return;
        }
        if (target.handleAttack(actor)) {
            return;
        }
        float f = 1.0f;
        if (useAttributes) {
            EntityAttributeInstance entityAttributeInstance = actor.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            if(entityAttributeInstance != null) {
                if (!useWeapon) {
                    entityAttributeInstance.tryRemoveModifier(UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF"));
                }
                if (!useEffects) {
                    entityAttributeInstance.tryRemoveModifier(UUID.fromString("648D7064-6A60-4F59-8ABE-C2C23A6DD7A9"));
                    entityAttributeInstance.tryRemoveModifier(UUID.fromString("22653B89-116E-49DC-9B6B-9971489B5BE5"));
                }
                f = (float) entityAttributeInstance.getValue();
            }
        }
        float g = 0.0f;
        if (useEnchants) {
            g = target instanceof LivingEntity ? EnchantmentHelper.getAttackDamage(actor.getMainHandStack(), ((LivingEntity) target).getGroup()) : EnchantmentHelper.getAttackDamage(actor.getMainHandStack(), EntityGroup.DEFAULT);
        }
        float h = 0;
        if(actor instanceof PlayerEntity actorEntity) {
            h = actorEntity.getAttackCooldownProgress(0.5f);
            actorEntity.resetLastAttackedTicks();
        }
        g *= h;

        if ((f *= 0.2f + h * h * 0.8f) > 0.0f || g > 0.0f) {
            boolean bl = h > 0.9f;
            boolean bl2 = false;
            int i = 0;
            if (useEnchants) {
                i += EnchantmentHelper.getKnockback(actor);
            }
            if (actor.isSprinting() && bl) {
                actor.world.playSound(null, actor.getX(), actor.getY(), actor.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, actor.getSoundCategory(), 1.0f, 1.0f);
                ++i;
                bl2 = true;
            }
            boolean bl3 = bl && actor.fallDistance > 0.0f && !actor.isOnGround() && !actor.isClimbing() && !actor.isTouchingWater() && !actor.hasStatusEffect(StatusEffects.BLINDNESS) && !actor.hasVehicle() && target instanceof LivingEntity;
            if (bl3) {
                f *= 1.5f;
            }
            f += g;
            boolean bl42 = false;
            double d = actor.horizontalSpeed - actor.prevHorizontalSpeed;
            if (bl && !bl3 && !bl2 && actor.isOnGround() && d < (double)actor.getMovementSpeed() && actor.getStackInHand(Hand.MAIN_HAND).getItem() instanceof SwordItem) {
                bl42 = true;
            }
            float j = 0.0f;
            boolean bl5 = false;
            int k = 0;
            if(useEnchants) {
                k = EnchantmentHelper.getFireAspect(actor);
            }
            if (target instanceof LivingEntity) {
                j = ((LivingEntity)target).getHealth();
                if (k > 0 && !target.isOnFire()) {
                    bl5 = true;
                    target.setOnFireFor(1);
                }
            }
            Vec3d vec3d = target.getVelocity();
            boolean bl6 = target.damage(source, f);
            if (bl6) {
                if (i > 0) {
                    if (target instanceof LivingEntity) {
                        ((LivingEntity)target).takeKnockback((float)i * 0.5f, MathHelper.sin(actor.getYaw() * ((float)Math.PI / 180)), -MathHelper.cos(actor.getYaw() * ((float)Math.PI / 180)));
                    } else {
                        target.addVelocity(-MathHelper.sin(actor.getYaw() * ((float)Math.PI / 180)) * (float)i * 0.5f, 0.1, MathHelper.cos(actor.getYaw() * ((float)Math.PI / 180)) * (float)i * 0.5f);
                    }
                    actor.setVelocity(actor.getVelocity().multiply(0.6, 1.0, 0.6));
                    actor.setSprinting(false);
                }
                if (bl42) {
                    float l = 1.0f + EnchantmentHelper.getSweepingMultiplier(actor) * f;
                    List<LivingEntity> list = actor.world.getNonSpectatingEntities(LivingEntity.class, target.getBoundingBox().expand(1.0, 0.25, 1.0));
                    for (LivingEntity livingEntity : list) {
                        if (livingEntity == actor || livingEntity == target || actor.isTeammate(livingEntity) || livingEntity instanceof ArmorStandEntity && ((ArmorStandEntity)livingEntity).isMarker() || !(actor.squaredDistanceTo(livingEntity) < 9.0)) continue;
                        livingEntity.takeKnockback(0.4f, MathHelper.sin(actor.getYaw() * ((float)Math.PI / 180)), -MathHelper.cos(actor.getYaw() * ((float)Math.PI / 180)));
                        livingEntity.damage(source, l);
                    }
                    actor.world.playSound(null, actor.getX(), actor.getY(), actor.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, actor.getSoundCategory(), 1.0f, 1.0f);
                    PlayerEntity actorEntity = (PlayerEntity) actor;
                    actorEntity.spawnSweepAttackParticles();
                }
                if (target instanceof ServerPlayerEntity && target.velocityModified) {
                    ((ServerPlayerEntity)target).networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(target));
                    target.velocityModified = false;
                    target.setVelocity(vec3d);
                }
                if (bl3) {
                    actor.world.playSound(null, actor.getX(), actor.getY(), actor.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, actor.getSoundCategory(), 1.0f, 1.0f);
                    PlayerEntity actorEntity = (PlayerEntity) actor;
                    actorEntity.addCritParticles(target);
                }
                if (!bl3 && !bl42) {
                    if (bl) {
                        actor.world.playSound(null, actor.getX(), actor.getY(), actor.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, actor.getSoundCategory(), 1.0f, 1.0f);
                    } else {
                        actor.world.playSound(null, actor.getX(), actor.getY(), actor.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, actor.getSoundCategory(), 1.0f, 1.0f);
                    }
                }
                if (g > 0.0f) {
                    if(actor instanceof PlayerEntity actorEntity) {
                        actorEntity.addEnchantedHitParticles(target);
                    }
                }
                actor.onAttacking(target);
                if (target instanceof LivingEntity) {
                    EnchantmentHelper.onUserDamaged((LivingEntity)target, actor);
                }
                EnchantmentHelper.onTargetDamaged(actor, target);
                ItemStack itemStack2 = actor.getMainHandStack();
                Entity entity = target;
                if (target instanceof EnderDragonPart) {
                    entity = ((EnderDragonPart)target).owner;
                }
                if (!actor.world.isClient && !itemStack2.isEmpty() && entity instanceof LivingEntity) {
                    if(actor instanceof PlayerEntity actorEntity) {
                        itemStack2.postHit((LivingEntity) entity, actorEntity);
                    }
                    if (itemStack2.isEmpty()) {
                        actor.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
                    }
                }
                if (target instanceof LivingEntity) {
                    float m = j - ((LivingEntity)target).getHealth();
                    if(actor instanceof PlayerEntity actorEntity) {
                        actorEntity.increaseStat(Stats.DAMAGE_DEALT, Math.round(m * 10.0f));
                    }
                    if (k > 0) {
                        target.setOnFireFor(k * 4);
                    }
                    if (actor.world instanceof ServerWorld && m > 2.0f) {
                        int n = (int)((double)m * 0.5);
                        ((ServerWorld)actor.world).spawnParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getBodyY(0.5), target.getZ(), n, 0.1, 0.0, 0.1, 0.2);
                    }
                }
                if(actor instanceof PlayerEntity actorEntity) {
                    actorEntity.addExhaustion(0.1f);
                }
            } else {
                actor.world.playSound(null, actor.getX(), actor.getY(), actor.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, actor.getSoundCategory(), 1.0f, 1.0f);
                if (bl5) {
                    target.extinguish();
                }
            }
        }
    }
}
