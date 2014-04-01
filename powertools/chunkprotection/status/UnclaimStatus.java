package powertools.chunkprotection.status;

public enum UnclaimStatus
{
	/** Unclaimed by player */
	UNCLAIMED,
	/** Unclaimed by operator */
	UNCLAIMED_BY_OP,
	/** The chunk was not claimed */
	NOT_CLAIMED,
	/** The chunk was claimed by someone else. */
	CLAIMED_BY_OTHER_PLAYER,
}
