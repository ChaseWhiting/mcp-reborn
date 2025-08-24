package net.minecraft.entity.item;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;
import java.util.List;

public class PaintingType {


   private static final String KRISTOFFER = "Kristoffer Zetterstrand";
   public static final PaintingType KEBAB = registerKristoffer("Kebab med tre pepperoni","kebab", 16, 16, 2001);
   public static final PaintingType AZTEC = registerKristoffer("de_aztec","aztec", 16, 16, 2002);
   public static final PaintingType ALBAN = registerKristoffer("Albanian","alban", 16, 16, 2002);
   public static final PaintingType AZTEC2 = registerKristoffer("de_aztec", "aztec2", 16, 16, 2002);
   public static final PaintingType BOMB = registerKristoffer("Target Successfully Bombed","bomb", 16, 16, 2002);
   public static final PaintingType PLANT = registerKristoffer("Paradisträd", "plant", 16, 16, 1997);
   public static final PaintingType WASTELAND = registerKristoffer("Wasteland", "wasteland", 16, 16, 2005);
   public static final PaintingType POOL = registerKristoffer("The Pool", "pool", 32, 16, 2008);
   public static final PaintingType COURBET = registerKristoffer("Bonjour Monsieur Courbet","courbet", 32, 16, 2003);
   public static final PaintingType SEA = registerKristoffer("Seaside","sea", 32, 16, 2005);
   public static final PaintingType SUNSET = registerKristoffer("sunset_dense","sunset", 32, 16, 2003);
   public static final PaintingType CREEBET = registerKristoffer("Creebet","creebet", 32, 16, 2005);
   public static final PaintingType WANDERER = registerKristoffer("Wanderer","wanderer", 16, 32, 2008);
   public static final PaintingType GRAHAM = registerKristoffer("Graham","graham", 16, 32, 2003);
   public static final PaintingType MATCH = registerKristoffer("Match","match", 32, 32, 2009);
   public static final PaintingType BUST = registerKristoffer("Bust","bust", 32, 32, 2009);
   public static final PaintingType STAGE = registerKristoffer("The Stage Is Set","stage", 32, 32, 2006);
   public static final PaintingType VOID = registerKristoffer("The void","void", 32, 32, 2009);
   public static final PaintingType SKULL_AND_ROSES = registerKristoffer("Skull and Roses","skull_and_roses", 32, 32, 2009);
   public static final PaintingType WITHER = register("Wither","wither", 32, 32, "Jens Bergensten", null);
   public static final PaintingType FIGHTERS = registerKristoffer("Fighters","fighters", 64, 32, 2007);
   public static final PaintingType POINTER = registerKristoffer("Pointer","pointer", 64, 64, 2008);
   public static final PaintingType PIGSCENE = registerKristoffer("Pigscene","pigscene", 64, 64, 2008);
   public static final PaintingType BURNING_SKULL = registerKristoffer("Skull On Fire","burning_skull", 64, 64, 2022);
   public static final PaintingType SKELETON = registerKristoffer("Mortal Coil","skeleton", 64, 48, 2003);
   public static final PaintingType DONKEY_KONG = registerKristoffer("Kong", "donkey_kong", 64, 48, 2003);

   public static final PaintingType COTAN = registerKristoffer("Cotán", "cotan", 48, 48, 2022);
   public static final PaintingType MEDITATIVE = register("Meditative", "meditative", 16, 16, "Sarah Boeving", null);
   public static final PaintingType BACKYARD = registerKristoffer("Backyard", "backyard", 48, 64, 2022);
   public static final PaintingType PRAIRE_RIDE = register("Prairie Ride","prairie_ride", 16, 32, "Sarah Boeving", null);
   public static final PaintingType UNPACKED = register("Unpacked","unpacked", 64, 64, "Sarah Boeving", null);
   public static final PaintingType BAROQUE = register("Baroque","baroque", 32, 32, "Sarah Boeving", null);
   public static final PaintingType HUMBLE = register("Humble","humble", 32, 32, "Sarah Boeving", null);
   public static final PaintingType BOUQUET = registerKristoffer("Bouquet","bouquet", 48, 48, null);
   public static final PaintingType CAVEBIRD = registerKristoffer("Cavebird","cavebird", 48, 48, null);
   public static final PaintingType CHANGING = registerKristoffer("Changing","changing", 64, 32, 2008);
   public static final PaintingType PASSAGE = registerKristoffer("Passage","passage", 64, 32, 2010);
   public static final PaintingType POND = registerKristoffer("Pond","pond", 48, 64, 2018);
   public static final PaintingType SUNFLOWERS = registerKristoffer("Sunflowers", "sunflowers", 48, 48, null);
   public static final PaintingType ORB = registerKristoffer("Orb","orb", 64, 64, 2022);
   public static final PaintingType LOWMIST = registerKristoffer("Lowmist","lowmist", 64, 32, 2003);
   public static final PaintingType FINDING = registerKristoffer("Finding", "finding", 64, 32, 2015-2016);
   public static final PaintingType FERN = registerKristoffer("Fern","fern", 48, 48, null);
   public static final PaintingType OWLEMONS = registerKristoffer("Owlemons","owlemons", 48, 48, 2023);
   public static final PaintingType ENDBOSS = registerKristoffer("Endboss", "endboss", 48, 48, 2006);

