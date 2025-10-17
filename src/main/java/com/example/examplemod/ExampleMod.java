package com.example.examplemod;

import com.mojang.logging.LogUtils;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

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

    public ExampleMod(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        ITEMS.register(modEventBus);

        LOGGER.info("Привет! Мой простой мод загружен!");
    }
}
