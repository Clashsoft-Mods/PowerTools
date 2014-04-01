package powertools.chunkprotection;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import powertools.chunkprotection.command.CommandChunk;
import powertools.chunkprotection.common.AutoUnclaimTickHandler;
import powertools.chunkprotection.common.ProtectEvents;
import powertools.chunkprotection.common.ProtectTickHandler;
import powertools.chunkprotection.lib.ChunkInfo;
import powertools.chunkprotection.status.ClaimStatus;
import powertools.chunkprotection.status.UnclaimStatus;
import powertools.main.PowerTools;
import powertools.main.lib.ChunkPos;
import powertools.main.lib.PlayerInfo;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = ChunkProtection.MODID, name = ChunkProtection.NAME, version = ChunkProtection.VERSION, dependencies = ChunkProtection.DEPENDENCIES)
@NetworkMod(clientSideRequired = false, serverSideRequired = false)
public class ChunkProtection
{
	public static final String				MODID			= "chunkprotection";
	public static final String				NAME			= "Chunk Protection";
	public static final String				VERSION			= "1.6.4-1.0.0";
	public static final String				DEPENDENCIES	= "required-after:" + PowerTools.MODID;
	
	public static File						file;
	
	public static Map<ChunkPos, ChunkInfo>	chunkInfo		= new HashMap();
	
	public static boolean					cfgDefaultBorderInfo, cfgAllPlayersCanClaim;
	public static int						cfgMaxChunksPerPlayer, cfgAutoUnclaimInDays;
	
