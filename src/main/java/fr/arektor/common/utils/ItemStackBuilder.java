package fr.arektor.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.map.MapView;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

public final class ItemStackBuilder {

	private String locationName;
	private MapView mapView = null;
	private Material material;
	private int amount = 1, durability = 0, modelData = 0;
	private String displayName;
	private List<String> lore = new ArrayList<String>();
	private Map<Enchantment, Integer> enchantmentIntegerMap = new HashMap<Enchantment, Integer>();
	private OfflinePlayer owner;
	private List<String> pages;
	private String author;
	private FireworkEffect fireworkEffect;
	private Color color = null;
	private boolean scaling;
	private boolean unbreakable;
	private PotionData potionData = new PotionData(PotionType.WATER, false, false);
	private List<PotionEffect> potionEffects = new ArrayList<PotionEffect>();
	private List<ItemFlag> itemFlags = new ArrayList<ItemFlag>();
	private List<Material> placedOn = new ArrayList<Material>();
	private List<Material> canBreak = new ArrayList<Material>();
	private List<Pattern> patterns = new ArrayList<Pattern>();
	
	public ItemStackBuilder() {}
	public ItemStackBuilder(ItemStack base) {
		load(base);
	}

	public ItemStackBuilder load(ItemStack is) {
		if (is == null) return this;
		this.material = is.getType();

		for (Entry<Enchantment,Integer> entry : is.getEnchantments().entrySet()) {
			this.enchantmentIntegerMap.put(entry.getKey(), entry.getValue());
		}
		if (is.hasItemMeta()) {
			ItemMeta itemMeta = is.getItemMeta();
			if (itemMeta instanceof Damageable) {
				this.durability = ((Damageable)itemMeta).getDamage();
			}

			if (itemMeta instanceof BannerMeta) {
				this.patterns = ((BannerMeta) itemMeta).getPatterns();
			} else if (itemMeta instanceof BookMeta) {
				this.author = ((BookMeta) itemMeta).getAuthor();
				this.pages = ((BookMeta) itemMeta).getPages();
				this.displayName = ((BookMeta) itemMeta).getTitle();
			} else if (itemMeta instanceof SkullMeta && owner != null) {
				this.owner = ((SkullMeta) itemMeta).getOwningPlayer();
			} else if (itemMeta instanceof PotionMeta) {
				this.potionData = ((PotionMeta) itemMeta).getBasePotionData();
				this.color = ((PotionMeta) itemMeta).getColor();
				this.potionEffects = ((PotionMeta) itemMeta).getCustomEffects();
			} else if (itemMeta instanceof EnchantmentStorageMeta) {
				this.enchantmentIntegerMap.putAll(((EnchantmentStorageMeta) itemMeta).getStoredEnchants());
			} else if (itemMeta instanceof FireworkEffectMeta) {
				this.fireworkEffect = ((FireworkEffectMeta) itemMeta).getEffect();
			} else if (itemMeta instanceof LeatherArmorMeta) {
				this.color = ((LeatherArmorMeta) itemMeta).getColor();
			} else if (itemMeta instanceof MapMeta) {
				this.scaling = ((MapMeta) itemMeta).isScaling();
				this.mapView = ((MapMeta) itemMeta).getMapView();
				this.locationName = ((MapMeta) itemMeta).getLocationName();
				this.color = ((MapMeta) itemMeta).getColor();
			}

			this.displayName = itemMeta.getDisplayName();
			this.lore = itemMeta.getLore();
			this.itemFlags = new ArrayList<ItemFlag>(itemMeta.getItemFlags());
			this.unbreakable = itemMeta.isUnbreakable();
		}
		return this;
	}

	public ItemStackBuilder withModel(int modelId) {
		this.modelData = modelId;
		return this;
	}

	public ItemStackBuilder withPotionData(PotionData potionData) {
		this.potionData = potionData;
		return this;
	}

	public ItemStackBuilder withMaterial(Material material) {
		this.material = material;
		return this;
	}

	public ItemStackBuilder withAmount(int amount) {
		this.amount = amount;
		return this;
	}

	public ItemStackBuilder withDurability(short durability) {
		this.durability = durability;
		return this;
	}

	public ItemStackBuilder withDurability(int durability) {
		this.durability = durability;
		return this;
	}

	public ItemStackBuilder withDisplayName(String displayName) {
		this.displayName = displayName;
		return this;
	}

	public ItemStackBuilder withLore(List<String> lore) {
		if (lore == null) {
			this.lore = new ArrayList<String>();
			return this;
		}
		this.lore = lore;
		return this;
	}

