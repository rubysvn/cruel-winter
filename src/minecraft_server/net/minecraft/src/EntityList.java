package net.minecraft.src;

import java.util.HashMap;
import java.util.Map;

public class EntityList {
	private static Map stringToClassMapping = new HashMap();
	private static Map classToIDMapping = new HashMap();

	private static void addMapping(Class clazz, String entityName) {
		stringToClassMapping.put(entityName, clazz);
		classToIDMapping.put(clazz, entityName);
	}

	public static Entity createEntityByName(String entityName, World world) {
		Entity entity2 = null;

		try {
			Class class3 = (Class)stringToClassMapping.get(entityName);
			if(class3 != null) {
				entity2 = (Entity)class3.getConstructor(new Class[]{World.class}).newInstance(new Object[]{world});
			}
		} catch (Exception exception4) {
			exception4.printStackTrace();
		}

		return entity2;
	}

	public static Entity createEntityFromNBT(NBTTagCompound nbttagcompound, World world) {
		Entity entity2 = null;

		try {
			Class class3 = (Class)stringToClassMapping.get(nbttagcompound.getString("id"));
			if(class3 != null) {
				entity2 = (Entity)class3.getConstructor(new Class[]{World.class}).newInstance(new Object[]{world});
			}
		} catch (Exception exception4) {
			exception4.printStackTrace();
		}

		if(entity2 != null) {
			entity2.readFromNBT(nbttagcompound);
		} else {
			System.out.println("Skipping Entity with id " + nbttagcompound.getString("id"));
		}

		return entity2;
	}

	public static String getEntityString(Entity entity) {
		return (String)classToIDMapping.get(entity.getClass());
	}

	static {
		addMapping(EntityArrow.class, "Arrow");
		addMapping(EntitySnowball.class, "Snowball");
		addMapping(EntityItem.class, "Item");
		addMapping(EntityPainting.class, "Painting");
		addMapping(EntityLiving.class, "Mob");
		addMapping(EntityMobs.class, "Monster");
		addMapping(EntityCreeper.class, "Creeper");
		addMapping(EntitySkeleton.class, "Skeleton");
		addMapping(EntitySpider.class, "Spider");
		addMapping(EntityGiantZombie.class, "Giant");
		addMapping(EntityZombie.class, "Zombie");
		addMapping(EntitySlime.class, "Slime");
		addMapping(EntityPig.class, "Pig");
		addMapping(EntitySheep.class, "Sheep");
		addMapping(EntityCow.class, "Cow");
		addMapping(EntityChicken.class, "Chicken");
		addMapping(EntityTNTPrimed.class, "PrimedTnt");
		addMapping(EntityFallingSand.class, "FallingSand");
		addMapping(EntityMinecart.class, "Minecart");
		addMapping(EntityBoat.class, "Boat");
	}
}
