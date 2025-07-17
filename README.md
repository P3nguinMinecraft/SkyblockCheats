# CURRENT RELEASE: v1.1.3
## About
#### Fabric 1.21.5 Mod for Hypixel Skyblock

## Usage
`/skyblockcheats` or alias `/sbc`

## Features
#### Grotto Search
`/sbc grottosearch [clear / list / scan / toggle]` <br>
Searches for Magenta Stained Glass and Panes (only in Fairy Grottos)
- Highlights the found blocks
- List out all highlighted blocks
- Togglable Search Loop with **server hop**
- Single click block search
- Ping when found
- Copiable coordinates

#### Uptime
`/sbc uptime` <br>
Displays Server Uptime
- Displayed in Day count, Hours, Minutes, Ticks

#### TPS
`/sbc tps` <br>
Displays Server TPS

#### Ghost Block
Makes ghost blocks without tools
- Turn on Ghost Block in config and hold keybind to create ghost blocks when you mine!

#### Automelody
Automatically completes melody harp song
- Very slightly ping dependent

#### Send Coords
`/sbc sendcoords` <br>
Sends your coords like in Patcher
- Make sure to change your chat channel before using

#### Look At
`/sbc look [block / pos] [x] [y], [z]` <br>
Quick util to face a certain position
- Look at blocks and specific positions

#### Play Sound
`/sbc playsound [sound] [volume] [pitch]` <br>
Plays a sound effect
- Useful to test out sounds such as for ping-on-found

#### Server Tracker
`/sbc serverhistory [list / clear]` <br>
Tracks servers and notifies when a server was visited recently
- Useful to avoid duplicate servers or to find a server you were on before

#### Autoclicker
Keybind Only <br>
Automatically clicks for you at a set CPS (Clicks Per Second)
- Toggled by keybind

#### Auto Impel
Completes impels automatically in Vampire Slayer
- Delays configurable

#### Beach Ball
Features for Bouncy Beach Ball in Year of the Seal
- Renders landing prediction
- Auto bounce
- Full auto bounce (macro)

#### Entity Rendering
`/sbc render [entity]`
Toggles specific entity rendering
- BlockDisplay
- ArmorStand
- Player (Other than yourself)
- FallingBlock

#### Timite Helper
`/sbc config set timite-helper [true/false]`
Automatically ages and mines timite that you look at
- Customizable Time Gun cooldown, anything below 15 works, 10 is recommended
  - `/sbc config set gun-cd`
- Prioritizes mining low "set" counts based on your inventory and preset amounts 
  - `/sbc config set youngite-ratio / timite-ratio / obsolite-ratio`
- Must have Chrono Pickaxe and Time Gun in your hotbar

#### Auto Ubik
`/sbc config set auto-ubik [true/false]`
Automatically steals when playing Split or Steal against a 100% Split opponent

#### Disable Block Breaking Cooldown
`/sbc config set disable-break-cooldown [true/false]`
Disables the 4gt cooldown between breaking blocks

#### Auto Visit
`/sbc config set auto-visit [true/false]`
Automatically confirms visit to island when you do /visit

#### Anvil Helper
`/sbc config set anvil-helper [true/false]`
Displays a book (toggle) in anvil screen that, when clicked, starts to combine books in your inventory with exactly matching enchants


## Config
`/sbc config list`: Lists all config options and values
`/sbc config set [name] [new value]`: Changes configs
`/sbc config reset [name / all]`: Resets config options to default