	@Instance(MODID)
	public static ChunkProtection			instance;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		if (event.getSide() == Side.SERVER)
		{
			Configuration config = new Configuration(event.getSuggestedConfigurationFile());
			
			cfgAllPlayersCanClaim = config.get("players", "AllPlayersCanClaim", true, "true: All players are allowed to claim chunks. / false: Only op's are allowed to claim.").getBoolean(false);
			cfgAutoUnclaimInDays = config.get("players", "AutoUnclaimInDays", 7, "Chunks will automaticly be unclaimed if owner doesn't visit them for X days. 0=never unclaim").getInt();
			cfgDefaultBorderInfo = config.get("players", "DefaultBorderInfo", true, "This sets the default of displaying messages when crossing chunk borders. true / false").getBoolean(true);
			cfgMaxChunksPerPlayer = config.get("players", "MaxChunksPerPlayer", 4, "This defines how many chunks each player is allowed to claim.").getInt();
			
			config.save();
		}
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		if (event.getSide() == Side.SERVER)
		{
			MinecraftForge.EVENT_BUS.register(new ProtectEvents());
			
			TickRegistry.registerScheduledTickHandler(new ProtectTickHandler(), Side.SERVER);
			if (cfgAutoUnclaimInDays > 0)
			{
				TickRegistry.registerScheduledTickHandler(new AutoUnclaimTickHandler(), Side.SERVER);
			}
			
			PowerTools.chunkProtectionLoaded = true;
			
			file = new File(PowerTools.getSaveDir(), "chunkprotection.dat");
			this.loadChunkInfo(file);
			
			PowerTools.commandManager.registerCommand(new CommandChunk());
		}
	}
	
	@EventHandler
	public void serverStopping(FMLServerStoppingEvent event)
	{
		if (file != null)
		{
			this.savePlayerInfo(file);
		}
	}
	
	public void loadChunkInfo(File file)
	{
		try
		{
			NBTTagCompound nbt = CompressedStreamTools.read(file);
			
			if (nbt != null)
			{
				NBTTagList chunks = nbt.getTagList("Chunks");
				for (int i = 0; i < chunks.tagCount(); i++)
				{
					NBTTagCompound chunkNBT = (NBTTagCompound) chunks.tagAt(i);
					ChunkInfo info = new ChunkInfo();
					info.readFromNBT(chunkNBT);
					chunkInfo.put(info.chunkPos, info);
				}
			}
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void savePlayerInfo(File file)
	{
		try
		{
			PowerTools.createFile(file);
			
			NBTTagCompound nbt = new NBTTagCompound();
			NBTTagList chunks = new NBTTagList();
			
			for (ChunkPos chunkPos : chunkInfo.keySet())
			{
				NBTTagCompound chunkNBT = new NBTTagCompound();
				chunkInfo.get(chunkPos).writeToNBT(chunkNBT);
				chunks.appendTag(chunkNBT);
			}
			nbt.setTag("Chunks", chunks);
			
			CompressedStreamTools.write(nbt, file);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}
	
	public static ChunkInfo getChunkInfo(ChunkPos chunkPos)
	{
		ChunkInfo info = chunkInfo.get(chunkPos);
		if (info == null)
		{
			info = new ChunkInfo(chunkPos, "");
			chunkInfo.put(chunkPos, info);
		}
		return info;
	}
	
	public static ChunkInfo getChunkInfo(int dim, int x, int z)
	{
		return getChunkInfo(new ChunkPos(dim, x, z));
	}
	
	public static ClaimStatus claimChunk(ChunkPos chunkPos, String player)
	{
		ChunkInfo info = getChunkInfo(chunkPos);
		PlayerInfo pinfo = PowerTools.getPlayerInfo(player);
		ClaimStatus compare = info.compare(chunkPos, player);
		
		int maxChunks = pinfo.maxChunks;
		if (maxChunks < 0 || maxChunks >= cfgMaxChunksPerPlayer)
		{
			maxChunks = cfgMaxChunksPerPlayer;
			pinfo.maxChunks = maxChunks;
		}
		
		if (compare == ClaimStatus.NOT_CLAIMED)
		{
			if (pinfo.chunksOwned < maxChunks)
			{
				info.claim(pinfo);
				return ClaimStatus.CLAIMED;
			}
			else
			{
				return ClaimStatus.FULL;
			}
		}
		else if (compare == ClaimStatus.CLAIMED)
		{
			return ClaimStatus.ALREADY_CLAIMED;
		}
		else if (compare == ClaimStatus.CLAIMED_BY_OTHER_PLAYER && isPlayerOp(player))
		{
			info.claim(pinfo);
			return ClaimStatus.CLAIMED;
		}
		return compare;
	}
	
	public static UnclaimStatus unclaimChunk(ChunkPos chunkPos, String player)
	{
		ChunkInfo info = getChunkInfo(chunkPos);
		ClaimStatus compare = info.compare(chunkPos, player);
		
		if (compare == ClaimStatus.CLAIMED)
		{
			info.unclaim();
			return UnclaimStatus.UNCLAIMED;
		}
		else if (compare == ClaimStatus.CLAIMED_BY_OTHER_PLAYER || compare == ClaimStatus.ALLOWED)
		{
			if (isPlayerOp(player))
			{
				info.unclaim();
				return UnclaimStatus.UNCLAIMED_BY_OP;
			}
			return UnclaimStatus.CLAIMED_BY_OTHER_PLAYER;
		}
		return UnclaimStatus.NOT_CLAIMED;
	}
	
	public static int unclaimAllChunks(String player)
	{
		PlayerInfo pinfo = PowerTools.getPlayerInfo(player);
		int i = 0;
		for (ChunkInfo cinfo : chunkInfo.values())
		{
			if (cinfo.isOwner(pinfo))
			{
				i++;
				cinfo.unclaim();
			}
		}
		return 1;
	}
	
	public static boolean addAllowedPlayer(ChunkPos chunkPos, String owner, String player)
	{
		ChunkInfo info = getChunkInfo(chunkPos);
		ClaimStatus compare = info.compare(chunkPos, owner);
		if (compare == ClaimStatus.CLAIMED || isPlayerOp(owner))
		{
			info.addPlayer(player);
			return true;
		}
		return false;
	}
	
	public static boolean removeAllowedPlayer(ChunkPos chunkPos, String owner, String player)
	{
		ChunkInfo info = getChunkInfo(chunkPos);
		ClaimStatus compare = info.compare(chunkPos, owner);
		if (compare == ClaimStatus.CLAIMED || isPlayerOp(owner))
		{
			info.removePlayer(player);
		}
		return false;
	}
	
	public static boolean clearAllowedPlayers(ChunkPos chunkPos, String player)
	{
		ChunkInfo info = getChunkInfo(chunkPos);
		ClaimStatus compare = info.compare(chunkPos, player);
		if (compare == ClaimStatus.CLAIMED || isPlayerOp(player))
		{
			info.clearPlayers();
			return true;
		}
		return false;
	}
	
	public static boolean isPlayerOp(String playerName)
	{
		return PowerTools.server.getConfigurationManager().isPlayerOpped(playerName);
	}
}
