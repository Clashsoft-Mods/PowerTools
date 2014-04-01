package powertools.main.lib;

import powertools.main.PowerTools;

import net.minecraft.command.ICommandSender;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;

public class ChunkPos
{
	public int	dim;
	public int	x;
	public int	z;
	
	public ChunkPos()
	{
	}
	
	public ChunkPos(ICommandSender player)
	{
		this.dim = player.getEntityWorld().provider.dimensionId;
		ChunkCoordinates position = player.getPlayerCoordinates();
		this.x = PowerTools.toChunkCoordinate(position.posX);
		this.z = PowerTools.toChunkCoordinate(position.posZ);
	}
	
	public ChunkPos(int d, int x, int z)
	{
		this.dim = d;
		this.x = x;
		this.z = z;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + this.dim;
		result = prime * result + this.x;
		result = prime * result + this.z;
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		ChunkPos other = (ChunkPos) obj;
		return this.equals(other.dim, other.x, other.z);
	}
	
	public boolean equals(int dim, int x, int z)
	{
		return this.dim == dim && this.x == x && this.z == z;
	}
	
	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("D", this.dim);
		nbt.setInteger("X", this.x);
		nbt.setInteger("Z", this.z);
	}
	
	public void readFromNBT(NBTTagCompound nbt)
	{
		this.dim = nbt.getInteger("D");
		this.x = nbt.getInteger("X");
		this.z = nbt.getInteger("Z");
	}
}
