package com.kwpugh.mining_dims.mixin;

import com.kwpugh.mining_dims.MiningDims;
import com.kwpugh.mining_dims.init.MiningDimsRegistry;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.ServerWorldAccess;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieEntity.class)
public abstract class ZombieEntityMixin extends MobEntity
{
    private ZombieEntityMixin(EntityType<? extends HostileEntity> entityType, World world)
    {
		super(entityType, world);
	}

	@Inject(method="initEquipment",at=@At("TAIL"),cancellable = true)
	private void miningdimsInitEquipment(LocalDifficulty difficulty, CallbackInfo ci)
	{
		RegistryKey<World> registryKey = world.getRegistryKey();
		if(registryKey == MiningDimsRegistry.MININGDIMS_WORLD_KEY2)
		{
			this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.DIAMOND_SWORD));
			this.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));
			this.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.DIAMOND_CHESTPLATE));
			this.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.DIAMOND_LEGGINGS));
			this.equipStack(EquipmentSlot.FEET, new ItemStack(Items.DIAMOND_BOOTS));
		}
	}

	@Inject(method="damage",at=@At("HEAD"),cancellable = true)
	public void miningdimsApplyAttributeModifiersDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir)
	{
		Entity self = (Entity) (Object) this;
		if(self instanceof ZombieEntity)
		{
			ZombieEntity zombie = (ZombieEntity) self;
		}

		RegistryKey<World> registryKey = world.getRegistryKey();
		if(!this.world.isClient() && registryKey == MiningDimsRegistry.MININGDIMS_WORLD_KEY2)
		{
			if((source.getAttacker() instanceof PlayerEntity) && source.isProjectile())
			{
				source.getAttacker().damage(DamageSource.GENERIC, 5.0F);
			}
		}
	}

	@Inject(method="applyAttributeModifiers",at=@At("TAIL"),cancellable = true)
	public void miningDimsApplyAttributeModifiers(float chanceMultiplier, CallbackInfo ci)
	{
		double health = MiningDims.CONFIG.GENERAL.zombieMaxHealth;
		double attack = MiningDims.CONFIG.GENERAL.zombieAttackDamageBonus;
		double armor = MiningDims.CONFIG.GENERAL.zombieArmorBonus;
		double speed = MiningDims.CONFIG.GENERAL.zombieMovementBonus;

		RegistryKey<World> registryKey = world.getRegistryKey();
		if(registryKey == MiningDimsRegistry.MININGDIMS_WORLD_KEY2)
		{
			this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).addPersistentModifier(new EntityAttributeModifier("MiningDims Health Bonus", health, EntityAttributeModifier.Operation.ADDITION));
			this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).addPersistentModifier(new EntityAttributeModifier("MiningDims Attack Bonus", attack, EntityAttributeModifier.Operation.ADDITION));
			this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).addPersistentModifier(new EntityAttributeModifier("MiningDims Armor Bonus", armor, EntityAttributeModifier.Operation.ADDITION));
			this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).addPersistentModifier(new EntityAttributeModifier("MiningDims Movement Bonus", speed, EntityAttributeModifier.Operation.ADDITION));
		}
	}
}