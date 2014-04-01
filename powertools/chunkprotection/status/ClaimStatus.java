package powertools.chunkprotection.status;

public enum ClaimStatus
{
	/** The chunk is not claimed. */
	NOT_CLAIMED,
	/** The chunk is claimed by someone else. */
	CLAIMED_BY_OTHER_PLAYER,
	/** The chunk is claimed by the player. */
	CLAIMED,
	/** The player doesn't own the chunk, but is allowed. */
	ALLOWED,
	/** No more allowed owners */
	FULL,
	/** The chunk was already claimed by the player */
	ALREADY_CLAIMED
}
