package powertools.main.lib;

import java.util.ArrayList;
import java.util.List;

import powertools.main.PowerTools;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

public class PlayerInfo
{
	public String		name;
	public String		password;
	public boolean		isConnected;
	public boolean		isLoggedIn;
	public double		loginPosX;
	public double		loginPosY;
	public double		loginPosZ;
	
	public ChunkPos		chunkPos;
	public String		ownerMessage;
	public int			chunksOwned;
	public int			maxChunks;
	public boolean		borderInfo;
	
	public long			time;
	
	public ChatMode		chatMode;
	public List<String>	chatToPlayers;
	
	public PlayerInfo()
	{
		this("");
	}
	
	public PlayerInfo(String plName)
	{
		this.name = plName;
		this.password = null;
		
		this.isConnected = false;
		this.isLoggedIn = false;
		
		this.loginPosX = 0D;
		this.loginPosY = 0D;
		this.loginPosZ = 0D;
		
		this.chunkPos = new ChunkPos(0, 0, 0);
		this.ownerMessage = "";
		this.chunksOwned = 0;
		this.maxChunks = PowerTools.maxChunksPerPlayer;
		this.borderInfo = PowerTools.defaultBorderInfo;
		
		this.time = System.currentTimeMillis();
		
		this.chatMode = ChatMode.NORMAL;
		this.chatToPlayers = new ArrayList();
	}
	
	public boolean isRegistered()
	{
		return this.password != null;
	}
	
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setString("Name", this.name);
		if (this.password != null)
			nbt.setString("Password", this.password);
		
		nbt.setBoolean("IsConnected", this.isConnected);
		nbt.setBoolean("IsLoggedIn", this.isLoggedIn);
		
		nbt.setDouble("LoginPosX", this.loginPosX);
		nbt.setDouble("LoginPosY", this.loginPosY);
		nbt.setDouble("LoginPosZ", this.loginPosZ);
		
		NBTTagCompound chunkPos = new NBTTagCompound();
		this.chunkPos.writeToNBT(chunkPos);
		nbt.setTag("ChunkPos", chunkPos);
		
		nbt.setString("OwnerMessage", this.ownerMessage);
		nbt.setInteger("ChunksOwned", this.chunksOwned);
		nbt.setInteger("MaxChunks", this.maxChunks);
		nbt.setBoolean("BorderInfo", this.borderInfo);
		nbt.setByte("ChatMode", (byte) this.chatMode.ordinal());
		
		NBTTagList players = new NBTTagList();
		for (String player : this.chatToPlayers)
		{
			players.appendTag(new NBTTagString(player));
		}
		nbt.setTag("ChatToPlayers", players);
	}
	
	public void readFromNBT(NBTTagCompound nbt)
	{
		this.name = nbt.getString("Name");
		if (nbt.hasKey("Password"))
			this.password = nbt.getString("Password");
		
		this.isConnected = nbt.getBoolean("IsConnected");
		this.isLoggedIn = nbt.getBoolean("IsLoggedIn");
		
		this.loginPosX = nbt.getDouble("LoginPosX");
		this.loginPosY = nbt.getDouble("LoginPosY");
		this.loginPosZ = nbt.getDouble("LoginPosZ");
		
		NBTTagCompound chunkPos = nbt.getCompoundTag("ChunkPos");
		this.chunkPos.readFromNBT(chunkPos);
		
		this.ownerMessage = nbt.getString("OwnerMessage");
		this.chunksOwned = nbt.getInteger("ChunksOwned");
		this.maxChunks = nbt.getInteger("MaxChunks");
		this.borderInfo = nbt.getBoolean("BorderInfo");
		this.chatMode = ChatMode.values()[nbt.getByte("ChatMode")];
		
		NBTTagList players = nbt.getTagList("ChatToPlayers");
		for (int i = 0; i < players.tagCount(); i++)
		{
			NBTTagString s = (NBTTagString) players.tagAt(i);
			this.chatToPlayers.add(s.data);
		}
	}
}
