package com.minecraftabnormals.upgrade_aquatic.core;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import org.apache.commons.lang3.tuple.Pair;

public class UAConfig {

	public static class Common {
		public final ConfigValue<Integer> deepOceanMobMaxHeight;

		public final ConfigValue<Integer> glowSquidWeight;
		public final ConfigValue<Integer> thrasherWeight;
		public final ConfigValue<Integer> nautilusWeight;
		public final ConfigValue<Integer> lionfishWeight;
		public final ConfigValue<Integer> pikeWeight;
		public final ConfigValue<Integer> pikeSwampWeight;
		public final ConfigValue<Integer> squidSwampWeight;
		public final ConfigValue<Integer> salmonSwampWeight;

		public final ConfigValue<Integer> beachgrassFrequency;
		public final ConfigValue<Integer> searocketFrequency;
		public final ConfigValue<Integer> pickerelweedFrequency;
		public final ConfigValue<Integer> pickerelweedExtraFrequency;
		public final ConfigValue<Integer> floweringRushFrequency;

		public Common(ForgeConfigSpec.Builder builder) {
			builder.comment("Upgrade Aquatic common configuration").push("common");
			builder.push("mobs");

			this.deepOceanMobMaxHeight = builder.comment("The max height that deep ocean mobs can spawn at").defineInRange("Deep ocean mob max height", 30, 0, 255);

			builder.push("spawns");

			this.glowSquidWeight = builder.define("Glow Squid spawn weight (oceans)", 67);
			this.thrasherWeight = builder.define("Thrasher spawn weight (cold oceans)", 90);
			this.nautilusWeight = builder.define("Nautilus spawn weight (warm oceans)", 51);
			this.lionfishWeight = builder.define("Lionfish spawn weight (coral reefs)", 15);
			this.pikeWeight = builder.define("Pike spawn weight (rivers)", 11);

			this.pikeSwampWeight = builder.define("Pike spawn weight (swamps)", 5);
			this.squidSwampWeight = builder.define("Squid spawn weight (swamps)", 5);
			this.salmonSwampWeight = builder.define("Salmon spawn weight (swamps)", 5);

			builder.pop();
			builder.pop();
			builder.push("world");
			builder.push("generation");

			this.beachgrassFrequency = builder.define("Beachgrass frequency (beaches)", 18);
			this.searocketFrequency = builder.define("Searocket frequency (beaches)", 15);
			this.pickerelweedFrequency = builder.define("Pickerelweed frequency (jungles, swamps, rivers)", 28);
			this.pickerelweedExtraFrequency = builder.define("Pickerelweed frequency (flower forests)", 90);
			this.floweringRushFrequency = builder.define("Flowering Rush frequency (rivers)", 15);

			builder.pop();
			builder.pop();
			builder.pop();
		}
	}

	public static class Client {
		public final ConfigValue<Integer> daysTillRenderInsomniaOverlay;

		public Client(ForgeConfigSpec.Builder builder) {
			builder.comment("Upgrade Aquatic client configuration").push("client");
			builder.push("misc");

			this.daysTillRenderInsomniaOverlay = builder
					.comment("The amount of days till the insomnia overlay is rendered", "Setting to 3 will make the overlay indicate phantom spawns", "Setting to 0 will disable the overlay")
					.define("daysTillRenderInsomniaOverlay", 0);

			builder.pop();
			builder.pop();
		}
	}

	public static final ForgeConfigSpec COMMON_SPEC;
	public static final Common COMMON;

	public static final ForgeConfigSpec CLIENT_SPEC;
	public static final Client CLIENT;

	static {
		Pair<Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON_SPEC = commonSpecPair.getRight();
		COMMON = commonSpecPair.getLeft();

		Pair<Client, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(Client::new);
		CLIENT_SPEC = clientSpecPair.getRight();
		CLIENT = clientSpecPair.getLeft();
	}
}