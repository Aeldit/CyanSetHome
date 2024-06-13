![banner](https://github.com/Aeldit/Aeldit/blob/main/banners/cyansh.png?raw=true)
<!-- modrinth_exclude.start -->
> ❌ I do NOT allow this mod to be ported on the Forge loader, nor to be uploaded on Curseforge
<!-- modrinth_exclude.end -->
[![fabric-api](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/requires/fabric-api_vector.svg)](https://modrinth.com/mod/fabric-api)
or
[![quilted-fabric-api](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/requires/quilted-fabric-api_vector.svg)](https://modrinth.com/mod/qsl)
[![cyanlib_badge_use](https://raw.githubusercontent.com/Aeldit/Aeldit/e84549f8cef529270bd41775357d577e1f71978a/images/cyanlib-cozy.svg)](https://modrinth.com/mod/cyanlib)

<details>
<summary>🎴 Show available versions</summary>

|   MC Version    | Up to date | Latest Version |
|:---------------:|:----------:|:--------------:|
|  1.19 - 1.19.2  |     ❌      |     0.1.4      |
|     1.19.3      |     ❌      |     0.1.0      |
|     1.19.4      |     ✅      |     latest     |
| 1.20.2 - 1.20.6 |     ✅      |     latest     |
|      1.21       |     ✅      |     latest     |

</details>

***

### 💲Features

1. Create homes and teleport to them (you can also : `remove, remove all, rename, list` them)

2. Trust players so they can teleport to your homes (you can also untrust them)

3. See and edit plenty of options, like the minimum OP level required to run a command or simply toggle a functionality

> The home-of commands require the player running the command to be trusted (for `/home-of` and `/get-homes-of`).
>
> All the commands that edit the homes of another player require the player running the command to be OP with
> level `MIN_OP_LVL_BYPASS` and the allowBypass option to be set to `true`

<details>
<summary>✅ List of commands (and their aliases)</summary>

|                        Command                        |                       Description                       |              Alias               |
|:-----------------------------------------------------:|:-------------------------------------------------------:|:--------------------------------:|
|                `/set-home <home_name>`                |           Creates a home with the given name            |        `/sh <home_name>`         |
|              `/remove-home <home_name>`               |                 Removes the given home                  |        `/rh <home_name>`         |
|                  `/remove-all-homes`                  |                 Removes all your homes                  |                ❌                 |
|       `rename-home <home_name> <new_home_name>`       |           Renames the home to the given name            |                ❌                 |
|                  `/home <home_name>`                  |             Teleports you to the given home             |         `/h <home_name>`         |
|                     `/get-homes`                      |           Displays all your homes in the chat           |              `/gh`               |
|                                                       |                                                         |                                  |
|                     `/home-trust`                     |        Adds the given player to your trust list         |                ❌                 |
|                    `/home-untrust`                    |      Removes the given player from your trust list      |                ❌                 |
|                `/get-trusting-players`                |    Displays in the chat every player that trusts you    |                ❌                 |
|                `/get-trusted-players`                 |    Displays in the chat every player that you trust     |                ❌                 |
|                                                       |                                                         |                                  |
|       `/set-home-of <player_name> <home_name>`        | Creates a home for the given player with the given name | `/sho <player_name> <home_name>` |
|      `/remove-home-of <player_name> <home_name>`      |          Removes the home of the given player           | `/rho <player_name> <home_name>` |
|         `/remove-all-homes-of <player_name>`          |        Removes all the homes of the given player        |                ❌                 |
| `rename-home-of <player_name> <home_name> <new_name>` | Renames the home of the given player to the given name  |                ❌                 |
|         `/home-of <player_name> <home_name>`          |      Teleports you to the home of the given player      | `/ho <player_name> <home_name>`  |
|             `/get-homes-of <player_name>`             |    Displays all the given player's homes in the chat    |       `/gho <player_name>`       |

</details>

***

> If you find any issue, please make sure to report it on github so I can fix it (both badges can be clicked on to
> follow the link)
>
> [![github_issues](https://img.shields.io/github/issues/Aeldit/CyanSetHome?color=red&style=for-the-badge&logo=github)](https://github.com/Aeldit/CyanSetHome/issues)
>
> If you have a suggestion, you can go on my discord server and create a post in 🗽-suggestions-forum
>
> [![discord_badge](https://img.shields.io/discord/750243612473819188?color=7289da&label=DISCORD&logo=discord&logoColor=7289da&style=for-the-badge)](https://discord.gg/PcYPpqzhKS)

### Check out the rest of my projects !

[![cyan_badge](https://raw.githubusercontent.com/Aeldit/Aeldit/bef8e5f6a837ee8c3479a2550e92c0ac028200f3/images/cyan-cozy-minimal.svg)](https://modrinth.com/mod/cyan)
[![ctms_badge](https://raw.githubusercontent.com/Aeldit/Aeldit/d668bc7cd71d654d2331905a5ad425283dedab94/images/ctms-cozy-minimal.svg)](https://modrinth.com/mod/ctm-selector)
[![cyanlib_badge](https://raw.githubusercontent.com/Aeldit/Aeldit/bef8e5f6a837ee8c3479a2550e92c0ac028200f3/images/cyanlib-cozy-minimal.svg)](https://modrinth.com/mod/cyanlib)
[![ctm_badge](https://raw.githubusercontent.com/Aeldit/Aeldit/e2fb5f7ffe92301f627540cebca28d9aa90c641d/images/ctm-cozy-minimal.svg)](https://modrinth.com/resourcepack/ctm-of-fabric)
[![ctm_faithful_badge](https://raw.githubusercontent.com/Aeldit/Aeldit/54529d9dbb33d35184f386269c889cef818e7e79/images/ctm-faithful-cozy-minimal.svg)](https://modrinth.com/resourcepack/ctm-faithful)
[![ctm_create_badge](https://raw.githubusercontent.com/Aeldit/Aeldit/54529d9dbb33d35184f386269c889cef818e7e79/images/ctm-create-cozy-minimal.svg)](https://modrinth.com/resourcepack/ctm-create)
[![dark_gui_badge](https://raw.githubusercontent.com/Aeldit/Aeldit/2f4a47b3752b28cbcd13c6d76c66a803d7fe1df5/images/dark-gui-cozy-minimal.svg)](https://modrinth.com/resourcepack/dark-smooth-gui)
[![light_gui_badge](https://raw.githubusercontent.com/Aeldit/Aeldit/2f4a47b3752b28cbcd13c6d76c66a803d7fe1df5/images/light-gui-cozy-minimal.svg)](https://modrinth.com/resourcepack/light-smooth-gui)
[![floating_texts_badge](https://raw.githubusercontent.com/Aeldit/Aeldit/c4163b0470c0d710ba2cd3314cd241b5669ef175/images/floating-texts-cozy-minimal.svg)](https://modrinth.com/datapack/floating-texts)
