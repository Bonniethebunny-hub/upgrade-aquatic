package com.teamabnormals.upgrade_aquatic.core;

import com.teamabnormals.blueprint.core.util.registry.RegistryHelper;
import com.teamabnormals.upgrade_aquatic.client.GlowSquidSpriteUploader;
import com.teamabnormals.upgrade_aquatic.client.model.*;
import com.teamabnormals.upgrade_aquatic.client.model.jellyfish.BoxJellyfishModel;
import com.teamabnormals.upgrade_aquatic.client.model.jellyfish.CassiopeaJellyfishModel;
import com.teamabnormals.upgrade_aquatic.client.model.jellyfish.ImmortalJellyfishModel;
import com.teamabnormals.upgrade_aquatic.client.renderer.entity.*;
import com.teamabnormals.upgrade_aquatic.client.renderer.entity.jellyfish.BoxJellyfishRenderer;
import com.teamabnormals.upgrade_aquatic.client.renderer.entity.jellyfish.CassiopeaJellyfishRenderer;
import com.teamabnormals.upgrade_aquatic.client.renderer.entity.jellyfish.ImmortalJellyfishRenderer;
import com.teamabnormals.upgrade_aquatic.common.network.RotateJellyfishMessage;
import com.teamabnormals.upgrade_aquatic.core.data.server.UAAdvancementModifierProvider;
import com.teamabnormals.upgrade_aquatic.core.data.server.UALootModifierProvider;
import com.teamabnormals.upgrade_aquatic.core.data.server.tags.UABlockTagsProvider;
import com.teamabnormals.upgrade_aquatic.core.other.UAClientCompat;
import com.teamabnormals.upgrade_aquatic.core.other.UACompat;
import com.teamabnormals.upgrade_aquatic.core.other.UADataSerializers;
import com.teamabnormals.upgrade_aquatic.core.other.UADispenseBehaviorRegistry;
import com.teamabnormals.upgrade_aquatic.core.other.UASpawns;
import com.teamabnormals.upgrade_aquatic.core.registry.UAEntityTypes;
import com.teamabnormals.upgrade_aquatic.core.registry.UAFeatures;
import com.teamabnormals.upgrade_aquatic.core.registry.UAItems;
import com.teamabnormals.upgrade_aquatic.core.registry.UAMobEffects;
import com.teamabnormals.upgrade_aquatic.core.registry.UAParticleTypes;
import com.teamabnormals.upgrade_aquatic.core.registry.util.UAItemSubRegistryHelper;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(value = UpgradeAquatic.MOD_ID)
public class UpgradeAquatic {
	public static final String NETWORK_PROTOCOL = "1";
	public static final String MOD_ID = "upgrade_aquatic";
	public static final RegistryHelper REGISTRY_HELPER = RegistryHelper.create(MOD_ID, helper -> helper.putSubHelper(ForgeRegistries.ITEMS, new UAItemSubRegistryHelper(helper)));

