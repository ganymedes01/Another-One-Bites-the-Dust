package ganymedes01.aobd.recipes.modules;

import ganymedes01.aobd.lib.CompatType;
import ganymedes01.aobd.lib.Reference;
import ganymedes01.aobd.ore.Ore;
import ganymedes01.aobd.recipes.RecipesModule;

import java.util.ArrayList;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.common.config.ConfigResearch;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.registry.GameRegistry;

public class Thaumcraft extends RecipesModule {

	public Thaumcraft() {
		super(CompatType.THAUMCRAFT, "iron", "gold", "copper", "tin", "lead", "silver");
	}

	@Override
	public void initOre(Ore ore) {
		String name = ore.name();
		ItemStack cluster = getOreStack("cluster", ore);
		for (ItemStack block : OreDictionary.getOres("ore" + name)) {
			String s1 = Item.getIdFromItem(block.getItem()) + "," + block.getItemDamage();
			String s2 = Item.getIdFromItem(cluster.getItem()) + "," + cluster.getItemDamage();
			FMLInterModComms.sendMessage("Thaumcraft", "nativeCluster", s1 + "," + s2 + "," + 1);
		}
		GameRegistry.addSmelting(cluster, getOreStack("ingot", ore, 2), 0.2F);
		ThaumcraftApi.addSmeltingBonus(cluster, getOreStack("nugget", ore, 0));
	}

	@Override
	public void postInit() {
		boolean addedAtLeastOne = false;
		ArrayList<ResearchPage> pages = new ArrayList<ResearchPage>();
		pages.add(new ResearchPage("tc.research_page.PUREORE.1"));
		for (Ore ore : Ore.ores)
			if (isOreEnabled(ore)) {
				String name = ore.name();
				ItemStack cluster = getOreStack("cluster", ore);

				CrucibleRecipe recipe = ThaumcraftApi.addCrucibleRecipe("PUREORE", cluster, "ore" + name, new AspectList().merge(Aspect.METAL, 1).merge(Aspect.ORDER, 1));
				ConfigResearch.recipes.put("Pure" + name, recipe);
				pages.add(new ResearchPage(recipe));
				addedAtLeastOne = true;
			}

		if (addedAtLeastOne) {
			ResourceLocation texture = new ResourceLocation(Reference.MOD_ID, "textures/items/research_cluster.png");
			ResearchCategories.registerCategory("AOBD", texture, new ResourceLocation("thaumcraft", "textures/gui/gui_researchback.png"));
			new ResearchItem("PUREORE", "AOBD", new AspectList().add(Aspect.METAL, 5).add(Aspect.ORDER, 2), 0, 0, 1, texture).setPages(pages.toArray(new ResearchPage[pages.size()])).setSecondary().setParents("PUREIRON").registerResearchItem();
		}
	}
}