	public ItemStackBuilder withLore(List<String> lore, String... additional) {
		if (lore == null) {
			this.lore = new ArrayList<String>();
			return this;
		}
		this.lore = lore;
		for (String s : additional) this.lore.add(s);
		return this;
	}

	public ItemStackBuilder addLore(String string) {
		lore.add(string);
		return this;
	}

	public ItemStackBuilder withUnbreakable(boolean unbreakable) {
		this.unbreakable = unbreakable;
		return this;
	}

	public ItemStackBuilder withItemFlags(List<ItemFlag> flags) {
		if (flags == null) {
			this.itemFlags = new ArrayList<ItemFlag>();
			return this;
		}
		this.itemFlags = flags;
		return this;
	}

	public ItemStackBuilder withItemFlags(List<ItemFlag> flags, ItemFlag... additional) {
		if (flags == null) {
			this.itemFlags = new ArrayList<ItemFlag>();
			return this;
		}
		this.itemFlags = flags;
		for (ItemFlag flag : additional) if (!itemFlags.contains(flag)) itemFlags.add(flag);
		return this;
	}

	public ItemStackBuilder withItemFlags(ItemFlag... additional) {
		for (ItemFlag flag : additional) if (!itemFlags.contains(flag)) itemFlags.add(flag);
		return this;
	}

	public ItemStackBuilder addItemFlags(ItemFlag flag) {
		if (!itemFlags.contains(flag)) itemFlags.add(flag);
		return this;
	}

	public ItemStackBuilder withPlacedOn(List<Material> flags) {
		if (flags == null) {
			this.placedOn = new ArrayList<Material>();
			return this;
		}
		this.placedOn = flags;
		return this;
	}

	public ItemStackBuilder withLocationName(String locName) {
		this.locationName = locName;
		return this;
	}

	public ItemStackBuilder withMapView(MapView mapView) {
		this.mapView = mapView;
		return this;
	}

	public ItemStackBuilder withPlacedOn(List<Material> flags, Material... additional) {
		if (flags == null) {
			this.placedOn = new ArrayList<Material>();
			return this;
		}
		this.placedOn = flags;
		for (Material flag : additional) if (!placedOn.contains(flag)) placedOn.add(flag);
		return this;
	}

	public ItemStackBuilder withPlacedOn(Material... additional) {
		for (Material flag : additional) if (!placedOn.contains(flag)) placedOn.add(flag);
		return this;
	}

	public ItemStackBuilder addPlacedOn(Material flag) {
		if (!placedOn.contains(flag)) placedOn.add(flag);
		return this;
	}

	public ItemStackBuilder withCanBreak(List<Material> flags) {
		if (flags == null) {
			this.canBreak = new ArrayList<Material>();
			return this;
		}
		this.canBreak = flags;
		return this;
	}

	public ItemStackBuilder withCanBreak(List<Material> flags, Material... additional) {
		if (flags == null) {
			this.canBreak = new ArrayList<Material>();
			return this;
		}
		this.canBreak = flags;
		for (Material flag : additional) if (!canBreak.contains(flag)) canBreak.add(flag);
		return this;
	}

	public ItemStackBuilder withCanBreak(Material... additional) {
		for (Material flag : additional) if (!canBreak.contains(flag)) canBreak.add(flag);
		return this;
	}

	public ItemStackBuilder addCanBreak(Material flag) {
		if (!canBreak.contains(flag)) canBreak.add(flag);
		return this;
	}

	public ItemStackBuilder withEnchantmentMap(Map<Enchantment, Integer> map) {
		if (map == null) {
			this.enchantmentIntegerMap = new HashMap<>();
			return this;
		}
		this.enchantmentIntegerMap = map;
		return this;
	}

	public ItemStackBuilder addEnchantment(Enchantment enchantment, Integer integer) {
		this.enchantmentIntegerMap.put(enchantment, integer);
		return this;
	}

	public ItemStackBuilder withOwner(UUID owner) {
		this.owner = Bukkit.getOfflinePlayer(owner);
		return this;
	}

	public ItemStackBuilder withOwner(OfflinePlayer owner) {
		this.owner = owner;
		return this;
	}

	public ItemStackBuilder withPages(String... pages) {
		this.pages = Arrays.asList(pages);
		return this;
	}

	public ItemStackBuilder withPages(List<String> pages) {
		this.pages = pages;
		return this;
	}

	public ItemStackBuilder withAuthor(String author) {
		this.author = author;
		return this;
	}

	public ItemStackBuilder withFireworkEffect(FireworkEffect effect) {
		this.fireworkEffect = effect;
		return this;
	}

	public ItemStackBuilder withColor(Color color) {
		this.color = color;
		return this;
	}

	public ItemStackBuilder withScaling(boolean scaling) {
		this.scaling = scaling;
		return this;
	}