	public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(UpgradeAquatic.MOD_ID, "net"))
			.networkProtocolVersion(() -> NETWORK_PROTOCOL)
			.clientAcceptedVersions(NETWORK_PROTOCOL::equals)
			.serverAcceptedVersions(NETWORK_PROTOCOL::equals)
			.simpleChannel();

	public UpgradeAquatic() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		ModLoadingContext context = ModLoadingContext.get();

		this.setupMessages();

		REGISTRY_HELPER.register(bus);
		UAMobEffects.MOB_EFFECTS.register(bus);
		UAMobEffects.POTIONS.register(bus);
		UAFeatures.FEATURES.register(bus);
		UAParticleTypes.PARTICLES.register(bus);
		UADataSerializers.SERIALIZERS.register(bus);
		MinecraftForge.EVENT_BUS.register(this);

		bus.addListener(this::commonSetup);
		bus.addListener(this::dataSetup);
		bus.addListener(this::clientSetup);

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			GlowSquidSpriteUploader.init(bus);
			bus.addListener(this::registerLayerDefinitions);
			bus.addListener(this::registerRenderers);
		});

		context.registerConfig(ModConfig.Type.COMMON, UAConfig.COMMON_SPEC);
		context.registerConfig(ModConfig.Type.CLIENT, UAConfig.CLIENT_SPEC);
	}

	private void commonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			UACompat.registerCompat();
			UASpawns.registerSpawns();
			UAMobEffects.registerBrewingRecipes();
			UADispenseBehaviorRegistry.registerDispenseBehaviors();
			ObfuscationReflectionHelper.setPrivateValue(BlockBehaviour.class, Blocks.BUBBLE_COLUMN, true, "f_60445_");
		});
	}

	private void dataSetup(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		if (event.includeServer()) {
			generator.addProvider(new UALootModifierProvider(generator));
			generator.addProvider(new UAAdvancementModifierProvider(generator));
			generator.addProvider(new UABlockTagsProvider(generator, event.getExistingFileHelper()));
		}
	}

	private void clientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			UAItems.registerItemProperties();
			UAClientCompat.registerClientCompat();
		});
	}

	private void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(NautilusModel.LOCATION, NautilusModel::createBodyLayer);
		event.registerLayerDefinition(PikeModel.LOCATION, PikeModel::createBodyLayer);
		event.registerLayerDefinition(LionfishModel.LOCATION, LionfishModel::createBodyLayer);
		event.registerLayerDefinition(PerchModel.LOCATION, PerchModel::createBodyLayer);
		event.registerLayerDefinition(ThrasherModel.LOCATION, ThrasherModel::createBodyLayer);
		event.registerLayerDefinition(FlareModel.LOCATION, FlareModel::createBodyLayer);
		event.registerLayerDefinition(SonarWaveModel.LOCATION, SonarWaveModel::createBodyLayer);
		event.registerLayerDefinition(UluluModel.LOCATION, UluluModel::createBodyLayer);
		event.registerLayerDefinition(UAGlowSquidModel.LOCATION, UAGlowSquidModel::createBodyLayer);
		event.registerLayerDefinition(GooseModel.LOCATION, GooseModel::createBodyLayer);
		event.registerLayerDefinition(BoxJellyfishModel.LOCATION, BoxJellyfishModel::createBodyLayer);
		event.registerLayerDefinition(CassiopeaJellyfishModel.LOCATION, CassiopeaJellyfishModel::createBodyLayer);
		event.registerLayerDefinition(ImmortalJellyfishModel.LOCATION, ImmortalJellyfishModel::createBodyLayer);
	}

	private void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(UAEntityTypes.NAUTILUS.get(), NautilusRenderer::new);
		event.registerEntityRenderer(UAEntityTypes.PIKE.get(), PikeRenderer::new);
		event.registerEntityRenderer(UAEntityTypes.LIONFISH.get(), LionfishRenderer::new);
		event.registerEntityRenderer(UAEntityTypes.PERCH.get(), PerchRenderer::new);
		event.registerEntityRenderer(UAEntityTypes.THRASHER.get(), ThrasherRenderer::new);
		event.registerEntityRenderer(UAEntityTypes.GREAT_THRASHER.get(), GreatThrasherRenderer::new);
		event.registerEntityRenderer(UAEntityTypes.FLARE.get(), FlareRenderer::new);
		event.registerEntityRenderer(UAEntityTypes.SONAR_WAVE.get(), SonarWaveRenderer::new);
		event.registerEntityRenderer(UAEntityTypes.ULULU.get(), UluluRenderer::new);
		event.registerEntityRenderer(UAEntityTypes.GOOSE.get(), GooseRenderer::new);
		event.registerEntityRenderer(UAEntityTypes.BOX_JELLYFISH.get(), BoxJellyfishRenderer::new);
		event.registerEntityRenderer(UAEntityTypes.CASSIOPEA_JELLYFISH.get(), CassiopeaJellyfishRenderer::new);
		event.registerEntityRenderer(UAEntityTypes.IMMORTAL_JELLYFISH.get(), ImmortalJellyfishRenderer::new);

		if (UAConfig.CLIENT.replaceGlowSquidRenderer.get()) event.registerEntityRenderer(EntityType.GLOW_SQUID, UAGlowSquidRenderer::new);
	}

	void setupMessages() {
		int id = -1;

		CHANNEL.messageBuilder(RotateJellyfishMessage.class, id++)
				.encoder(RotateJellyfishMessage::serialize).decoder(RotateJellyfishMessage::deserialize)
				.consumer(RotateJellyfishMessage::handle)
				.add();
	}
}