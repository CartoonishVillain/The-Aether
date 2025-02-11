package com.gildedgames.aether.common.block.dungeon;

import com.gildedgames.aether.common.entity.tile.TreasureChestTileEntity;
import com.gildedgames.aether.common.registry.AetherTileEntityTypes;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.piglin.PiglinTasks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.*;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

public class TreasureChestBlock extends AbstractChestBlock<TreasureChestTileEntity> implements IWaterLoggable
{
	public static final DirectionProperty FACING = HorizontalBlock.FACING;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	protected static final VoxelShape AABB = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);

	public TreasureChestBlock(AbstractBlock.Properties properties, Supplier<TileEntityType<? extends TreasureChestTileEntity>> tileEntityTypeSupplier) {
		super(properties, tileEntityTypeSupplier);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, Boolean.FALSE));
	}

	public TreasureChestBlock(AbstractBlock.Properties properties) {
		this(properties, AetherTileEntityTypes.TREASURE_CHEST::get);
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState stateOther, IWorld world, BlockPos pos, BlockPos posOther) {
		if (state.getValue(WATERLOGGED)) {
			world.getLiquidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}

		return super.updateShape(state, direction, stateOther, world, pos, posOther);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
		return AABB;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		Direction direction = context.getHorizontalDirection().getOpposite();
		FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());

		return this.defaultBlockState().setValue(FACING, direction).setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity livingEntity, ItemStack stack) {
		if (stack.hasCustomHoverName()) {
			TileEntity tileentity = world.getBlockEntity(pos);
			if (tileentity instanceof TreasureChestTileEntity) {
				((TreasureChestTileEntity) tileentity).setCustomName(stack.getHoverName());
			}
		}
	}

	@Override
	public float getDestroyProgress(BlockState state, PlayerEntity player, IBlockReader world, BlockPos pos) {
		TileEntity tileEntity = world.getBlockEntity(pos);
		if (tileEntity instanceof TreasureChestTileEntity) {
			TreasureChestTileEntity treasureChest = (TreasureChestTileEntity) tileEntity;
			float f = treasureChest.getLocked() ? state.getDestroySpeed(world, pos) : 3.0F;
			if (f == -1.0F) {
				return 0.0F;
			} else {
				int i = net.minecraftforge.common.ForgeHooks.canHarvestBlock(state, player, world, pos) ? 30 : 100;
				return player.getDigSpeed(state, pos) / f / (float) i;
			}
		}
		return super.getDestroyProgress(state, player, world, pos);
	}

	@Override
	public float getExplosionResistance(BlockState state, IBlockReader world, BlockPos pos, Explosion explosion) {
		TileEntity tileEntity = world.getBlockEntity(pos);
		if (tileEntity instanceof TreasureChestTileEntity) {
			TreasureChestTileEntity treasureChest = (TreasureChestTileEntity) tileEntity;
			return treasureChest.getLocked() ? super.getExplosionResistance(state, world, pos, explosion) : 3.0F;
		}
		return super.getExplosionResistance(state, world, pos, explosion);
	}

	@Override
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState stateOther, boolean flag) {
		if (!state.is(stateOther.getBlock())) {
			TileEntity tileentity = world.getBlockEntity(pos);
			if (tileentity instanceof IInventory) {
				InventoryHelper.dropContents(world, pos, (IInventory)tileentity);
				world.updateNeighbourForOutputSignal(pos, this);
			}
			super.onRemove(state, world, pos, stateOther, flag);
		}
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
		if (world.isClientSide) {
			return ActionResultType.SUCCESS;
		} else {
			TileEntity tileEntity = world.getBlockEntity(pos);
			if (tileEntity instanceof TreasureChestTileEntity) {
				TreasureChestTileEntity treasureChest = (TreasureChestTileEntity) tileEntity;
				INamedContainerProvider inamedcontainerprovider = this.getMenuProvider(state, world, pos);
				if (treasureChest.getLocked()) {
					ItemStack stack = player.getMainHandItem();
					if (treasureChest.tryUnlock(player)) {
						if (player instanceof ServerPlayerEntity) {
							ServerPlayerEntity serverplayerentity = (ServerPlayerEntity) player;
							CriteriaTriggers.CONSUME_ITEM.trigger(serverplayerentity, stack);
							player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
						}
						stack.shrink(1);
					}
				} else if (inamedcontainerprovider != null) {
					player.openMenu(inamedcontainerprovider);
					player.awardStat(this.getOpenChestStat());
					PiglinTasks.angerNearbyPiglins(player, true);
				}
			}
			return ActionResultType.CONSUME;
		}
	}

	@Override
	public ItemStack getCloneItemStack(IBlockReader reader, BlockPos pos, BlockState state) {
		ItemStack stack = super.getCloneItemStack(reader, pos, state);
		TreasureChestTileEntity treasureChestTileEntity = (TreasureChestTileEntity) reader.getBlockEntity(pos);
		CompoundNBT compound = new CompoundNBT();
		compound.putBoolean("Locked", treasureChestTileEntity.getLocked());
		compound.putString("Kind", treasureChestTileEntity.getKind());
		stack.addTagElement("BlockEntityTag", compound);
		return stack;
	}

	protected Stat<ResourceLocation> getOpenChestStat() {
		return Stats.CUSTOM.get(Stats.OPEN_CHEST);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public TileEntityMerger.ICallbackWrapper<? extends ChestTileEntity> combine(BlockState state, World world, BlockPos pos, boolean flag) {
		return TileEntityMerger.ICallback::acceptNone;
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader p_196283_1_) {
		return new TreasureChestTileEntity();
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState p_149740_1_) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState p_180641_1_, World p_180641_2_, BlockPos p_180641_3_) {
		return Container.getRedstoneSignalFromBlockEntity(p_180641_2_.getBlockEntity(p_180641_3_));
	}

	@Override
	public BlockState rotate(BlockState p_185499_1_, Rotation p_185499_2_) {
		return p_185499_1_.setValue(FACING, p_185499_2_.rotate(p_185499_1_.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState p_185471_1_, Mirror p_185471_2_) {
		return p_185471_1_.rotate(p_185471_2_.getRotation(p_185471_1_.getValue(FACING)));
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
		p_206840_1_.add(FACING, WATERLOGGED);
	}

	@Override
	public boolean isPathfindable(BlockState p_196266_1_, IBlockReader p_196266_2_, BlockPos p_196266_3_, PathType p_196266_4_) {
		return false;
	}
}