	public ItemStackBuilder withPotionEffects(List<PotionEffect> effects) {
		if (effects == null) {
			this.potionEffects = new ArrayList<PotionEffect>();
			return this;
		}
		this.potionEffects = effects;
		return this;
	}

	public ItemStackBuilder withPotionEffects(PotionEffect... effects) {
		if (effects == null) {
			this.potionEffects = new ArrayList<PotionEffect>();
			return this;
		}
		if (this.potionEffects == null) this.potionEffects = new ArrayList<PotionEffect>();
		for (PotionEffect pe : effects) this.potionEffects.add(pe);
		return this;
	}

	public ItemStackBuilder withPotionEffect(PotionEffect effect) {
		this.potionEffects.add(effect);
		return this;
	}

	public ItemStack build() {
		if (material == null) {
			return null;
		}
		ItemStack itemStack = new ItemStack(material, amount);

		for (Map.Entry<Enchantment, Integer> entry : enchantmentIntegerMap.entrySet()) {
			itemStack.addUnsafeEnchantment(entry.getKey(), entry.getValue());
		}
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (itemMeta != null) {
			if (itemMeta instanceof Damageable) {
				((Damageable)itemMeta).setDamage(durability);
			}

			if (itemMeta instanceof BannerMeta) {
				((BannerMeta) itemMeta).setPatterns(this.patterns);
			} else if (itemMeta instanceof BookMeta) {
				((BookMeta) itemMeta).setAuthor(author);
				((BookMeta) itemMeta).setPages(pages);
				((BookMeta) itemMeta).setTitle(displayName);
			} else if (itemMeta instanceof SkullMeta && owner != null) {
				((SkullMeta) itemMeta).setOwningPlayer(owner);
			} else if (itemMeta instanceof PotionMeta) {
				if (potionData != null) ((PotionMeta) itemMeta).setBasePotionData(potionData);
				((PotionMeta) itemMeta).clearCustomEffects();
				if (color != null) ((PotionMeta) itemMeta).setColor(color);
				else if (potionEffects.size() > 0) ((PotionMeta) itemMeta).setColor(/*NMSAPI.effectColors.get(potionEffects.iterator().next().getType())*/potionEffects.iterator().next().getType().getColor());

				for (PotionEffect potionEffect : potionEffects) {
					((PotionMeta) itemMeta).addCustomEffect(potionEffect, true);
				}
			} else if (itemMeta instanceof EnchantmentStorageMeta) {
				for (Map.Entry<Enchantment, Integer> entry : enchantmentIntegerMap.entrySet()) {
					((EnchantmentStorageMeta) itemMeta)
					.addStoredEnchant(entry.getKey(), entry.getValue(), true);
				}
			} else if (itemMeta instanceof FireworkEffectMeta) {
				((FireworkEffectMeta) itemMeta).setEffect(fireworkEffect);
			} else if (itemMeta instanceof LeatherArmorMeta) {
				((LeatherArmorMeta) itemMeta).setColor(color);
			} else if (itemMeta instanceof MapMeta) {
				((MapMeta) itemMeta).setScaling(scaling);
				if (mapView != null) ((MapMeta) itemMeta).setMapView(mapView);
				if (locationName != null) ((MapMeta) itemMeta).setLocationName(locationName);
				if (color != null) ((MapMeta) itemMeta).setColor(color);
			}

			itemMeta.setDisplayName(displayName);
			itemMeta.setLore(lore);
			itemMeta.addItemFlags(this.itemFlags.toArray(new ItemFlag[]{}));
			itemMeta.setUnbreakable(unbreakable);
			itemMeta.setCustomModelData(modelData);

			try {
				itemStack.setItemMeta(itemMeta);
			} catch (IllegalArgumentException e) {
				if (e.getMessage().equalsIgnoreCase("Name and ID cannot both be blank")) {
					System.out.println("Error: UUID "+(owner == null ? "null" : owner.getUniqueId().toString())+" cannot be linked to a mojang profile.");
					((SkullMeta)itemMeta).setOwningPlayer(null);
					itemStack.setItemMeta(itemMeta);
				}
			}
		} else {
			System.out.println("Could not get ItemMeta of item "+(itemStack == null ? "null" : itemStack.getType().name()));
		}
		return itemStack;
	}

	public ItemStackBuilder withPatterns(List<Pattern> patterns) {
		this.patterns = patterns;
		return this;
	}

	public ItemStackBuilder addPattern(Pattern pattern) {
		this.patterns.add(pattern);
		return this;
	}

	public ItemStackBuilder addPatterns(Pattern... patterns) {
		for (Pattern p : patterns) this.patterns.add(p);
		return this;
	}

}