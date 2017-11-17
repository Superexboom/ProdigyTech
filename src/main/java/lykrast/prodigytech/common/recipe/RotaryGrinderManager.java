package lykrast.prodigytech.common.recipe;

import java.util.ArrayList;
import java.util.List;

import lykrast.prodigytech.common.util.Config;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class RotaryGrinderManager {
	public static final List<RotaryGrinderRecipe> RECIPES = new ArrayList<>();
	
	public static RotaryGrinderRecipe addRecipe(ItemStack in, ItemStack out)
	{
		return addRecipe(new RotaryGrinderRecipe(in, out));
	}
	
	public static RotaryGrinderRecipe addRecipe(ItemStack in, ItemStack out, int time)
	{
		return addRecipe(new RotaryGrinderRecipe(in, out, time));
	}
	
	public static RotaryGrinderRecipe addRecipe(String in, ItemStack out)
	{
		return addRecipe(new RotaryGrinderRecipe(in, out));
	}
	
	public static RotaryGrinderRecipe addRecipe(String in, ItemStack out, int time)
	{
		return addRecipe(new RotaryGrinderRecipe(in, out, time));
	}
	
	private static RotaryGrinderRecipe addRecipe(RotaryGrinderRecipe recipe)
	{
		RECIPES.add(recipe);
		return recipe;
	}
	
	public static RotaryGrinderRecipe findRecipe(ItemStack in)
	{
		for (RotaryGrinderRecipe recipe : RECIPES)
			if (recipe.isValidInput(in)) return recipe;
		
		return null;
	}
	
	//Probably REALLY need to optimise those one day
	public static boolean isValidInput(ItemStack check)
	{
		for (RotaryGrinderRecipe recipe : RECIPES)
			if (recipe.isValidInput(check)) return true;
		
		return false;
	}
	
	public static void init()
	{
		addRecipe(new ItemStack(Blocks.STONE), new ItemStack(Blocks.COBBLESTONE));
		addRecipe(new ItemStack(Blocks.COBBLESTONE), new ItemStack(Blocks.GRAVEL));
		addRecipe(new ItemStack(Blocks.GRAVEL), new ItemStack(Blocks.SAND));
	}
	
	public static class RotaryGrinderRecipe {
		private final ItemStack input;
		private final ItemStack output;
		private final int time, oreInput;
		
		public RotaryGrinderRecipe(ItemStack input, ItemStack output)
		{
			this(input, output, Config.rotaryGrinderProcessTime);
		}
		
		public RotaryGrinderRecipe(ItemStack input, ItemStack output, int time)
		{
			this.input = input;
			//No support for recipes requiring multiple items for now
			input.setCount(1);
			this.output = output;
			this.time = time;
			oreInput = -1;
		}
		
		public RotaryGrinderRecipe(String input, ItemStack output)
		{
			this(input, output, Config.rotaryGrinderProcessTime);
		}
		
		public RotaryGrinderRecipe(String input, ItemStack output, int time)
		{
			oreInput = OreDictionary.getOreID(input);
			this.input = ItemStack.EMPTY;
			this.output = output;
			this.time = time;
		}
		
		public ItemStack getInput()
		{
			return input.copy();
		}
		
		public int getOreID()
		{
			return oreInput;
		}
		
		public ItemStack getOutput()
		{
			return output.copy();
		}
		
		public int getTimeTicks()
		{
			return time;
		}
		
		public int getTimeProcessing()
		{
			return time * 10;
		}
		
		public boolean isValidInput(ItemStack in)
		{
			if (in.isEmpty()) return false;
			if (oreInput != -1)
			{
				int[] oreIDs = OreDictionary.getOreIDs(in);
				for (int i : oreIDs)
				{
					if (i == oreInput) return true;
				}
				return false;
			}
			
			return (in.isItemEqual(input) && in.getCount() >= input.getCount());
		}
	}

}