<b>delay</b>: Time in seconds between each server hop in the Grotto Search loop (0.0 = no delay) <br>
Default: 0.0 <br>
<b>rgba-block-color</b>: Color of the highlighted blocks in RGBA format (r(255)-g(255)-b(255)-a(1)) <br>
Default: 255-103-103-0.6 <br>
<b>solid-highlight</b>: Whether to use solid color for highlighted blocks (true/false) <br>
Default: true <br>
<b>outline-weight</b>: Thickness of the outline for highlighted blocks (I have no idea the unit) <br>
Default: 10.0 <br>
<b>ping-on-found</b>: Whether to play a sound when a block is found in Grotto Search (true/false) <br>
Default: true <br>
<b>ping-sound</b>: Sound to play when a block is found in Grotto Search (full or incomplete id - minecraft:block.anvil.land or block.anvil.land are both acceptable) <br>
Default: minecraft:block.anvil.land <br>
<b>ping-volume</b>: Volume of the ping sound (0.0 to 1.0) <br>
Default: 1.0 <br>
<b>ping-pitch</b>: Pitch of the ping sound (0.0 to 2.0) <br>
Default: 1.0 <br>
<b>warp-in</b>: Location to warp in when using the Grotto Search <br>
Default: ch <br>
<b> warp-out</b>: Location to warp out when using the Grotto Search <br>
Default: forge <br>
<b>filter-Y</b>: Whether to filter out blocks based on Y coordinate in Grotto Search (true/false) <br>
Default: false <br>
<b>filter-Y-max</b>: Maximum Y coordinate to include in Grotto Search if filter-Y is true <br>
Default: 64 <br>
<b>auto-melody</b>: Whether to automatically complete the melody harp song (true/false <br>
Default: false <br>
<b>ghost-block</b>: Whether to enable ghost blocks without tools (true/false) <br>
Default: false <br>
<b>max-log-time</b>: Maximum time in seconds to keep server logs for Server History <br>
Default: 600 <br>
<b>left-click-cps</b>: Clicks per second for Autoclicker when left-clicking (0.0 to 20.0)<br>
Default: 10.0 <br>
<b>right-click-cps</b>: Clicks per second for Autoclicker when right-clicking (0.0 to 20.0) <br>
Default: 10.0 <br>
<b>auto-impel</b>: Automatically completes impels for Vampire Slayer <br>
Default: false <br>
<b>impel-delay</b>: Delay for the pop-up before reacting and completing it (sec) <br>
Default: 0.3 <br>
<b>impel-rate</b>: Rate at which the mod attempts to impel, in case that it fails (sec) <br>
Default: 0.5 <br>
<b>beachball-predictor</b>: Enables all beach ball features and draws a predicted trajectory based on projectile models. Required for all beach ball features <br>
Default: true <br>
<b>auto-beachball</b>: Automatically bounces the beach ball, requires predictor to be on
Default: false <br>
<b>fullauto-beachball</b>: Fully automatically bounces the beach ball, requires auto-beachball. Hold beach ball items and go to -100 102 0 in Dungeon Hub using Etherwarp <br>
Default: false <br>
<b>lumie-hitboxes</b>: Changes Sea Pickle interaction boxes to 1x1x1, removes it for Kelp and Seagrass (true/false) <br>
Default: false <br>
<b>lumie-hideblocks</b>: Stops the rendering of Kelp and Seagrass blocks (true/false) <br>
Default: false<br>
<b>hide-leaves</b>: Stops the rendering of leaves for Fig and Mangrove trees and allows you to break through them (true/false) <br>
Default: false<br>
<b>block-blank-hit-sound</b>: Blocks the sound of hits that deal no damage (true/false) <br>
Default: false<br>
<b>timite-helper</b>: Automatically ages and mines timite that you look at (true/false) <br>
Default: false<br>
<b>timite-hitboxes</b>: Modifies timite hitboxes to be 1x1x1 stained glass block (true/false) <br>
Default: false<br>
<b>gun-cd</b>: Cooldown for Time Gun in ticks, anything below 15 works, 10 is recommended <br>
Default: 10<br>
<b>youngite-ratio</b>: Amount of Youngite in a "set"<br>
Default: 2<br>
<b>timite-ratio</b>: Amount of Timite in a "set"<br>
Default: 2<br>
<b>obsolite-ratio</b>: Amount of Obsolite in a "set"<br>
Default: 1<br>
<b>auto-ubik</b>: Automatically steals in Split or Steal against a 100% Split opponent (true/false) <br>
Default: false<br>
<b>disable-break-cooldown</b>: Disables the 4gt cooldown between breaking blocks (true/false) <br>
Default: false<br>
<b>auto-visit</b>: Automatically confirms visit to island when you do /visit (true/false) <br>
Default: false<br>


## Contact
DM `windows1267` on discord for any questions, issues, or suggestions