   public static final PaintingType TIDES = registerKristoffer("Tides","tides", 48, 48, 2018);
   public static final PaintingType EARTH = register("Earth","earth", 32, 32, "Mojang", null);
   public static final PaintingType FIRE = register("Fire","fire", 32, 32, "Mojang", null);
   public static final PaintingType WIND = register("Wind","wind", 32, 32, "Mojang", null);
   public static final PaintingType WATER = register("Water","water", 32, 32, "Mojang", null);

   public static ItemStack withVariant(PaintingType motive) {
      ItemStack painting = new ItemStack(Items.PAINTING);
      CompoundNBT entityTag = new CompoundNBT();
      entityTag.putString("Motive", motive.getName());
      painting.getOrCreateTag().put("EntityTag", entityTag);
      return painting;
   }

   public static ItemStack withNoVariant() {
       return new ItemStack(Items.PAINTING);
   }

   public static final List<PaintingType> IN_ORDER = List.of(ALBAN, AZTEC, AZTEC2,
           BOMB,
           KEBAB,
           MEDITATIVE,
           PLANT,
           WASTELAND,
           GRAHAM,
           PRAIRE_RIDE,
           WANDERER,
           COURBET,
           CREEBET,
           POOL,
           SEA,
           SUNSET,
           BAROQUE,
           BUST,
           HUMBLE,
           MATCH,
           SKULL_AND_ROSES,
           STAGE,
           VOID,
           WITHER,
           CHANGING,
           FIGHTERS,
           FINDING,
           LOWMIST,
           PASSAGE,
           BOUQUET,
           CAVEBIRD,
           COTAN,
           ENDBOSS,
           FERN,
           OWLEMONS,
           SUNFLOWERS,
           TIDES,
           BACKYARD,
           POND,
           DONKEY_KONG,
           SKELETON,
           BURNING_SKULL,
           ORB,
           PIGSCENE,
           POINTER,
           UNPACKED,
           EARTH,
           FIRE,
           WATER,
           WIND);



   private final int width;
   private final int height;
   private final String author;
   @Nullable
   private final Integer year;
   private final String title;
   private final String name;

   private static PaintingType register(String title, String name, int width, int height, String author, @Nullable Integer year) {
      return Registry.register(Registry.MOTIVE, name, new PaintingType(width, height, author, year, title, name));
   }

   private static PaintingType registerKristoffer(String title, String name, int width, int height, @Nullable Integer year) {
      return Registry.register(Registry.MOTIVE, name, new PaintingType(width, height, KRISTOFFER, year, title, name));
   }

   public PaintingType(int width, int height, String author, @Nullable Integer year, String title, String name) {
      this.width = width;
      this.height = height;
      this.author = author;
      this.year = year;
      this.title = title;
      this.name = name;
   }

   public String getName() {
      return name;
   }

   public String getTitle() {
      return title;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public String getAuthor() {
      return this.author;
   }

   @Nullable
   public Integer getYear() {
      return this.year;
   }

   public int getWidthInBlocks() {
      return switch (this.getWidth()) {
         default -> 1;
         case 32 -> 2;
         case 48 -> 3;
         case 64 -> 4;
      };
   }

   public int getHeightInBlocks() {
      return switch (this.getHeight()) {
         default -> 1;
         case 32 -> 2;
         case 48 -> 3;
         case 64 -> 4;
      };
   }
}