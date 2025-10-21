package com.example.examplemod;

import com.example.examplemod.items.HouseBuilderItem;
import com.example.examplemod.items.MazeBuilderItem;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod(ExampleMod.MODID)
public class ExampleMod
{
    public static final String MODID = "examplemod";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static final RegistryObject<Item> SUPER_APPLE = ITEMS.register("super_apple",
        () -> new Item(new Item.Properties()
            .food(new FoodProperties.Builder()
                .nutrition(10)
                .saturationMod(1.0f)
                .alwaysEat()
                .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 600, 2), 1.0f)      // Регенерация III на 30 секунд
                .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1200, 2), 1.0f)   // Скорость III на 60 секунд
                .effect(() -> new MobEffectInstance(MobEffects.JUMP, 1200, 2), 1.0f)             // Прыгучесть III на 60 секунд
                .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 800, 1), 1.0f)      // Сила II на 40 секунд
                .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1200, 1), 1.0f) // Сопротивление II на 60 секунд
                .effect(() -> new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 1200, 0), 1.0f)  // Огнестойкость на 60 секунд
                .effect(() -> new MobEffectInstance(MobEffects.NIGHT_VISION, 1200, 0), 1.0f)     // Ночное зрение на 60 секунд
                .effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 600, 3), 1.0f)        // Поглощение IV на 30 секунд
                .effect(() -> new MobEffectInstance(MobEffects.SATURATION, 100, 0), 1.0f)        // Насыщение на 5 секунд
                .build())
            .rarity(Rarity.EPIC)
        ));

    public static final RegistryObject<Item> HOUSE_BUILDER = ITEMS.register("house_builder",
        () -> new HouseBuilderItem(new Item.Properties()
            .stacksTo(16)
            .rarity(Rarity.RARE)
        ));

    public static final RegistryObject<Item> MAZE_BUILDER = ITEMS.register("maze_builder",
        () -> new MazeBuilderItem(new Item.Properties()
            .stacksTo(16)
            .rarity(Rarity.RARE)
        ));

    public ExampleMod(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        ITEMS.register(modEventBus);

        LOGGER.info("Привет! Мой простой мод загружен!");
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents
    {
        private static final Map<UUID, Boolean> flyingHorses = new HashMap<>();

        private static boolean wasJumpPressed = false;
        private static long lastJumpTime = 0;
        private static final long DOUBLE_TAP_TIME = 300;

        @SubscribeEvent
        @OnlyIn(Dist.CLIENT)
        public static void onClientTick(TickEvent.ClientTickEvent event)
        {
            if (event.phase != TickEvent.Phase.END) return;
            
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || mc.level == null) return;

            Player player = mc.player;

            if (player.getVehicle() instanceof AbstractHorse horse)
            {
                UUID horseId = horse.getUUID();
                boolean isFlying = flyingHorses.getOrDefault(horseId, false);

                boolean jumpPressed = mc.options.keyJump.isDown();
                
                if (jumpPressed && !wasJumpPressed)
                {
                    long currentTime = System.currentTimeMillis();
                    
                    if (currentTime - lastJumpTime < DOUBLE_TAP_TIME)
                    {
                        isFlying = !isFlying;
                        flyingHorses.put(horseId, isFlying);

                        if (isFlying)
                        {
                            player.displayClientMessage(Component.literal("Режим полета активирован!"), true);
                        }
                        else
                        {
                            player.displayClientMessage(Component.literal("Режим полета отключен"), true);
                        }
                        
                        lastJumpTime = 0;
                    }
                    else
                    {
                        lastJumpTime = currentTime;
                    }
                }
                
                wasJumpPressed = jumpPressed;
                

                horse.fallDistance = 0;
                
                if (isFlying)
                {
                    horse.setNoGravity(true);
                    
                    Vec3 motion = horse.getDeltaMovement();
                    double motionY = motion.y;

                    if (mc.options.keyJump.isDown())
                    {
                        motionY = 0.5;
                    }
                    else
                    {
                        motionY = -0.15;
                    }
                    
                    horse.setDeltaMovement(motion.x, motionY, motion.z);
                }
                else
                {
                    horse.setNoGravity(false);

                    Vec3 motion = horse.getDeltaMovement();
                    if (motion.y < -0.5)
                    {
                        horse.setDeltaMovement(motion.x, -0.5, motion.z);
                    }
                }
            }
            else
            {
                if (player.getVehicle() == null)
                {
                    mc.level.getEntitiesOfClass(AbstractHorse.class, 
                        player.getBoundingBox().inflate(10.0), 
                        h -> h.getPassengers().isEmpty())
                        .forEach(h -> {
                            h.setNoGravity(false);
                            flyingHorses.remove(h.getUUID());
                        });
                }
                
                wasJumpPressed = false;
            }
        }
    }
}
