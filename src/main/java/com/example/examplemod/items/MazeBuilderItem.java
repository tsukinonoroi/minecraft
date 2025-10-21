package com.example.examplemod.items;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;

import java.util.Random;

public class MazeBuilderItem extends Item
{
    public MazeBuilderItem(Properties properties)
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
            

            BlockPos buildPos = clickedPos.offset(0, 1, -1);
            
            buildMaze(level, buildPos);
            
            player.displayClientMessage(Component.literal("Лабиринт построен!"), true);
            

            ItemStack itemStack = player.getItemInHand(hand);
            if (!player.isCreative())
            {
                itemStack.shrink(1);
            }
        }
        
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
    
    private void buildMaze(Level level, BlockPos pos)
    {
        int size = 11;
        int height = 3;
        Random random = new Random();

        boolean[][] maze = new boolean[size][size];

        for (int x = 0; x < size; x++)
        {
            for (int z = 0; z < size; z++)
            {
                maze[x][z] = true;
            }
        }

        generateMaze(maze, 1, 1, random);

        maze[0][1] = false;
        maze[size-1][size-2] = false;
        

        for (int x = 0; x < size; x++)
        {
            for (int z = 0; z < size; z++)
            {
                level.setBlock(pos.offset(x, -1, z), Blocks.STONE.defaultBlockState(), 3);
            }
        }

        for (int x = 0; x < size; x++)
        {
            for (int z = 0; z < size; z++)
            {
                if (maze[x][z])
                {
                    for (int y = 0; y < height; y++)
                    {
                        level.setBlock(pos.offset(x, y, z), Blocks.STONE_BRICKS.defaultBlockState(), 3);
                    }
                }
                else
                {

                    if (random.nextInt(5) == 0)
                    {
                        level.setBlock(pos.offset(x, 0, z), Blocks.TORCH.defaultBlockState(), 3);
                    }
                }
            }
        }

        int center = size / 2;
        BlockPos chestPos = pos.offset(center, 0, center);
        level.setBlock(chestPos, Blocks.CHEST.defaultBlockState(), 3);

        if (level.getBlockEntity(chestPos) instanceof ChestBlockEntity chest)
        {
            chest.setItem(13, new ItemStack(Items.DIAMOND_BLOCK, 1));
        }

        level.setBlock(pos.offset(0, 0, 1), Blocks.GLOWSTONE.defaultBlockState(), 3);
        level.setBlock(pos.offset(size-1, 0, size-2), Blocks.GLOWSTONE.defaultBlockState(), 3);
    }
    
    private void generateMaze(boolean[][] maze, int x, int z, Random random)
    {
        maze[x][z] = false;

        int[][] directions = {{0, -2}, {2, 0}, {0, 2}, {-2, 0}};

        shuffleArray(directions, random);
        
        for (int[] dir : directions)
        {
            int nx = x + dir[0];
            int nz = z + dir[1];

            if (nx > 0 && nx < maze.length - 1 && nz > 0 && nz < maze[0].length - 1)
            {
                if (maze[nx][nz])
                {
                    maze[x + dir[0] / 2][z + dir[1] / 2] = false;

                    generateMaze(maze, nx, nz, random);
                }
            }
        }
    }
    
    private void shuffleArray(int[][] array, Random random)
    {
        for (int i = array.length - 1; i > 0; i--)
        {
            int index = random.nextInt(i + 1);
            int[] temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }
}

