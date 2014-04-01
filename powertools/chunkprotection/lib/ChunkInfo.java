package powertools.chunkprotection.lib;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import powertools.chunkprotection.status.ClaimStatus;
import powertools.main.PowerTools;
import powertools.main.lib.ChunkPos;
import powertools.main.lib.PlayerInfo;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

public class ChunkInfo
{
	public ChunkPos		chunkPos;
	private String		owner;
	private long			lastVisited;
	private Set<String>	players;
	
	public ChunkInfo()
	{
		this(new ChunkPos(), "");
	}
	
	public ChunkInfo(int dim, int x, int z, String owner)
	{
		this(new ChunkPos(dim, x, z), owner);
	}
	
	public ChunkInfo(ChunkPos chunkPos, String owner)
	{
		this.chunkPos = chunkPos;
		this.owner = owner;
		this.players = new HashSet();
		this.lastVisited = System.currentTimeMillis();
	}
	
	public boolean isOwner(PlayerInfo player)
	{
		return this.owner.equals(player.name);
	}
	
	public boolean isClaimed()
	{
		return !this.owner.isEmpty();
	}
	
	public String getOwner()
	{
		return this.owner;
	}
	
	public void claim(PlayerInfo player)
	{
		this.owner = player.name;
		player.chunksOwned++;
	}
	
	public void unclaim()
	{
		PowerTools.getPlayerInfo(this.owner).chunksOwned--;
		this.owner = "";
	}
	
	public Collection<String> getPlayers()
	{
		return this.players;
	}
	
	public void addPlayer(String player)
	{
		this.players.add(player);
	}
	
	public void removePlayer(String player)
	{
		this.players.remove(player);
	}
	
	public void clearPlayers()
	{
		this.players.clear();
	}
	
	public ClaimStatus compare(ChunkPos chunkPos, String owner)
	{
		return this.compare(chunkPos.dim, chunkPos.x, chunkPos.z, owner);
	}
	
	public ClaimStatus compare(int dim, int x, int z, String owner)
	{
		// c=0, chunk is not claimed
		// c=1, chunk is claimed but not owned
		// c=2, chunk is owned by player
		// c=3, chunk claimed, but player is allowed
		if (this.chunkPos.equals(dim, x, z) && !this.owner.isEmpty())
		{
			if (this.owner.equals(owner))
				return ClaimStatus.CLAIMED; // 2
			if (this.players.contains(owner))
				return ClaimStatus.ALLOWED; // 3
			return ClaimStatus.CLAIMED_BY_OTHER_PLAYER; // 1
		}
		return ClaimStatus.NOT_CLAIMED; // 0
	}
	
	public int getDaysSinceLastVisit()
	{
		long now = System.currentTimeMillis();
		long compare = now - this.lastVisited;
		int divider = 86400000; // 1000 * 60 * 60 * 24, ms/day
		int days = (int) compare / divider;
		return days;
	}
	
	public void setLastVisitNow()
	{
		this.lastVisited = System.currentTimeMillis();
	}
	
	public void writeToNBT(NBTTagCompound nbt)
	{
		this.chunkPos.writeToNBT(nbt);
		nbt.setString("Owner", this.owner);
		nbt.setLong("LastVisited", this.lastVisited);
		
		NBTTagList players = new NBTTagList();
		for (String s : this.players)
		{
			players.appendTag(new NBTTagString(s));
		}
		nbt.setTag("Players", players);
	}
	
	public void readFromNBT(NBTTagCompound nbt)
	{
		this.chunkPos.readFromNBT(nbt);
		this.owner = nbt.getString("Owner");
		this.lastVisited = nbt.getLong("LastVisited");
		
		NBTTagList players = nbt.getTagList("Players");
		for (int i = 0; i < players.tagCount(); i++)
		{
			NBTTagString s = (NBTTagString) players.tagAt(i);
			this.players.add(s.data);
		}
	}
}
