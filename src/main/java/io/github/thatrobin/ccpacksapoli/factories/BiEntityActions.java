package io.github.thatrobin.ccpacksapoli.factories;

import io.github.apace100.apoli.power.factory.action.ActionFactory;
import io.github.apace100.apoli.registry.ApoliRegistries;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.calio.data.SerializableDataType;
import io.github.apace100.calio.data.SerializableDataTypes;
import io.github.apace100.calio.mixin.DamageSourceAccessor;
import io.github.thatrobin.ccpacksapoli.CCPacksApoli;
import io.github.thatrobin.ccpacksapoli.util.PlayerEntityExtention;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
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
        register(new ActionFactory<>(CCPacksApoli.identifier("attack"), new SerializableData()
                .add("source", SerializableDataTypes.DAMAGE_SOURCE)
                .add("allow_enchants", SerializableDataTypes.BOOLEAN, false)
                .add("allow_weapons", SerializableDataTypes.BOOLEAN, false)
                .add("allow_effects", SerializableDataTypes.BOOLEAN, false)
                .add("allow_attributes", SerializableDataTypes.BOOLEAN, false),
                (data, entities) -> {
                    if(entities.getLeft() instanceof PlayerEntity attacker) {
                        DamageSource providedSource = data.get("source");
                        DamageSource source = new EntityDamageSource(providedSource.getName(), attacker);
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
                        attack(attacker, entities.getRight(), source, data.getBoolean("allow_enchants"), data.getBoolean("allow_attributes"), data.getBoolean("allow_weapons"), data.getBoolean("allow_effects"));
                        attacker.resetLastAttackedTicks();
                    }
                }));

    }

    private static void register(ActionFactory<Pair<Entity, Entity>> actionFactory) {
        Registry.register(ApoliRegistries.BIENTITY_ACTION, actionFactory.getSerializerId(), actionFactory);
    }

    public static void attack(PlayerEntity player, Entity target, DamageSource source, boolean useEnchants, boolean useAttributes, boolean useWeapon, boolean useEffects) {
        if (!target.isAttackable()) {
            return;
        }
        if (target.handleAttack(player)) {
            return;
        }
        float f;
        if (useAttributes) {
            EntityAttributeInstance entityAttributeInstance = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            if(!useWeapon) {
                entityAttributeInstance.removeModifier(UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF"));
            }
            if(!useEffects) {
                entityAttributeInstance.removeModifier(UUID.fromString("648D7064-6A60-4F59-8ABE-C2C23A6DD7A9"));
                entityAttributeInstance.removeModifier(UUID.fromString("22653B89-116E-49DC-9B6B-9971489B5BE5"));
            }
            f = (float) entityAttributeInstance.getValue();
        } else {
            f = 1.0f;
        }
        float g = 0.0f;
        if (useEnchants) {
            g = target instanceof LivingEntity ? EnchantmentHelper.getAttackDamage(player.getMainHandStack(), ((LivingEntity) target).getGroup()) : EnchantmentHelper.getAttackDamage(player.getMainHandStack(), EntityGroup.DEFAULT);
        }
        float h = player.getAttackCooldownProgress(0.5f);
        g *= h;
        player.resetLastAttackedTicks();
        if ((f *= 0.2f + h * h * 0.8f) > 0.0f || g > 0.0f) {
            ItemStack itemStack;
            boolean bl = h > 0.9f;
            boolean bl2 = false;
            int i = 0;
            if (useEnchants) {
                i += EnchantmentHelper.getKnockback(player);
            }
            if (player.isSprinting() && bl) {
                player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, player.getSoundCategory(), 1.0f, 1.0f);
                ++i;
                bl2 = true;
            }
            boolean bl3 = bl && player.fallDistance > 0.0f && !player.isOnGround() && !player.isClimbing() && !player.isTouchingWater() && !player.hasStatusEffect(StatusEffects.BLINDNESS) && !player.hasVehicle() && target instanceof LivingEntity;
            boolean bl4 = bl3 = bl3 && !player.isSprinting();
            if (bl3) {
                f *= 1.5f;
            }
            f += g;
            boolean bl42 = false;
            double d = player.horizontalSpeed - player.prevHorizontalSpeed;
            if (bl && !bl3 && !bl2 && player.isOnGround() && d < (double)player.getMovementSpeed() && (itemStack = player.getStackInHand(Hand.MAIN_HAND)).getItem() instanceof SwordItem) {
                bl42 = true;
            }
            float j = 0.0f;
            boolean bl5 = false;
            int k = 0;
            if(useEnchants) {
                k = EnchantmentHelper.getFireAspect(player);
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
                        ((LivingEntity)target).takeKnockback((float)i * 0.5f, MathHelper.sin(player.getYaw() * ((float)Math.PI / 180)), -MathHelper.cos(player.getYaw() * ((float)Math.PI / 180)));
                    } else {
                        target.addVelocity(-MathHelper.sin(player.getYaw() * ((float)Math.PI / 180)) * (float)i * 0.5f, 0.1, MathHelper.cos(player.getYaw() * ((float)Math.PI / 180)) * (float)i * 0.5f);
                    }
                    player.setVelocity(player.getVelocity().multiply(0.6, 1.0, 0.6));
                    player.setSprinting(false);
                }
                if (bl42) {
                    float l = 1.0f + EnchantmentHelper.getSweepingMultiplier(player) * f;
                    List<LivingEntity> list = player.world.getNonSpectatingEntities(LivingEntity.class, target.getBoundingBox().expand(1.0, 0.25, 1.0));
                    for (LivingEntity livingEntity : list) {
                        if (livingEntity == player || livingEntity == target || player.isTeammate(livingEntity) || livingEntity instanceof ArmorStandEntity && ((ArmorStandEntity)livingEntity).isMarker() || !(player.squaredDistanceTo(livingEntity) < 9.0)) continue;
                        livingEntity.takeKnockback(0.4f, MathHelper.sin(player.getYaw() * ((float)Math.PI / 180)), -MathHelper.cos(player.getYaw() * ((float)Math.PI / 180)));
                        livingEntity.damage(source, l);
                    }
                    player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, player.getSoundCategory(), 1.0f, 1.0f);
                    player.spawnSweepAttackParticles();
                }
                if (target instanceof ServerPlayerEntity && target.velocityModified) {
                    ((ServerPlayerEntity)target).networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(target));
                    target.velocityModified = false;
                    target.setVelocity(vec3d);
                }
                if (bl3) {
                    player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, player.getSoundCategory(), 1.0f, 1.0f);
                    player.addCritParticles(target);
                }
                if (!bl3 && !bl42) {
                    if (bl) {
                        player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, player.getSoundCategory(), 1.0f, 1.0f);
                    } else {
                        player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, player.getSoundCategory(), 1.0f, 1.0f);
                    }
                }
                if (g > 0.0f) {
                    player.addEnchantedHitParticles(target);
                }
                player.onAttacking(target);
                if (target instanceof LivingEntity) {
                    EnchantmentHelper.onUserDamaged((LivingEntity)target, player);
                }
                EnchantmentHelper.onTargetDamaged(player, target);
                ItemStack itemStack2 = player.getMainHandStack();
                Entity entity = target;
                if (target instanceof EnderDragonPart) {
                    entity = ((EnderDragonPart)target).owner;
                }
                if (!player.world.isClient && !itemStack2.isEmpty() && entity instanceof LivingEntity) {
                    itemStack2.postHit((LivingEntity)entity, player);
                    if (itemStack2.isEmpty()) {
                        player.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
                    }
                }
                if (target instanceof LivingEntity) {
                    float m = j - ((LivingEntity)target).getHealth();
                    player.increaseStat(Stats.DAMAGE_DEALT, Math.round(m * 10.0f));
                    if (k > 0) {
                        target.setOnFireFor(k * 4);
                    }
                    if (player.world instanceof ServerWorld && m > 2.0f) {
                        int n = (int)((double)m * 0.5);
                        ((ServerWorld)player.world).spawnParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getBodyY(0.5), target.getZ(), n, 0.1, 0.0, 0.1, 0.2);
                    }
                }
                player.addExhaustion(0.1f);
            } else {
                player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, player.getSoundCategory(), 1.0f, 1.0f);
                if (bl5) {
                    target.extinguish();
                }
            }
        }
    }
}
