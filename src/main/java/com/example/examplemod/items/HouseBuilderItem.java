package com.example.examplemod.items;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class HouseBuilderItem extends Item
{
    public HouseBuilderItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        if (!level.isClientSide())
        {

            var hitResult = player.pick(20.0D, 0.0F, false);
            BlockPos clickedPos = new BlockPos((int)hitResult.getLocation().x, 
                                              (int)hitResult.getLocation().y, 
                                              (int)hitResult.getLocation().z);
            

            BlockPos buildPos = clickedPos.offset(-3, 1, 0);
            
            buildHouse(level, buildPos);
            
            player.displayClientMessage(Component.literal("Домик построен!"), true);

            ItemStack itemStack = player.getItemInHand(hand);
            if (!player.isCreative())
            {
                itemStack.shrink(1);
            }
        }
        
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
    
    private void buildHouse(Level level, BlockPos pos)
    {
        for (int x = 0; x < 7; x++)
        {
            for (int z = 0; z < 7; z++)
            {
                level.setBlock(pos.offset(x, -1, z), Blocks.OAK_PLANKS.defaultBlockState(), 3);
            }
        }

        for (int y = 0; y < 4; y++)
        {
            for (int x = 0; x < 7; x++)
            {
                for (int z = 0; z < 7; z++)
                {
                    if (x == 0 || x == 6 || z == 0 || z == 6)
                    {
                        BlockPos wallPos = pos.offset(x, y, z);

                        if (z == 0 && x == 3 && y < 2)
                        {
                            if (y == 0)
                            {
                                level.setBlock(wallPos, Blocks.OAK_DOOR.defaultBlockState(), 3);
                            }
                            else
                            {
                                level.setBlock(wallPos, Blocks.OAK_DOOR.defaultBlockState()
                                    .setValue(net.minecraft.world.level.block.DoorBlock.HALF, 
                                    net.minecraft.world.level.block.state.properties.DoubleBlockHalf.UPPER), 3);
                            }
                        }

                        else if (y >= 1 && y <= 2)
                        {

                            if ((x == 0 && z == 3) || (x == 6 && z == 3) || 
                                (z == 6 && x == 2) || (z == 6 && x == 4))
                            {
                                level.setBlock(wallPos, Blocks.GLASS.defaultBlockState(), 3);
                            }
                            else
                            {
                                level.setBlock(wallPos, Blocks.OAK_PLANKS.defaultBlockState(), 3);
                            }
                        }
                        else
                        {
                            level.setBlock(wallPos, Blocks.OAK_PLANKS.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }

        for (int roofLevel = 0; roofLevel < 4; roofLevel++)
        {
            int y = 4 + roofLevel;
            int inset = roofLevel;
            
            for (int x = inset; x < 7 - inset; x++)
            {
                for (int z = inset; z < 7 - inset; z++)
                {

                    if (x == inset || x == 6 - inset || z == inset || z == 6 - inset)
                    {
                        level.setBlock(pos.offset(x, y, z), Blocks.BRICK_STAIRS.defaultBlockState(), 3);
                    }
                }
            }

            if (roofLevel == 3)
            {
                level.setBlock(pos.offset(3, y, 3), Blocks.BRICKS.defaultBlockState(), 3);
            }
        }

        level.setBlock(pos.offset(1, 1, 1), Blocks.TORCH.defaultBlockState(), 3);
        level.setBlock(pos.offset(5, 1, 1), Blocks.TORCH.defaultBlockState(), 3);
        level.setBlock(pos.offset(1, 1, 5), Blocks.TORCH.defaultBlockState(), 3);
        level.setBlock(pos.offset(5, 1, 5), Blocks.TORCH.defaultBlockState(), 3);
    }
}

