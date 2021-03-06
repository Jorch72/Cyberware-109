package flaxbeard.cyberware.common.item;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.google.common.collect.HashMultimap;

import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.common.lib.LibConstants;

public class ItemBoneUpgrade extends ItemCyberware
{

	public ItemBoneUpgrade(String name, EnumSlot slot, String[] subnames)
	{
		super(name, slot, subnames);
		MinecraftForge.EVENT_BUS.register(this);

	}
	
	private static final UUID healthId = UUID.fromString("8bce997a-4c3a-11e6-beb8-9e71128cae77");

	@Override
	public void onAdded(EntityLivingBase entity, ItemStack stack)
	{
		if (stack.getItemDamage() == 0)
		{
			HashMultimap<String, AttributeModifier> multimap = HashMultimap.<String, AttributeModifier>create();
			
			multimap.put(SharedMonsterAttributes.MAX_HEALTH.getAttributeUnlocalizedName(), new AttributeModifier(healthId, "Bone hp upgrade", 4F * stack.stackSize, 0));
			entity.getAttributeMap().applyAttributeModifiers(multimap);
		}
	}
	
	@Override
	public void onRemoved(EntityLivingBase entity, ItemStack stack)
	{
		if (stack.getItemDamage() == 0)
		{
			//System.out.println("REMOVED0");
			HashMultimap<String, AttributeModifier> multimap = HashMultimap.<String, AttributeModifier>create();
			
			multimap.put(SharedMonsterAttributes.MAX_HEALTH.getAttributeUnlocalizedName(), new AttributeModifier(healthId, "Bone hp upgrade", 4F * stack.stackSize, 0));
			entity.getAttributeMap().removeAttributeModifiers(multimap);
		}
	}
	
	@SubscribeEvent
	public void handleJoinWorld(EntityJoinWorldEvent event)
	{
		Entity e = event.getEntity();
		
		ItemStack test = new ItemStack(this, 1, 0);
		if (e instanceof EntityLivingBase && CyberwareAPI.isCyberwareInstalled(e, test))
		{
			this.onAdded((EntityLivingBase) e, CyberwareAPI.getCyberware(e, test));
		}
		else if (CyberwareAPI.hasCapability(e) && e instanceof EntityLivingBase)
		{
			this.onRemoved((EntityLivingBase) e, test);
		}
	}
	
	@SubscribeEvent
	public void handleFallDamage(LivingHurtEvent event)
	{
		EntityLivingBase e = event.getEntityLiving();
		
		if (CyberwareAPI.isCyberwareInstalled(e, new ItemStack(this, 1, 1)))
		{

			if (event.getSource() == DamageSource.fall)
			{
				event.setAmount(event.getAmount() * .3333F);
			}
			
		}
	}
	
	@Override
	public boolean isIncompatible(ItemStack stack, ItemStack other)
	{
		return other.getItem() == this;
	}	@Override
	
	public int getCapacity(ItemStack wareStack)
	{
		return wareStack.getItemDamage() == 2 ? LibConstants.BONE_BATTERY_CAPACITY * wareStack.stackSize : 0;
	}
	
	@Override
	public int installedStackSize(ItemStack stack)
	{
		return stack.getItemDamage() == 0 ? 5 : 
			stack.getItemDamage() == 2 ? 4 : 1;
	}
	
	@Override
	protected int getUnmodifiedEssenceCost(ItemStack stack)
	{
		if (stack.getItemDamage() == 0)
		{
			switch (stack.stackSize)
			{
				case 1:
					return 3;
				case 2:
					return 6;
				case 3:
					return 9;
				case 4:
					return 12;
				case 5:
					return 15;
			}
		}
		if (stack.getItemDamage() == 2)
		{
			switch (stack.stackSize)
			{
				case 1:
					return 2;
				case 2:
					return 3;
				case 3:
					return 4;
				case 4:
					return 5;
			}
		}
		return super.getUnmodifiedEssenceCost(stack);
	}
	
}
