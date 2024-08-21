Fix `getRequiredXpLevelsToTp()` not returning the correct value, as it was called with the position of the player to the
position of the player, which made the return value always 1 *(basically the xp taken from the player when it teleported
to a home was always 1)*