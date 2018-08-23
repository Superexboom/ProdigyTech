package lykrast.prodigytech.common.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;

public class ZorraAltarManager {
	public static final ZorraAltarManager SWORD = new ZorraAltarManager();
	
	public static void init() {
		SWORD.addEnchant(Enchantments.SHARPNESS, 8);
		SWORD.addEnchant(Enchantments.SMITE, 8);
		SWORD.addEnchant(Enchantments.BANE_OF_ARTHROPODS, 8);
		SWORD.addEnchant(Enchantments.FIRE_ASPECT, 5);
		SWORD.addEnchant(Enchantments.KNOCKBACK, 5);
		SWORD.addEnchant(Enchantments.LOOTING, 6);
		SWORD.addEnchant(Enchantments.SWEEPING, 6);
	}
	
	private List<EnchantmentData> enchants;
	
	public ZorraAltarManager() {
		enchants = new ArrayList<>();
	}
	
	public void addEnchant(EnchantmentData enchant) {
		enchants.add(enchant);
	}
	
	/**
	 * Adds the given enchantment to the pool, with the given maximul level
	 * @param enchant enchantment to add
	 * @param maxLvl maximum applicable level
	 */
	public void addEnchant(Enchantment enchant, int maxLvl) {
		enchants.add(new EnchantmentData(enchant, maxLvl));
	}
	
	/**
	 * Gives the base cost in levels of applying this enchantment
	 * @param data EnchantmentData to apply
	 * @return cost in level to apply it
	 */
	public int getLevelCost(EnchantmentData data) {
		int lvl = data.enchantmentLevel;
		if (lvl <= 1) return data.enchantment.getMinEnchantability(1);
		else return data.enchantment.getMinEnchantability(lvl) - (data.enchantment.getMinEnchantability(lvl - 1) / 2);
	}
	
	/**
	 * Applies a random deviation to the given level cost
	 * @param cost level cost to deviate
	 * @param rand Random to use
	 * @return the cost randomly deviated
	 */
	public int deviate(int cost, Random rand) {
		int deviation = Math.max(2, cost / 10);
		return Math.max(1, cost - deviation + rand.nextInt(deviation * 2 + 1));
	}
	
	/**
	 * Gives a deviated level cost for applying the given enchantment
	 * @param data EnchantmentData to apply
	 * @param rand Random to use
	 * @return cost in level to apply it, randomly deviated
	 */
	public int getRandomLevelCost(EnchantmentData data, Random rand) {
		return deviate(getLevelCost(data), rand);
	}
	
	public List<EnchantmentData> getAvailableEnchants(ItemStack stack) {
		List<EnchantmentData> list = new ArrayList<>();
		Map<Enchantment,Integer> map = EnchantmentHelper.getEnchantments(stack);
		for (EnchantmentData data : enchants) {
			Integer lvl = map.get(data.enchantment);
			if (lvl == null) list.add(new EnchantmentData(data.enchantment, 1));
			else if (lvl < data.enchantmentLevel) list.add(new EnchantmentData(data.enchantment, lvl + 1));
		}
		
		return list;
	}
	
	/**
	 * Gives 3 random enchantments for the Zorra Altar
	 * @param stack ItemStack to apply enchantments for
	 * @param rand Random to use
	 * @return up to 3 applicable enchantments
	 */
	public EnchantmentData[] getRandomEnchants(ItemStack stack, Random rand) {
		List<EnchantmentData> apply = new ArrayList<>();
		List<EnchantmentData> upgrade = new ArrayList<>();
		
		Map<Enchantment,Integer> map = EnchantmentHelper.getEnchantments(stack);
		for (EnchantmentData data : enchants) {
			Integer lvl = map.get(data.enchantment);
			if (lvl == null) apply.add(new EnchantmentData(data.enchantment, 1));
			else if (lvl < data.enchantmentLevel) upgrade.add(new EnchantmentData(data.enchantment, lvl + 1));
		}
		
		EnchantmentData[] datas = new EnchantmentData[3];
		
		if (!apply.isEmpty() || !upgrade.isEmpty())
		{
			//1st is always a new enchant if possible
			if (!apply.isEmpty()) {
				int i = rand.nextInt(apply.size());
				datas[0] = apply.remove(i);
			}
			else {
				int i = rand.nextInt(upgrade.size());
				datas[0] = upgrade.remove(i);
			}
			
			//2nd is always an upgrade if possible
			if (!upgrade.isEmpty()) {
				int i = rand.nextInt(upgrade.size());
				datas[1] = upgrade.remove(i);
			}
			else if (!apply.isEmpty()) {
				int i = rand.nextInt(apply.size());
				datas[1] = apply.remove(i);
			}
			
			//3rd is random
			apply.addAll(upgrade);
			if (!apply.isEmpty()) {
				int i = rand.nextInt(apply.size());
				datas[2] = apply.remove(i);
			}
		}
			
		return datas;
	}
}
